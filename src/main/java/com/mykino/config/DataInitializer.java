package com.mykino.config;

import com.mykino.entity.OttPlatform;
import com.mykino.repository.ContentRepository;
import com.mykino.repository.OttPlatformRepository;
import com.mykino.service.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ContentRepository contentRepository;
    private final OttPlatformRepository ottPlatformRepository;
    private final TmdbService tmdbService;

    @Value("${mykino.auto-sync:true}")
    private boolean autoSync;

    @Override
    @Transactional
    public void run(String... args) {
        // OTT 로고를 TMDB에서 가져와 업데이트
        updateOttLogos();

        if (autoSync && contentRepository.count() == 0) {
            log.info("콘텐츠가 비어있습니다. TMDB에서 인기 영화를 자동으로 가져옵니다...");
            try {
                Map<String, Object> result = tmdbService.syncPopularMovies(1);
                log.info("TMDB 자동 동기화 완료 - 저장: {}편", result.get("saved"));
            } catch (Exception e) {
                log.warn("TMDB 자동 동기화 실패: {}", e.getMessage());
            }
        }
    }

    private void updateOttLogos() {
        try {
            Map<Integer, String> logoMap = tmdbService.getWatchProviderLogos();
            List<OttPlatform> platforms = ottPlatformRepository.findAll();
            for (OttPlatform ott : platforms) {
                if (ott.getTmdbProviderId() != null) {
                    String tmdbLogo = logoMap.get(ott.getTmdbProviderId());
                    if (tmdbLogo != null) {
                        ott.updateLogoUrl(tmdbLogo);
                    }
                }
            }
            log.info("OTT 로고 업데이트 완료");
        } catch (Exception e) {
            log.warn("OTT 로고 업데이트 실패: {}", e.getMessage());
        }
    }
}
