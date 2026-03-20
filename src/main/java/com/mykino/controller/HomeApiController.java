package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import com.mykino.entity.Review;
import com.mykino.entity.Watchlist;
import com.mykino.enums.WatchStatus;
import com.mykino.service.ReviewService;
import com.mykino.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class HomeApiController {

    private final WatchlistService watchlistService;
    private final ReviewService reviewService;

    @GetMapping("/api/home/my-watchlist")
    public ResponseEntity<List<Map<String, Object>>> myWatchlist(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        List<Watchlist> items = watchlistService.getMyWatchlist(
                userDetails.getUser().getId(), WatchStatus.WANT_TO_WATCH, 20);

        List<Map<String, Object>> result = items.stream().map(w -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("contentId", w.getContent().getId());
            map.put("title", w.getContent().getTitle());
            map.put("posterUrl", w.getContent().getPosterUrl());
            map.put("contentType", w.getContent().getContentType().getDisplayName());
            map.put("releaseYear", w.getContent().getReleaseYear());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/public/home/latest-reviews")
    public ResponseEntity<List<Map<String, Object>>> latestReviews() {
        List<Review> reviews = reviewService.getLatestReviews(6);

        List<Map<String, Object>> result = reviews.stream().map(r -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", r.getId());
            map.put("body", r.getBody());
            map.put("nickname", r.getUser().getNickname());
            map.put("contentTitle", r.getContent().getTitle());
            map.put("contentId", r.getContent().getId());
            map.put("posterUrl", r.getContent().getPosterUrl());
            map.put("hasSpoiler", r.getHasSpoiler());
            map.put("createdAt", r.getCreatedAt().toString());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}
