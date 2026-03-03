package com.mykino.controller;

import com.mykino.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class AdminController {

    private final TmdbService tmdbService;

    @GetMapping("/admin/tmdb")
    public String tmdbPage() {
        return "admin/tmdb";
    }

    @PostMapping("/api/admin/tmdb/sync-popular")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> syncPopular(
            @RequestParam(defaultValue = "1") int page) {
        Map<String, Object> result = tmdbService.syncPopularMovies(page);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/admin/tmdb/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchAndSync(
            @RequestParam String keyword) {
        Map<String, Object> result = tmdbService.searchAndSync(keyword);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/api/admin/tmdb/sync/{tmdbId}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> syncSingle(@PathVariable Long tmdbId) {
        try {
            tmdbService.syncSingleMovie(tmdbId);
            return ResponseEntity.ok(Map.of("success", true, "message", "동기화 완료"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
