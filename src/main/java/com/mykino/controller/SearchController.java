package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import com.mykino.service.SearchService;
import com.mykino.service.TmdbService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;
    private final TmdbService tmdbService;

    /**
     * 검색 결과 페이지 (TMDB API 직접 검색)
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q,
                         @RequestParam(defaultValue = "0") int page,
                         @AuthenticationPrincipal CustomUserDetails userDetails,
                         Model model) {

        if (q != null && !q.trim().isEmpty()) {
            String keyword = q.trim();
            Map<String, Object> results = tmdbService.searchMulti(keyword, page);

            model.addAttribute("keyword", keyword);
            model.addAttribute("results", results);

            // 검색 기록 저장
            if (userDetails != null) {
                searchService.saveSearchHistory(userDetails.getUser(), keyword);
            }
        }

        model.addAttribute("q", q);
        return "search/result";
    }

    /**
     * 자동완성 API (TMDB API 직접 검색)
     */
    @GetMapping("/api/public/autocomplete")
    @ResponseBody
    @SuppressWarnings("unchecked")
    public ResponseEntity<List<Map<String, Object>>> autocomplete(
            @RequestParam String q) {

        if (q == null || q.trim().length() < 1) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        Map<String, Object> searchResult = tmdbService.searchMulti(q.trim(), 0);
        List<Map<String, Object>> items = (List<Map<String, Object>>) searchResult.get("content");

        // 자동완성은 상위 7개만
        if (items.size() > 7) {
            items = items.subList(0, 7);
        }

        return ResponseEntity.ok(items);
    }
}
