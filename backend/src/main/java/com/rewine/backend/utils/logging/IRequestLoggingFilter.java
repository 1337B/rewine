package com.rewine.backend.utils.logging;

import jakarta.servlet.Filter;

/**
 * Interface for request logging filter.
 * Provides request correlation through MDC (Mapped Diagnostic Context).
 */
public interface IRequestLoggingFilter extends Filter {

    /**
     * HTTP header name for request ID.
     */
    String REQUEST_ID_HEADER = "X-Request-Id";

    /**
     * MDC key for request ID.
     */
    String MDC_REQUEST_ID = "requestId";

    /**
     * MDC key for user ID.
     */
    String MDC_USER_ID = "userId";

    /**
     * MDC key for request path.
     */
    String MDC_PATH = "path";

    /**
     * MDC key for HTTP method.
     */
    String MDC_METHOD = "method";

    /**
     * MDC key for response status code.
     */
    String MDC_STATUS_CODE = "statusCode";

    /**
     * MDC key for elapsed time.
     */
    String MDC_ELAPSED_TIME = "elapsedTime";

    /**
     * Generates a new unique request ID.
     *
     * @return A unique request ID string
     */
    String generateRequestId();
}

