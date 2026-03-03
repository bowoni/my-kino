package com.mykino.service;

import com.mykino.entity.Content;
import com.mykino.entity.Rating;
import com.mykino.entity.User;
import com.mykino.enums.TrafficColor;
import com.mykino.repository.ContentRepository;
import com.mykino.repository.RatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RatingService {

    private final RatingRepository ratingRepository;
    private final ContentRepository contentRepository;

    public Optional<Rating> getUserRating(Long userId, Long contentId) {
        return ratingRepository.findByUserIdAndContentId(userId, contentId);
    }

    @Transactional
    public Rating rate(User user, Long contentId, TrafficColor trafficColor,
                       Double score, String comment) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘텐츠입니다."));

        Rating rating = ratingRepository.findByUserIdAndContentId(user.getId(), contentId)
                .map(existing -> {
                    existing.update(trafficColor, score, comment);
                    return existing;
                })
                .orElseGet(() -> Rating.builder()
                        .user(user)
                        .content(content)
                        .trafficColor(trafficColor)
                        .score(score)
                        .comment(comment)
                        .build());

        ratingRepository.save(rating);

        // 콘텐츠 평균 점수 업데이트
        updateContentScore(content);

        return rating;
    }

    @Transactional
    public void deleteRating(Long userId, Long contentId) {
        Rating rating = ratingRepository.findByUserIdAndContentId(userId, contentId)
                .orElseThrow(() -> new IllegalArgumentException("평가를 찾을 수 없습니다."));

        Content content = rating.getContent();
        ratingRepository.delete(rating);

        updateContentScore(content);
    }

    private void updateContentScore(Content content) {
        Long count = ratingRepository.countByContentId(content.getId());
        Double avg = ratingRepository.findAverageScoreByContentId(content.getId());

        TrafficColor color = null;
        if (avg != null) {
            if (avg >= 3.5) color = TrafficColor.GREEN;
            else if (avg >= 2.0) color = TrafficColor.YELLOW;
            else color = TrafficColor.RED;
        }

        content.updateKinoScore(avg, color, count.intValue());
    }
}
