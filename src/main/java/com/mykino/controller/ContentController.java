package com.mykino.controller;

import com.mykino.config.CustomUserDetails;
import com.mykino.dto.ContentDetailDto;
import com.mykino.entity.Content;
import com.mykino.entity.Rating;
import com.mykino.enums.ContentType;
import com.mykino.repository.ContentRepository;
import com.mykino.service.ContentService;
import com.mykino.service.RatingService;
import com.mykino.service.TmdbService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequestMapping("/content")
@RequiredArgsConstructor
@Slf4j
public class ContentController {

    private final ContentService contentService;
    private final ContentRepository contentRepository;
    private final TmdbService tmdbService;
    private final RatingService ratingService;

    @GetMapping("/{id}")
    public String contentDetail(@PathVariable Long id,
                                @AuthenticationPrincipal CustomUserDetails userDetails,
                                Model model) {
        ContentDetailDto content = contentService.getContentDetail(id);
        model.addAttribute("content", content);

        // 로그인한 사용자의 기존 평가 정보
        if (userDetails != null) {
            Optional<Rating> myRating = ratingService.getUserRating(
                    userDetails.getUser().getId(), id);
            myRating.ifPresent(r -> {
                model.addAttribute("myTrafficColor", r.getTrafficColor().name());
                model.addAttribute("myScore", r.getScore());
                model.addAttribute("myComment", r.getComment());
            });
        }

        return "content/detail";
    }

    @GetMapping("/tmdb/{tmdbId}")
    public String contentByTmdb(@PathVariable Long tmdbId,
                                @RequestParam(defaultValue = "movie") String type) {
        ContentType contentType = "tv".equals(type) ? ContentType.DRAMA : ContentType.MOVIE;

        Optional<Content> existing = contentRepository.findByTmdbIdAndContentType(tmdbId, contentType);
        if (existing.isPresent()) {
            return "redirect:/content/" + existing.get().getId();
        }

        try {
            Content content;
            if ("tv".equals(type)) {
                content = tmdbService.syncSingleTv(tmdbId);
            } else {
                content = tmdbService.syncSingleMovie(tmdbId);
            }
            return "redirect:/content/" + content.getId();
        } catch (Exception e) {
            log.error("TMDB 동기화 실패 [tmdbId={}, type={}]: {}", tmdbId, type, e.getMessage());
            return "redirect:/search";
        }
    }
}
