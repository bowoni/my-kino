package com.mykino.config;

import com.mykino.repository.ContentRepository;
import com.mykino.service.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ContentRepository contentRepository;
    private final TmdbService tmdbService;

    @Override
    public void run(String... args) {
        if (contentRepository.count() == 0) {
            log.info("콘텐츠가 비어있습니다. TMDB에서 인기 영화를 자동으로 가져옵니다...");
            try {
                Map<String, Object> result = tmdbService.syncPopularMovies(1);
                log.info("TMDB 자동 동기화 완료 - 저장: {}편", result.get("saved"));
            } catch (Exception e) {
                log.warn("TMDB 자동 동기화 실패: {}", e.getMessage());
            }
        }
    }
}
