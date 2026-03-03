package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "user_ott_subscriptions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "ott_platform_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserOttSubscription extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ott_platform_id", nullable = false)
    private OttPlatform ottPlatform;

    public UserOttSubscription(User user, OttPlatform ottPlatform) {
        this.user = user;
        this.ottPlatform = ottPlatform;
    }
}
