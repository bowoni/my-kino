package com.mykino.service;

import com.mykino.entity.Content;
import com.mykino.entity.SearchHistory;
import com.mykino.entity.User;
import com.mykino.repository.ContentRepository;
import com.mykino.repository.SearchHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SearchService {

    private final ContentRepository contentRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    /**
     * 키워드 검색 (페이징)
     */
    public Page<Content> search(String keyword, Pageable pageable) {
        return contentRepository.searchByKeyword(keyword, pageable);
    }

    /**
     * 자동완성 (상위 10개)
     */
    public List<Content> autocomplete(String keyword) {
        return contentRepository.findTop10ByKeyword(keyword);
    }

    /**
     * 검색 기록 저장
     */
    @Transactional
    public void saveSearchHistory(User user, String keyword) {
        if (user != null && keyword != null && !keyword.trim().isEmpty()) {
            searchHistoryRepository.save(new SearchHistory(user, keyword.trim()));
        }
    }

    /**
     * 최근 검색어 (로그인 사용자)
     */
    public List<String> getRecentKeywords(Long userId) {
        return searchHistoryRepository.findTop10ByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(SearchHistory::getKeyword)
                .collect(Collectors.toList());
    }

    /**
     * 인기 검색어 (상위 10개)
     */
    public List<String> getPopularKeywords() {
        return searchHistoryRepository.findPopularKeywords()
                .stream()
                .limit(10)
                .map(row -> (String) row[0])
                .collect(Collectors.toList());
    }
}
