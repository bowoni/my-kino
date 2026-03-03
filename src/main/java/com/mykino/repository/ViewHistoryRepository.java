package com.mykino.repository;

import com.mykino.entity.ViewHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ViewHistoryRepository extends JpaRepository<ViewHistory, Long> {

    List<ViewHistory> findByUserIdOrderByWatchDateDesc(Long userId);

    // 월별 시청 수
    @Query("SELECT COUNT(v) FROM ViewHistory v WHERE v.user.id = :userId " +
           "AND YEAR(v.watchDate) = :year AND MONTH(v.watchDate) = :month")
    Long countByUserIdAndYearMonth(@Param("userId") Long userId,
                                   @Param("year") int year,
                                   @Param("month") int month);

    // 총 시청 편수
    Long countByUserId(Long userId);
}
