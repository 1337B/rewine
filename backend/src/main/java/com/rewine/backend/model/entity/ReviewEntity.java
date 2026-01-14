package com.rewine.backend.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Review entity representing a user's review of a wine.
 */
@Entity
@Table(name = "reviews", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "wine_id"}, name = "uq_reviews_user_wine")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"wine", "user", "likes", "comments"})
@ToString(exclude = {"wine", "user", "likes", "comments"})
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wine_id", nullable = false)
    private WineEntity wine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Column(nullable = false, precision = 2, scale = 1)
    private BigDecimal rating;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean isVerified = false;

    @Column(name = "helpful_count")
    @Builder.Default
    private Integer helpfulCount = 0;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewLikeEntity> likes = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ReviewCommentEntity> comments = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    /**
     * Gets the count of likes.
     *
     * @return likes count
     */
    public int getLikesCount() {
        return Objects.nonNull(likes) ? likes.size() : 0;
    }

    /**
     * Gets the count of comments.
     *
     * @return comments count
     */
    public int getCommentsCount() {
        return Objects.nonNull(comments) ? comments.size() : 0;
    }

    /**
     * Gets the username of the reviewer safely.
     *
     * @return username or null
     */
    public String getUsername() {
        return Objects.nonNull(user) ? user.getUsername() : null;
    }

    /**
     * Gets the wine ID safely.
     *
     * @return wine ID or null
     */
    public UUID getWineId() {
        return Objects.nonNull(wine) ? wine.getId() : null;
    }

    /**
     * Gets the user ID safely.
     *
     * @return user ID or null
     */
    public UUID getUserId() {
        return Objects.nonNull(user) ? user.getId() : null;
    }
}

