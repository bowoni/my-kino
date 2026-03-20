package com.mykino.service;

import com.mykino.entity.Content;
import com.mykino.entity.User;
import com.mykino.entity.Watchlist;
import com.mykino.enums.WatchStatus;
import com.mykino.repository.ContentRepository;
import com.mykino.repository.WatchlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WatchlistService {

    private final WatchlistRepository watchlistRepository;
    private final ContentRepository contentRepository;

    @Transactional
    public Optional<WatchStatus> toggleStatus(User user, Long contentId, WatchStatus status) {
        Content content = contentRepository.findById(contentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 콘텐츠입니다."));

        Optional<Watchlist> existing = watchlistRepository.findByUserIdAndContentId(user.getId(), contentId);

        if (existing.isPresent()) {
            Watchlist watchlist = existing.get();
            if (watchlist.getStatus() == status) {
                watchlistRepository.delete(watchlist);
                return Optional.empty();
            } else {
                watchlist.updateStatus(status);
                return Optional.of(status);
            }
        } else {
            Watchlist watchlist = new Watchlist(user, content, null, status);
            watchlistRepository.save(watchlist);
            return Optional.of(status);
        }
    }

    public Optional<WatchStatus> getWatchStatus(Long userId, Long contentId) {
        return watchlistRepository.findByUserIdAndContentId(userId, contentId)
                .map(Watchlist::getStatus);
    }

    public List<Watchlist> getMyWatchlist(Long userId, WatchStatus status, int limit) {
        return watchlistRepository.findByUserIdAndStatusOrderByCreatedAtDesc(
                userId, status, PageRequest.of(0, limit));
    }

    public long countByStatus(Long userId, WatchStatus status) {
        return watchlistRepository.countByUserIdAndStatus(userId, status);
    }

    public List<Watchlist> getMyWatchlistAll(Long userId, WatchStatus status) {
        return watchlistRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status);
    }
}
