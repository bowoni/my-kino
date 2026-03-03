<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MyKino - OTT 통합검색</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/home.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <div class="hero">
            <h1 class="hero-title">어디서 볼 수 있을까?<br><span>MyKino</span>에서 찾아보세요</h1>
            <p class="hero-desc">넷플릭스, 티빙, 웨이브, 왓챠, 디즈니+ 한 번에 검색</p>
            <div class="hero-search">
                <form class="hero-search-form" id="heroSearchForm">
                    <input type="text" placeholder="영화, 드라마 제목을 검색하세요"
                           id="heroSearchInput" autocomplete="off">
                </form>
                <div class="hero-autocomplete" id="heroAutocomplete"></div>
            </div>
        </div>

        <sec:authorize access="isAuthenticated()">
            <div class="welcome-section">
                <h2>${user.nickname}님, 환영합니다!</h2>
                <p>오늘은 어떤 콘텐츠를 감상하시겠어요?</p>
            </div>
        </sec:authorize>

        <!-- 인기 콘텐츠 (TMDB 무한스크롤) -->
        <div class="home-section">
            <h2 class="home-section-title">인기 콘텐츠</h2>
            <div class="content-grid" id="popularGrid"></div>
            <div class="loading-indicator" id="popularLoading" style="display:none;">
                <div class="spinner"></div>
            </div>
        </div>

        <!-- 현재 상영중 -->
        <div class="home-section">
            <h2 class="home-section-title">현재 상영중</h2>
            <div class="content-grid" id="nowPlayingGrid"></div>
            <div class="loading-indicator" id="nowPlayingLoading" style="display:none;">
                <div class="spinner"></div>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script>
        var heroInput = document.getElementById('heroSearchInput');
        var heroAC = document.getElementById('heroAutocomplete');
        var timer;

        heroInput.addEventListener('input', function() {
            clearTimeout(timer);
            var q = this.value.trim();
            if (q.length < 1) { heroAC.style.display = 'none'; return; }

            timer = setTimeout(function() {
                fetch('/api/public/autocomplete?q=' + encodeURIComponent(q))
                    .then(function(r) { return r.json(); })
                    .then(function(data) {
                        if (data.length === 0) { heroAC.style.display = 'none'; return; }
                        var h = '';
                        for (var i = 0; i < data.length; i++) {
                            var d = data[i];
                            var img = d.posterUrl
                                ? '<img src="' + d.posterUrl + '">'
                                : '<span>' + d.title.substring(0,1) + '</span>';
                            h += '<a href="/content/tmdb/' + d.tmdbId + '?type=' + (d.mediaType || 'movie') + '" class="hero-ac-item">'
                                + '<div class="hero-ac-poster">' + img + '</div>'
                                + '<div class="hero-ac-info"><div class="hero-ac-title">' + d.title + '</div>'
                                + '<div class="hero-ac-meta">' + (d.releaseYear||'') + ' · ' + d.contentType + '</div></div></a>';
                        }
                        heroAC.innerHTML = h;
                        heroAC.style.display = 'block';
                    });
            }, 300);
        });

        document.addEventListener('click', function(e) {
            if (!heroInput.contains(e.target) && !heroAC.contains(e.target)) {
                heroAC.style.display = 'none';
            }
        });

        // 폼 제출 시 %20 인코딩 적용
        document.getElementById('heroSearchForm').addEventListener('submit', function(e) {
            e.preventDefault();
            var q = heroInput.value.trim();
            if (q.length > 0) {
                location.href = '/search?q=' + encodeURIComponent(q);
            }
        });

        // 무한스크롤 로직
        function createInfiniteScroll(gridId, loadingId, apiUrl) {
            var grid = document.getElementById(gridId);
            var loading = document.getElementById(loadingId);
            var page = 0;
            var isLoading = false;
            var hasMore = true;

            function renderCard(item) {
                var poster = item.posterUrl
                    ? '<img src="' + item.posterUrl + '" alt="' + item.title + '">'
                    : '<span>' + item.title.substring(0, 4) + '</span>';
                var score = item.voteAverage
                    ? '<div class="card-score tmdb-score">' + item.voteAverage + '</div>'
                    : '';
                return '<a href="/content/tmdb/' + item.tmdbId + '?type=' + (item.mediaType || 'movie') + '" class="content-card">'
                    + '<div class="card-poster">' + poster + score + '</div>'
                    + '<div class="card-title">' + item.title + '</div>'
                    + '<div class="card-year">' + (item.releaseYear || '') + ' · ' + item.contentType + '</div>'
                    + '</a>';
            }

            function loadMore() {
                if (isLoading || !hasMore) return;
                isLoading = true;
                loading.style.display = 'flex';

                fetch(apiUrl + '?page=' + page)
                    .then(function(r) { return r.json(); })
                    .then(function(data) {
                        var items = data.content;
                        var html = '';
                        for (var i = 0; i < items.length; i++) {
                            html += renderCard(items[i]);
                        }
                        grid.insertAdjacentHTML('beforeend', html);
                        page++;
                        hasMore = page < data.totalPages;
                        isLoading = false;
                        loading.style.display = 'none';
                    })
                    .catch(function() {
                        isLoading = false;
                        loading.style.display = 'none';
                    });
            }

            // 첫 페이지 로드
            loadMore();

            return { loadMore: loadMore, getGrid: function() { return grid; } };
        }

        var popularScroll = createInfiniteScroll('popularGrid', 'popularLoading', '/api/public/tmdb/popular');
        var nowPlayingScroll = createInfiniteScroll('nowPlayingGrid', 'nowPlayingLoading', '/api/public/tmdb/now-playing');

        // 스크롤 이벤트
        window.addEventListener('scroll', function() {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 500) {
                popularScroll.loadMore();
                nowPlayingScroll.loadMore();
            }
        });
    </script>
</body>
</html>
