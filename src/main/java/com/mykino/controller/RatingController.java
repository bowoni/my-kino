package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import com.mykino.entity.Rating;
import com.mykino.enums.TrafficColor;
import com.mykino.service.RatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/rating")
@RequiredArgsConstructor
public class RatingController {

    private final RatingService ratingService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> rate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Object> body) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long contentId = ((Number) body.get("contentId")).longValue();
        String colorStr = (String) body.get("trafficColor");
        TrafficColor trafficColor = TrafficColor.valueOf(colorStr);
        Double score = body.get("score") != null ? ((Number) body.get("score")).doubleValue() : null;
        String comment = (String) body.get("comment");

        Rating rating = ratingService.rate(userDetails.getUser(), contentId,
                trafficColor, score, comment);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id", rating.getId());
        result.put("trafficColor", rating.getTrafficColor().name());
        result.put("score", rating.getScore());
        result.put("comment", rating.getComment());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteRating(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long contentId) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        ratingService.deleteRating(userDetails.getUser().getId(), contentId);
        return ResponseEntity.ok().build();
    }
}
