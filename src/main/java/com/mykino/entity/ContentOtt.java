package com.mykino.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "content_ott")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContentOtt extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ott_platform_id", nullable = false)
    private OttPlatform ottPlatform;

    @Column(nullable = false)
    private Boolean isStreaming = false;

    private Integer buyPrice;

    private Integer rentPrice;

    private String deepLink;

    private LocalDate availableFrom;

    private LocalDate availableUntil;

    @Builder
    public ContentOtt(Content content, OttPlatform ottPlatform, Boolean isStreaming,
                      Integer buyPrice, Integer rentPrice, String deepLink,
                      LocalDate availableFrom, LocalDate availableUntil) {
        this.content = content;
        this.ottPlatform = ottPlatform;
        this.isStreaming = isStreaming != null ? isStreaming : false;
        this.buyPrice = buyPrice;
        this.rentPrice = rentPrice;
        this.deepLink = deepLink;
        this.availableFrom = availableFrom;
        this.availableUntil = availableUntil;
    }
}
