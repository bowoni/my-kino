package com.mykino.repository;

import com.mykino.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {

    // 사용자 최근 검색어
    List<SearchHistory> findTop10ByUserIdOrderByCreatedAtDesc(Long userId);

    // 인기 검색어 (전체)
    @Query("SELECT s.keyword, COUNT(s) as cnt FROM SearchHistory s " +
           "GROUP BY s.keyword ORDER BY cnt DESC")
    List<Object[]> findPopularKeywords();
}
