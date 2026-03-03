package com.mykino.service;

import com.mykino.dto.ContentDetailDto;
import com.mykino.entity.*;
import com.mykino.enums.CastRoleType;
import com.mykino.repository.ContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    public ContentDetailDto getContentDetail(Long contentId) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘텐츠입니다."));

        // 장르 목록
        List<String> genres = content.getContentGenres().stream()
                .map(cg -> cg.getGenre().getName())
                .collect(Collectors.toList());

        // 감독 목록
        List<ContentDetailDto.CastDto> directors = content.getContentCasts().stream()
                .filter(cc -> cc.getRoleType() == CastRoleType.DIRECTOR)
                .map(this::toCastDto)
                .collect(Collectors.toList());

        // 배우 목록 (순서대로)
        List<ContentDetailDto.CastDto> actors = content.getContentCasts().stream()
                .filter(cc -> cc.getRoleType() == CastRoleType.ACTOR)
                .sorted((a, b) -> {
                    if (a.getCastOrder() == null) return 1;
                    if (b.getCastOrder() == null) return -1;
                    return a.getCastOrder().compareTo(b.getCastOrder());
                })
                .map(this::toCastDto)
                .collect(Collectors.toList());

        // OTT 정보
        List<ContentDetailDto.OttInfoDto> ottInfos = content.getContentOtts().stream()
                .map(this::toOttInfoDto)
                .collect(Collectors.toList());

        return ContentDetailDto.builder()
                .id(content.getId())
                .title(content.getTitle())
                .titleEng(content.getTitleEng())
                .contentType(content.getContentType())
                .releaseYear(content.getReleaseYear())
                .runtime(content.getRuntime())
                .country(content.getCountry())
                .ageRating(content.getAgeRating())
                .synopsis(content.getSynopsis())
                .posterUrl(content.getPosterUrl())
                .backdropUrl(content.getBackdropUrl())
                .kinoScore(content.getKinoScore())
                .kinoColor(content.getKinoColor())
                .totalRatings(content.getTotalRatings())
                .genres(genres)
                .directors(directors)
                .actors(actors)
                .ottInfos(ottInfos)
                .build();
    }

    public List<Content> getAllContents() {
        return contentRepository.findAll();
    }

    private ContentDetailDto.CastDto toCastDto(ContentCast cc) {
        CastMember cm = cc.getCastMember();
        return ContentDetailDto.CastDto.builder()
                .id(cm.getId())
                .name(cm.getName())
                .nameEng(cm.getNameEng())
                .profileImage(cm.getProfileImage())
                .characterName(cc.getCharacterName())
                .build();
    }

    private ContentDetailDto.OttInfoDto toOttInfoDto(ContentOtt co) {
        OttPlatform ott = co.getOttPlatform();
        return ContentDetailDto.OttInfoDto.builder()
                .ottId(ott.getId())
                .ottName(ott.getName())
                .logoUrl(ott.getLogoUrl())
                .baseUrl(ott.getBaseUrl())
                .isStreaming(co.getIsStreaming())
                .buyPrice(co.getBuyPrice())
                .rentPrice(co.getRentPrice())
                .deepLink(co.getDeepLink())
                .build();
    }
}
