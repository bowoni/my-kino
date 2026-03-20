package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import com.mykino.entity.Review;
import com.mykino.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.util.HtmlUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/content/{contentId}")
    public ResponseEntity<Map<String, Object>> getReviews(
            @PathVariable Long contentId,
            @RequestParam(defaultValue = "0") int page) {

        Page<Review> reviews = reviewService.getReviews(contentId,
                PageRequest.of(page, 10, Sort.by(Sort.Direction.DESC, "createdAt")));

        List<Map<String, Object>> items = reviews.getContent().stream()
                .map(this::toMap)
                .collect(Collectors.toList());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", items);
        result.put("totalElements", reviews.getTotalElements());
        result.put("totalPages", reviews.getTotalPages());
        result.put("number", reviews.getNumber());
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Object> body) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        Long contentId = ((Number) body.get("contentId")).longValue();
        String title = (String) body.get("title");
        String reviewBody = (String) body.get("body");
        Boolean hasSpoiler = body.get("hasSpoiler") != null
                ? (Boolean) body.get("hasSpoiler") : false;

        Review review = reviewService.createReview(userDetails.getUser(),
                contentId, title, reviewBody, hasSpoiler);

        return ResponseEntity.ok(toMap(review));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        reviewService.deleteReview(reviewId, userDetails.getUser().getId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long reviewId) {

        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }

        boolean liked = reviewService.toggleLike(userDetails.getUser().getId(), reviewId);

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("liked", liked);
        return ResponseEntity.ok(result);
    }

    private Map<String, Object> toMap(Review review) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", review.getId());
        map.put("nickname", HtmlUtils.htmlEscape(review.getUser().getNickname()));
        map.put("title", review.getTitle() != null ? HtmlUtils.htmlEscape(review.getTitle()) : null);
        map.put("body", HtmlUtils.htmlEscape(review.getBody()));
        map.put("hasSpoiler", review.getHasSpoiler());
        map.put("likeCount", review.getLikeCount());
        map.put("createdAt", review.getCreatedAt().toString());
        return map;
    }
}
