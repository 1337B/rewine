// =============================================================================
// Rewine - Jenkins CI/CD Pipeline
// =============================================================================
// Branch Strategy:
//   - development       -> Deploy to DEV environment
//   - release/*         -> Deploy to UAT environment
//   - main (tag v*)     -> Deploy to PROD environment (manual approval required)
// =============================================================================

pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: maven
    image: maven:3.9-eclipse-temurin-21-alpine
    command:
    - cat
    tty: true
    resources:
      requests:
        memory: "2Gi"
        cpu: "1"
      limits:
        memory: "4Gi"
        cpu: "2"
  - name: docker
    image: docker:24-dind
    securityContext:
      privileged: true
    volumeMounts:
    - name: docker-socket
      mountPath: /var/run/docker.sock
  - name: kubectl
    image: bitnami/kubectl:latest
    command:
    - cat
    tty: true
  volumes:
  - name: docker-socket
    hostPath:
      path: /var/run/docker.sock
'''
        }
    }

    environment {
        // Application Info
        APP_NAME = 'rewine-backend'
        APP_VERSION = readMavenPom().getVersion()

        // Docker Registry
        DOCKER_REGISTRY = credentials('docker-registry-url')
        DOCKER_CREDENTIALS = credentials('docker-registry-credentials')

        // SonarQube
        SONAR_HOST_URL = credentials('sonarqube-url')
        SONAR_TOKEN = credentials('sonarqube-token')

        // Kubernetes
        K8S_NAMESPACE_DEV = 'rewine-dev'
        K8S_NAMESPACE_UAT = 'rewine-uat'
        K8S_NAMESPACE_PROD = 'rewine-prod'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds()
        timestamps()
    }

    stages {
        // =====================================================================
        // Stage: Checkout
        // =====================================================================
        stage('Checkout') {
            steps {
                checkout scm
                script {
                    env.GIT_COMMIT_SHORT = sh(
                        script: 'git rev-parse --short HEAD',
                        returnStdout: true
                    ).trim()
                    env.GIT_BRANCH_NAME = env.BRANCH_NAME ?:
                        sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()

                    echo "Building ${env.APP_NAME} version ${env.APP_VERSION}"
                    echo "Branch: ${env.GIT_BRANCH_NAME}, Commit: ${env.GIT_COMMIT_SHORT}"
                }
            }
        }

        // =====================================================================
        // Stage: Code Quality - Checkstyle
        // =====================================================================
        stage('Checkstyle') {
            steps {
                container('maven') {
                    dir('backend') {
                        sh 'mvn checkstyle:check -B'
                    }
                }
            }
            post {
                always {
                    recordIssues(
                        enabledForFailure: true,
                        tools: [checkStyle(pattern: '**/checkstyle-result.xml')]
                    )
                }
            }
        }

        // =====================================================================
        // Stage: Unit Tests
        // =====================================================================
        stage('Unit Tests') {
            steps {
                container('maven') {
                    dir('backend') {
                        sh 'mvn test -B -Dspring.profiles.active=test'
                    }
                }
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        // =====================================================================
        // Stage: Integration Tests
        // =====================================================================
        stage('Integration Tests') {
            when {
                anyOf {
                    branch 'development'
                    branch 'release/*'
                    branch 'main'
                }
            }
            steps {
                container('maven') {
                    dir('backend') {
                        sh 'mvn verify -B -Dspring.profiles.active=integration -DskipUnitTests'
                    }
                }
            }
            post {
                always {
                    junit '**/target/failsafe-reports/*.xml'
                }
            }
        }

        // =====================================================================
        // Stage: Code Coverage - JaCoCo
        // =====================================================================
        stage('Code Coverage') {
            steps {
                container('maven') {
                    dir('backend') {
                        sh 'mvn jacoco:report -B'
                    }
                }
            }
            post {
                always {
                    jacoco(
                        execPattern: '**/target/jacoco.exec',
                        classPattern: '**/target/classes',
                        sourcePattern: '**/src/main/java',
                        exclusionPattern: '**/test/**'
                    )
                }
            }
        }

        // =====================================================================
        // Stage: SonarQube Analysis
        // =====================================================================
        stage('SonarQube Analysis') {
            when {
                anyOf {
                    branch 'development'
                    branch 'release/*'
                    branch 'main'
                }
            }
            steps {
                container('maven') {
                    dir('backend') {
                        withSonarQubeEnv('SonarQube') {
                            sh '''
                                mvn sonar:sonar -B \
                                    -Dsonar.projectKey=${APP_NAME} \
                                    -Dsonar.projectName=${APP_NAME} \
                                    -Dsonar.projectVersion=${APP_VERSION} \
                                    -Dsonar.host.url=${SONAR_HOST_URL} \
                                    -Dsonar.login=${SONAR_TOKEN}
                            '''
                        }
                    }
                }
            }
        }

        // =====================================================================
        // Stage: Quality Gate
        // =====================================================================
        stage('Quality Gate') {
            when {
                anyOf {
                    branch 'development'
                    branch 'release/*'
                    branch 'main'
                }
            }
            steps {
                timeout(time: 5, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        // =====================================================================
        // Stage: Build Application
        // =====================================================================
        stage('Build') {
            steps {
                container('maven') {
                    dir('backend') {
                        sh 'mvn clean package -B -DskipTests'
                    }
                }
            }
            post {
                success {
                    archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
                }
            }
        }

        // =====================================================================
        // Stage: Build Docker Image
        // =====================================================================
        stage('Build Docker Image') {
            when {
                anyOf {
                    branch 'development'
                    branch 'release/*'
                    branch 'main'
                }
            }
            steps {
                container('docker') {
                    dir('backend') {
                        script {
                            def imageTag = getImageTag()
                            sh """
                                docker build \
                                    -t ${DOCKER_REGISTRY}/${APP_NAME}:${imageTag} \
                                    -t ${DOCKER_REGISTRY}/${APP_NAME}:${GIT_COMMIT_SHORT} \
                                    .
                            """
                            env.DOCKER_IMAGE_TAG = imageTag
                        }
                    }
                }
            }
        }

        // =====================================================================
        // Stage: Push Docker Image
        // =====================================================================
        stage('Push Docker Image') {
            when {
                anyOf {
                    branch 'development'
                    branch 'release/*'
                    branch 'main'
                }
            }
            steps {
                container('docker') {
                    script {
                        sh """
                            echo ${DOCKER_CREDENTIALS_PSW} | docker login ${DOCKER_REGISTRY} \
                                -u ${DOCKER_CREDENTIALS_USR} --password-stdin
                            docker push ${DOCKER_REGISTRY}/${APP_NAME}:${DOCKER_IMAGE_TAG}
                            docker push ${DOCKER_REGISTRY}/${APP_NAME}:${GIT_COMMIT_SHORT}
                        """
                    }
                }
            }
        }

        // =====================================================================
        // Stage: Deploy to DEV
        // =====================================================================
        stage('Deploy to DEV') {
            when {
                branch 'development'
            }
            steps {
                container('kubectl') {
                    script {
                        deployToEnvironment('dev', K8S_NAMESPACE_DEV)
                    }
                }
            }
            post {
                success {
                    echo "Successfully deployed to DEV environment"
                }
            }
        }

        // =====================================================================
        // Stage: Deploy to UAT
        // =====================================================================
        stage('Deploy to UAT') {
            when {
                branch 'release/*'
            }
            steps {
                container('kubectl') {
                    script {
                        deployToEnvironment('uat', K8S_NAMESPACE_UAT)
                    }
                }
            }
            post {
                success {
                    echo "Successfully deployed to UAT environment"
                }
            }
        }

        // =====================================================================
        // Stage: Deploy to PROD (Manual Approval)
        // =====================================================================
        stage('Deploy to PROD') {
            when {
                allOf {
                    branch 'main'
                    tag pattern: "v\\d+\\.\\d+\\.\\d+", comparator: "REGEXP"
                }
            }
            stages {
                stage('Approval') {
                    steps {
                        script {
                            def approver = input(
                                message: 'Deploy to Production?',
                                ok: 'Approve',
                                submitterParameter: 'approver',
                                parameters: [
                                    choice(
                                        name: 'CONFIRM',
                                        choices: ['Yes', 'No'],
                                        description: 'Confirm deployment to Production'
                                    )
                                ]
                            )

                            if (approver.CONFIRM != 'Yes') {
                                error("Production deployment was not approved")
                            }

                            echo "Production deployment approved by: ${approver.approver}"
                        }
                    }
                }

                stage('Execute PROD Deploy') {
                    steps {
                        container('kubectl') {
                            script {
                                deployToEnvironment('prod', K8S_NAMESPACE_PROD)
                            }
                        }
                    }
                }
            }
            post {
                success {
                    echo "Successfully deployed to PROD environment"
                    // Notify stakeholders
                    // slackSend(color: 'good', message: "Production deployment successful: ${APP_NAME}:${DOCKER_IMAGE_TAG}")
                }
            }
        }
    }

    post {
        always {
            cleanWs()
        }
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed!"
            // Notify on failure
            // slackSend(color: 'danger', message: "Pipeline failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}")
        }
    }
}

// =============================================================================
// Helper Functions
// =============================================================================

/**
 * Get Docker image tag based on branch name
 */
def getImageTag() {
    def branch = env.GIT_BRANCH_NAME

    if (branch == 'development') {
        return "dev-${env.GIT_COMMIT_SHORT}"
    } else if (branch.startsWith('release/')) {
        def version = branch.replace('release/', '')
        return "rc-${version}-${env.GIT_COMMIT_SHORT}"
    } else if (branch == 'main') {
        // Use tag if available, otherwise use commit
        def tag = sh(script: 'git describe --tags --exact-match 2>/dev/null || echo ""', returnStdout: true).trim()
        return tag ?: "main-${env.GIT_COMMIT_SHORT}"
    }

    return env.GIT_COMMIT_SHORT
}

/**
 * Deploy to specified environment using Kustomize
 */
def deployToEnvironment(String environment, String namespace) {
    echo "Deploying to ${environment} environment (namespace: ${namespace})"

    withKubeConfig(credentialsId: "k8s-${environment}-credentials") {
        sh """
            cd infra/k8s/overlays/${environment}

            # Update image tag in kustomization
            kustomize edit set image ${APP_NAME}=${DOCKER_REGISTRY}/${APP_NAME}:${DOCKER_IMAGE_TAG}

            # Apply manifests
            kustomize build . | kubectl apply -f - -n ${namespace}

            # Wait for rollout
            kubectl rollout status deployment/${environment}-rewine-backend -n ${namespace} --timeout=300s
        """
    }
}

