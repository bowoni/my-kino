package com.mykino.entity;

import com.mykino.enums.TrafficColor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "ratings",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "content_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Rating extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TrafficColor trafficColor;

    private Double score;

    @Column(length = 200)
    private String comment;

    @Builder
    public Rating(User user, Content content, TrafficColor trafficColor,
                  Double score, String comment) {
        this.user = user;
        this.content = content;
        this.trafficColor = trafficColor;
        this.score = score;
        this.comment = comment;
    }

    public void update(TrafficColor trafficColor, Double score, String comment) {
        this.trafficColor = trafficColor;
        this.score = score;
        this.comment = comment;
    }
}
