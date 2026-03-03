package com.mykino.dto;

import com.mykino.enums.ContentType;
import com.mykino.enums.TrafficColor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ContentDetailDto {

    private Long id;
    private String title;
    private String titleEng;
    private ContentType contentType;
    private Integer releaseYear;
    private Integer runtime;
    private String country;
    private String ageRating;
    private String synopsis;
    private String posterUrl;
    private String backdropUrl;
    private Double kinoScore;
    private TrafficColor kinoColor;
    private Integer totalRatings;

    private List<String> genres;
    private List<CastDto> directors;
    private List<CastDto> actors;
    private List<OttInfoDto> ottInfos;

    @Getter
    @Builder
    public static class CastDto {
        private Long id;
        private String name;
        private String nameEng;
        private String profileImage;
        private String characterName;
    }

    @Getter
    @Builder
    public static class OttInfoDto {
        private Long ottId;
        private String ottName;
        private String logoUrl;
        private String baseUrl;
        private Boolean isStreaming;
        private Integer buyPrice;
        private Integer rentPrice;
        private String deepLink;
    }
}
