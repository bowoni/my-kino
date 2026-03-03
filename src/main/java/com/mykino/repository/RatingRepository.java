package com.mykino.repository;

import com.mykino.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    Optional<Rating> findByUserIdAndContentId(Long userId, Long contentId);

    // 콘텐츠별 평균 별점
    @Query("SELECT AVG(r.score) FROM Rating r WHERE r.content.id = :contentId")
    Double findAverageScoreByContentId(@Param("contentId") Long contentId);

    // 콘텐츠별 평가 수
    Long countByContentId(Long contentId);
}
