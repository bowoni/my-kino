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

        <!-- 장르 필터 -->
        <div class="genre-filter" id="genreFilter">
            <button class="genre-chip active" data-genre="">전체</button>
        </div>

        <!-- 정렬 -->
        <div class="sort-bar">
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

    <script>
        var CATEGORIES = [];
        var OTT_PROVIDERS = [];

        var grid = document.getElementById('exploreGrid');
        var loading = document.getElementById('exploreLoading');
        var emptyState = document.getElementById('exploreEmpty');
        var currentCategory = null;
        var currentGenre = '';
        var currentProvider = '';
        var currentSort = 'popularity.desc';
        var page = 0;
        var isLoading = false;
        var hasMore = true;

        // DB에서 카테고리 + OTT 데이터 fetch 후 초기화
        Promise.all([
            fetch('/api/public/tmdb/categories').then(function(r) { return r.json(); }),
            fetch('/api/public/tmdb/ott-providers').then(function(r) { return r.json(); })
        ]).then(function(results) {
            CATEGORIES = results[0];
            OTT_PROVIDERS = results[1];
            currentCategory = CATEGORIES[0];
            initFilters();
            loadMore();
        });

        function initFilters() {
            // OTT 이미지 버튼 렌더링
            var ottFilter = document.getElementById('ottFilter');
            var allOtt = document.createElement('button');
            allOtt.className = 'ott-img-chip active';
            allOtt.setAttribute('data-provider', '');
            allOtt.textContent = '전체';
            ottFilter.appendChild(allOtt);

            for (var j = 0; j < OTT_PROVIDERS.length; j++) {
                var btn = document.createElement('button');
                btn.className = 'ott-img-chip';
                btn.setAttribute('data-provider', OTT_PROVIDERS[j].id);
                btn.setAttribute('title', OTT_PROVIDERS[j].name);
                if (OTT_PROVIDERS[j].logoUrl) {
                    var img = document.createElement('img');
                    img.src = OTT_PROVIDERS[j].logoUrl;
                    img.alt = OTT_PROVIDERS[j].name;
                    btn.appendChild(img);
                } else {
                    btn.textContent = OTT_PROVIDERS[j].name;
                }
                ottFilter.appendChild(btn);
            }

            // 카테고리 칩 렌더링
            var catFilter = document.getElementById('categoryFilter');
            for (var i = 0; i < CATEGORIES.length; i++) {
                var catBtn = document.createElement('button');
                catBtn.className = 'category-chip' + (i === 0 ? ' active' : '');
                catBtn.setAttribute('data-category', CATEGORIES[i].key);
                catBtn.textContent = CATEGORIES[i].label;
                catFilter.appendChild(catBtn);
            }
            renderSubGenres(CATEGORIES[0]);
        }

        function renderSubGenres(category) {
            var filter = document.getElementById('genreFilter');
            filter.innerHTML = '';

            var allBtn = document.createElement('button');
            allBtn.className = 'genre-chip active';
            allBtn.setAttribute('data-genre', '');
            allBtn.textContent = '전체';
            filter.appendChild(allBtn);

            var subs = category.subGenres;
            for (var i = 0; i < subs.length; i++) {
                var btn = document.createElement('button');
                btn.className = 'genre-chip';
                btn.setAttribute('data-genre', String(subs[i].id));
                btn.textContent = subs[i].name;
                filter.appendChild(btn);
            }

            filter.style.display = subs.length === 0 ? 'none' : 'flex';
        }

        // 카테고리 클릭
        document.getElementById('categoryFilter').addEventListener('click', function(e) {
            var btn = e.target.closest('.category-chip');
            if (!btn) return;
            document.querySelectorAll('.category-chip').forEach(function(el) { el.classList.remove('active'); });
            btn.classList.add('active');

            var key = btn.getAttribute('data-category');
            for (var i = 0; i < CATEGORIES.length; i++) {
                if (CATEGORIES[i].key === key) {
                    currentCategory = CATEGORIES[i];
                    break;
                }
            }
            currentGenre = '';
            renderSubGenres(currentCategory);
            resetAndLoad();
        });

        // OTT 클릭
        document.getElementById('ottFilter').addEventListener('click', function(e) {
            var btn = e.target.closest('.ott-img-chip');
            if (!btn) return;
            document.querySelectorAll('.ott-img-chip').forEach(function(el) { el.classList.remove('active'); });
            btn.classList.add('active');
            currentProvider = btn.getAttribute('data-provider');
            resetAndLoad();
        });

        // 장르 클릭
        document.getElementById('genreFilter').addEventListener('click', function(e) {
            var btn = e.target.closest('.genre-chip');
            if (!btn) return;
            document.querySelectorAll('.genre-chip').forEach(function(el) { el.classList.remove('active'); });
            btn.classList.add('active');
            currentGenre = btn.getAttribute('data-genre');
            resetAndLoad();
        });

        // 정렬 드롭다운
        var sortToggle = document.getElementById('sortToggle');
        var sortMenu = document.getElementById('sortMenu');
        var sortDropdown = document.getElementById('sortDropdown');
        var sortLabel = document.getElementById('sortLabel');

        sortToggle.addEventListener('click', function() {
            sortDropdown.classList.toggle('open');
        });

        sortMenu.addEventListener('click', function(e) {
            var option = e.target.closest('.sort-option');
            if (!option) return;
            sortMenu.querySelectorAll('.sort-option').forEach(function(el) { el.classList.remove('active'); });
            option.classList.add('active');
            sortLabel.textContent = option.textContent;
            currentSort = option.getAttribute('data-sort');
            sortDropdown.classList.remove('open');
            resetAndLoad();
        });

        document.addEventListener('click', function(e) {
            if (!sortDropdown.contains(e.target)) {
                sortDropdown.classList.remove('open');
            }
        });

        function resetAndLoad() {
            page = 0;
            hasMore = true;
            grid.innerHTML = '';
            emptyState.style.display = 'none';
            loadMore();
        }

        function buildGenreParam(baseGenre, subGenre) {
            if (!baseGenre && !subGenre) return null;
            if (!baseGenre) return subGenre;
            if (!subGenre) return baseGenre;

            // baseGenre가 OR(|)이고 subGenre가 그 중 하나면 subGenre만 사용
            if (baseGenre.indexOf('|') !== -1) {
                var parts = baseGenre.split('|');
                for (var i = 0; i < parts.length; i++) {
                    if (parts[i] === subGenre) return subGenre;
                }
            }

            return baseGenre + ',' + subGenre;
        }

        function renderCard(item) {
            var poster = item.posterUrl
                ? '<img src="' + item.posterUrl + '" alt="' + item.title + '">'
                : '<span>' + item.title.substring(0, 4) + '</span>';
            var score = item.voteAverage
                ? '<div class="explore-score tmdb-score">' + item.voteAverage + '</div>'
                : '';
            return '<a href="/content/tmdb/' + item.tmdbId + '?type=' + (item.mediaType || 'movie') + '" class="explore-card">'
                + '<div class="explore-poster">' + poster + score + '</div>'
                + '<div class="explore-title">' + item.title + '</div>'
                + '<div class="explore-meta">' + (item.releaseYear || '') + ' · ' + item.contentType + '</div>'
                + '</a>';
        }

        function loadMore() {
            if (isLoading || !hasMore || !currentCategory) return;
            isLoading = true;
            loading.style.display = 'flex';

            var effectiveGenre = buildGenreParam(currentCategory.baseGenre, currentGenre);
            var url = '/api/public/tmdb/discover?sort=' + currentSort
                    + '&page=' + page
                    + '&mediaType=' + currentCategory.mediaType;
            if (effectiveGenre) {
                url += '&genre=' + encodeURIComponent(effectiveGenre);
            }
            if (currentProvider) {
                url += '&provider=' + currentProvider;
            }

            fetch(url)
                .then(function(r) { return r.json(); })
                .then(function(data) {
                    var items = data.content;
                    if (page === 0 && items.length === 0) {
                        emptyState.style.display = 'block';
                    }
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

        // 무한스크롤
        window.addEventListener('scroll', function() {
            if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - 500) {
                loadMore();
            }
        });
    </script>
</body>
</html>
