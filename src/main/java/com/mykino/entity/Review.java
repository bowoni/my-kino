package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(length = 100)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Column(nullable = false)
    private Boolean hasSpoiler = false;

    @Column(nullable = false)
    private Boolean isPublic = true;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Builder
    public Review(User user, Content content, String title, String body,
                  Boolean hasSpoiler, Boolean isPublic) {
        this.user = user;
        this.content = content;
        this.title = title;
        this.body = body;
        this.hasSpoiler = hasSpoiler != null ? hasSpoiler : false;
        this.isPublic = isPublic != null ? isPublic : true;
    }

    public void update(String title, String body, Boolean hasSpoiler, Boolean isPublic) {
        this.title = title;
        this.body = body;
        this.hasSpoiler = hasSpoiler;
        this.isPublic = isPublic;
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
