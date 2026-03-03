package com.mykino.service;

import com.mykino.entity.Content;
import com.mykino.entity.Review;
import com.mykino.entity.ReviewLike;
import com.mykino.entity.User;
import com.mykino.repository.ContentRepository;
import com.mykino.repository.ReviewLikeRepository;
import com.mykino.repository.ReviewRepository;
import com.mykino.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final ContentRepository contentRepository;
    private final UserRepository userRepository;

    public Page<Review> getReviews(Long contentId, Pageable pageable) {
        return reviewRepository.findByContentIdAndIsPublicTrue(contentId, pageable);
    }

    @Transactional
    public Review createReview(User user, Long contentId, String title,
                               String body, Boolean hasSpoiler) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘텐츠입니다."));

        Review review = Review.builder()
                .user(user)
                .content(content)
                .title(title)
                .body(body)
                .hasSpoiler(hasSpoiler)
                .build();

        return reviewRepository.save(review);
    }

    @Transactional
    public void deleteReview(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalArgumentException("본인의 리뷰만 삭제할 수 있습니다.");
        }

        reviewRepository.delete(review);
    }

    @Transactional
    public boolean toggleLike(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        return reviewLikeRepository.findByUserIdAndReviewId(userId, reviewId)
                .map(like -> {
                    reviewLikeRepository.delete(like);
                    review.decrementLikeCount();
                    return false;
                })
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
                    reviewLikeRepository.save(new ReviewLike(user, review));
                    review.incrementLikeCount();
                    return true;
                });
    }
}
