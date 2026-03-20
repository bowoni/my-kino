<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>탐색 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/explore.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <h1 class="page-title">탐색</h1>

        <!-- OTT 필터 -->
        <div class="ott-filter" id="ottFilter"></div>

        <!-- 카테고리 필터 -->
        <div class="category-filter" id="categoryFilter"></div>

        <!-- 필터 버튼 + 태그 + 정렬 -->
        <div class="filter-sort-bar">
            <div class="filter-bar-left">
                <button class="filter-btn" id="filterBtn">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M22 3H2l8 9.46V19l4 2v-8.54L22 3z"/></svg>
                    <span>필터</span>
                </button>
                <div class="filter-tags" id="filterTags" style="display:none;"></div>
            </div>
            <div class="sort-dropdown" id="sortDropdown">
                <button class="sort-toggle" id="sortToggle">
                    <span id="sortLabel">인기순</span>
                    <svg width="12" height="12" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M6 9l6 6 6-6"/></svg>
                </button>
                <ul class="sort-menu" id="sortMenu">
                    <li class="sort-option active" data-sort="popularity.desc">인기순</li>
                    <li class="sort-option" data-sort="vote_average.desc">평점순</li>
                    <li class="sort-option" data-sort="primary_release_date.desc">최신순</li>
                </ul>
            </div>
        </div>

        <!-- 필터 모달 -->
        <div class="genre-modal-overlay" id="filterModalOverlay">
            <div class="genre-modal">
                <div class="genre-modal-header">
                    <h3>필터</h3>
                    <div class="filter-header-actions">
                        <button class="filter-reset-btn" id="filterResetBtn">
                            <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M1 4v6h6"/><path d="M3.51 15a9 9 0 1 0 2.13-9.36L1 10"/></svg>
                            초기화
                        </button>
                        <button class="genre-modal-close" id="filterModalClose">&times;</button>
                    </div>
                </div>
                <div class="genre-modal-body" id="filterModalBody">
                    <div class="filter-section" id="filterGenreSection">
                        <div class="filter-section-header">
                            <span class="filter-section-title">장르</span>
                            <button class="filter-select-all active" data-section="filterGenreSection" data-type="multi"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>전체 선택</button>
                        </div>
                        <div class="filter-chips" id="filterGenreChips"></div>
                    </div>
                    <div class="filter-section" id="filterCountrySection">
                        <div class="filter-section-header">
                            <span class="filter-section-title">국가</span>
                            <button class="filter-select-all active" data-section="filterCountrySection" data-type="multi"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>전체 선택</button>
                        </div>
                        <div class="filter-chips" id="filterCountryChips"></div>
                    </div>
                    <div class="filter-section" id="filterYearSection">
                        <div class="filter-section-header">
                            <span class="filter-section-title">공개연도</span>
                            <button class="filter-select-all active" data-section="filterYearSection" data-type="single" data-key="year"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>전체 선택</button>
                        </div>
                        <div class="filter-chips" id="filterYearChips"></div>
                    </div>
                    <div class="filter-section" id="filterVoteSection">
                        <div class="filter-section-header">
                            <span class="filter-section-title">TMDB 평점</span>
                            <button class="filter-select-all active" data-section="filterVoteSection" data-type="single" data-key="voteMin"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>전체 선택</button>
                        </div>
                        <div class="filter-chips" id="filterVoteChips"></div>
                    </div>
                    <div class="filter-section" id="filterCertSection">
                        <div class="filter-section-header">
                            <span class="filter-section-title">관람등급</span>
                            <button class="filter-select-all active" data-section="filterCertSection" data-type="single" data-key="certification"><svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2.5"><path d="M20 6L9 17l-5-5"/></svg>전체 선택</button>
                        </div>
                        <div class="filter-chips" id="filterCertChips"></div>
                    </div>
                </div>
                <div class="filter-actions">
                    <button class="btn-primary filter-apply-btn" id="filterApplyBtn">적용하기</button>
                </div>
            </div>
        </div>

        <!-- 콘텐츠 그리드 -->
        <div class="explore-grid" id="exploreGrid"></div>
        <div class="loading-indicator" id="exploreLoading" style="display:none;">
            <div class="spinner"></div>
        </div>
        <div class="empty-state" id="exploreEmpty" style="display:none;">
            <p>검색 결과가 없습니다.</p>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script src="/js/util.js"></script>
    <script src="/js/explore.js"></script>
</body>
</html>
