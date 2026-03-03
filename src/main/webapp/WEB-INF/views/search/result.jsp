<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:if test="${not empty keyword}">${keyword} - </c:if>검색 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/search.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <!-- 검색바 -->
        <div class="search-bar">
            <form class="search-form" id="searchForm">
                <input type="text" value="${q}" placeholder="영화, 드라마 제목을 검색하세요"
                       id="searchInput" autocomplete="off">
                <button type="submit" class="search-btn">검색</button>
            </form>
            <div class="autocomplete-list" id="autocompleteList"></div>
        </div>

        <!-- 검색 결과 -->
        <c:if test="${not empty keyword}">
            <div class="search-info">
                <h2>'<span class="highlight">${keyword}</span>' 검색 결과</h2>
                <c:if test="${not empty results}">
                    <p class="result-count">총 ${results.totalElements}건</p>
                </c:if>
            </div>

            <c:choose>
                <c:when test="${not empty results && results.totalElements > 0}">
                    <div class="search-grid">
                        <c:forEach var="item" items="${results.content}">
                            <a href="/content/tmdb/${item.tmdbId}?type=${item.mediaType}" class="search-card">
                                <div class="card-poster">
                                    <c:choose>
                                        <c:when test="${not empty item.posterUrl}">
                                            <img src="${item.posterUrl}" alt="${item.title}">
                                        </c:when>
                                        <c:otherwise>
                                            ${fn:substring(item.title, 0, 4)}
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="card-info">
                                    <div class="card-title">${item.title}</div>
                                    <c:if test="${not empty item.titleEng}">
                                        <div class="card-title-eng">${item.titleEng}</div>
                                    </c:if>
                                    <div class="card-meta">
                                        <c:if test="${not empty item.releaseYear}">
                                            <span>${item.releaseYear}</span>
                                        </c:if>
                                        <span>${item.contentType}</span>
                                    </div>
                                    <c:if test="${not empty item.synopsis}">
                                        <div class="card-synopsis">${fn:substring(item.synopsis, 0, 80)}...</div>
                                    </c:if>
                                </div>
                            </a>
                        </c:forEach>
                    </div>

                    <!-- 페이징 -->
                    <c:if test="${results.totalPages > 1}">
                        <div class="pagination" id="pagination" data-keyword="${q}">
                            <c:if test="${results.number > 0}">
                                <a href="#" data-page="${results.number - 1}" class="page-btn">&laquo; 이전</a>
                            </c:if>

                            <c:forEach begin="0" end="${results.totalPages - 1}" var="i">
                                <c:choose>
                                    <c:when test="${i == results.number}">
                                        <span class="page-btn active">${i + 1}</span>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="#" data-page="${i}" class="page-btn">${i + 1}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test="${results.number < results.totalPages - 1}">
                                <a href="#" data-page="${results.number + 1}" class="page-btn">다음 &raquo;</a>
                            </c:if>
                        </div>
                    </c:if>
                </c:when>
                <c:otherwise>
                    <div class="no-results">
                        <p class="no-results-icon">:(</p>
                        <p class="no-results-text">'${keyword}'에 대한 검색 결과가 없습니다.</p>
                        <p class="no-results-hint">다른 검색어로 시도해보세요.</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </c:if>

        <!-- 검색어 미입력 시 -->
        <c:if test="${empty keyword}">
            <div class="search-empty">
                <p>영화나 드라마 제목을 검색해보세요.</p>
            </div>
        </c:if>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script>
        var searchInput = document.getElementById('searchInput');
        var autocompleteList = document.getElementById('autocompleteList');
        var debounceTimer;

        searchInput.addEventListener('input', function() {
            clearTimeout(debounceTimer);
            var query = this.value.trim();

            if (query.length < 1) {
                autocompleteList.style.display = 'none';
                return;
            }

            debounceTimer = setTimeout(function() {
                fetch('/api/public/autocomplete?q=' + encodeURIComponent(query))
                    .then(function(res) { return res.json(); })
                    .then(function(data) {
                        if (data.length === 0) {
                            autocompleteList.style.display = 'none';
                            return;
                        }

                        var html = '';
                        for (var i = 0; i < data.length; i++) {
                            var item = data[i];
                            var poster = item.posterUrl
                                ? '<img src="' + item.posterUrl + '" alt="">'
                                : '<span class="ac-no-img">' + item.title.substring(0, 1) + '</span>';

                            html += '<a href="/content/tmdb/' + item.tmdbId + '?type=' + (item.mediaType || 'movie') + '" class="ac-item">'
                                + '<div class="ac-poster">' + poster + '</div>'
                                + '<div class="ac-info">'
                                + '<div class="ac-title">' + item.title + '</div>'
                                + '<div class="ac-meta">' + (item.releaseYear || '') + ' · ' + item.contentType + '</div>'
                                + '</div></a>';
                        }
                        autocompleteList.innerHTML = html;
                        autocompleteList.style.display = 'block';
                    });
            }, 300);
        });

        // 바깥 클릭 시 자동완성 닫기
        document.addEventListener('click', function(e) {
            if (!searchInput.contains(e.target) && !autocompleteList.contains(e.target)) {
                autocompleteList.style.display = 'none';
            }
        });

        // 포커스 시 자동완성 다시 표시
        searchInput.addEventListener('focus', function() {
            if (autocompleteList.innerHTML.trim() !== '' && this.value.trim().length >= 1) {
                autocompleteList.style.display = 'block';
            }
        });

        // 폼 제출 시 %20 인코딩 적용
        document.getElementById('searchForm').addEventListener('submit', function(e) {
            e.preventDefault();
            var q = searchInput.value.trim();
            if (q.length > 0) {
                location.href = '/search?q=' + encodeURIComponent(q);
            }
        });

        // 페이징 클릭 시 %20 인코딩 적용
        var pagination = document.getElementById('pagination');
        if (pagination) {
            pagination.addEventListener('click', function(e) {
                var link = e.target.closest('a[data-page]');
                if (link) {
                    e.preventDefault();
                    var keyword = pagination.getAttribute('data-keyword');
                    var page = link.getAttribute('data-page');
                    location.href = '/search?q=' + encodeURIComponent(keyword) + '&page=' + page;
                }
            });
        }
    </script>
</body>
</html>
