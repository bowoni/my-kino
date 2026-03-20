package com.mykino.repository;

import com.mykino.entity.Watchlist;
import com.mykino.enums.WatchStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WatchlistRepository extends JpaRepository<Watchlist, Long> {

    List<Watchlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Watchlist> findByUserIdAndContentId(Long userId, Long contentId);

    boolean existsByUserIdAndContentId(Long userId, Long contentId);

    List<Watchlist> findByUserIdAndFolderId(Long userId, Long folderId);

    List<Watchlist> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, WatchStatus status);

    List<Watchlist> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, WatchStatus status, Pageable pageable);

    long countByUserIdAndStatus(Long userId, WatchStatus status);
}
