package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import com.mykino.enums.WatchStatus;
import com.mykino.service.WatchlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/watchlist")
@RequiredArgsConstructor
public class WatchlistController {

    private final WatchlistService watchlistService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> toggle(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Object> body) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long contentId = ((Number) body.get("contentId")).longValue();
        WatchStatus status = WatchStatus.valueOf((String) body.get("status"));

        Optional<WatchStatus> result = watchlistService.toggleStatus(
                userDetails.getUser(), contentId, status);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", result.orElse(null));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{contentId}")
    public ResponseEntity<Map<String, Object>> getStatus(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long contentId) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Optional<WatchStatus> status = watchlistService.getWatchStatus(
                userDetails.getUser().getId(), contentId);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", status.orElse(null));
        return ResponseEntity.ok(response);
    }
}
