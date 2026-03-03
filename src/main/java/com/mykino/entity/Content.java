package com.mykino.entity;

import com.mykino.enums.ContentType;
import com.mykino.enums.TrafficColor;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contents", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"tmdbId", "contentType"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Content extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 200)
    private String titleEng;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ContentType contentType;

    private Integer releaseYear;

    private Integer runtime;

    @Column(length = 50)
    private String country;

    @Column(length = 20)
    private String ageRating;

    @Column(columnDefinition = "TEXT")
    private String synopsis;

    private String posterUrl;

    private String backdropUrl;

    private Long tmdbId;

    private Double voteAverage;

    private Double kinoScore;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private TrafficColor kinoColor;

    @Column(nullable = false)
    private Integer totalRatings = 0;

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentGenre> contentGenres = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentOtt> contentOtts = new ArrayList<>();

    @OneToMany(mappedBy = "content", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ContentCast> contentCasts = new ArrayList<>();

    @Builder
    public Content(String title, String titleEng, ContentType contentType,
                   Integer releaseYear, Integer runtime, String country,
                   String ageRating, String synopsis, String posterUrl,
                   String backdropUrl, Long tmdbId, Double voteAverage) {
        this.title = title;
        this.titleEng = titleEng;
        this.contentType = contentType;
        this.releaseYear = releaseYear;
        this.runtime = runtime;
        this.country = country;
        this.ageRating = ageRating;
        this.synopsis = synopsis;
        this.posterUrl = posterUrl;
        this.backdropUrl = backdropUrl;
        this.tmdbId = tmdbId;
        this.voteAverage = voteAverage;
    }

    public void updateKinoScore(Double score, TrafficColor color, Integer totalRatings) {
        this.kinoScore = score;
        this.kinoColor = color;
        this.totalRatings = totalRatings;
    }
}
