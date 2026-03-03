<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${content.title} - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/content.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <!-- Backdrop -->
    <div class="content-backdrop"
         style="<c:if test='${not empty content.backdropUrl}'>background-image: url('${content.backdropUrl}');</c:if>">
    </div>

    <!-- Hero: Poster + Info -->
    <div class="content-hero">
        <div class="content-poster">
            <c:choose>
                <c:when test="${not empty content.posterUrl}">
                    <img src="${content.posterUrl}" alt="${content.title}">
                </c:when>
                <c:otherwise>
                    포스터 없음
                </c:otherwise>
            </c:choose>
        </div>

        <div class="content-info">
            <h1 class="content-title">${content.title}</h1>
            <c:if test="${not empty content.titleEng}">
                <p class="content-title-eng">${content.titleEng}</p>
            </c:if>

            <!-- Meta Tags -->
            <div class="content-meta">
                <span class="meta-tag">${content.contentType.displayName}</span>
                <c:if test="${not empty content.releaseYear}">
                    <span class="meta-tag">${content.releaseYear}</span>
                </c:if>
                <c:if test="${not empty content.runtime}">
                    <span class="meta-tag">${content.runtime}분</span>
                </c:if>
                <c:if test="${not empty content.country}">
                    <span class="meta-tag">${content.country}</span>
                </c:if>
                <c:if test="${not empty content.ageRating}">
                    <span class="meta-tag">${content.ageRating}</span>
                </c:if>
            </div>

            <!-- Genres -->
            <c:if test="${not empty content.genres}">
                <div class="content-genres">
                    <c:forEach var="genre" items="${content.genres}">
                        <span class="genre-tag">${genre}</span>
                    </c:forEach>
                </div>
            </c:if>

            <!-- Kino Score -->
            <div class="kino-score-section">
                <c:choose>
                    <c:when test="${not empty content.kinoColor}">
                        <div class="kino-badge ${fn:toLowerCase(content.kinoColor)}">
                            ${content.kinoScore}
                        </div>
                        <div class="kino-info">
                            <span class="kino-status">${content.kinoColor.displayName}</span>
                            <span class="kino-count">${content.totalRatings}명 평가</span>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="kino-badge none">-</div>
                        <div class="kino-info">
                            <span class="kino-status">평가 부족</span>
                            <span class="kino-count">50명 이상 평가 시 활성화</span>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <!-- Body -->
    <div class="content-body">

        <!-- OTT -->
        <div class="section">
            <h2 class="section-title">어디서 볼 수 있나요?</h2>
            <c:choose>
                <c:when test="${not empty content.ottInfos}">
                    <div class="ott-grid">
                        <c:forEach var="ott" items="${content.ottInfos}">
                            <div class="ott-card${ott.ottName == '극장' ? ' theater' : ''}">
                                <div class="ott-left">
                                    <c:choose>
                                        <c:when test="${ott.ottName == '극장'}">
                                            <div class="ott-logo theater">🎬</div>
                                            <div>
                                                <div class="ott-name">극장 상영중</div>
                                                <div class="ott-type">현재 극장에서 상영중</div>
                                            </div>
                                        </c:when>
                                        <c:otherwise>
                                            <div class="ott-logo">${fn:substring(ott.ottName, 0, 2)}</div>
                                            <div>
                                                <div class="ott-name">${ott.ottName}</div>
                                                <c:if test="${ott.isStreaming}">
                                                    <div class="ott-type">스트리밍</div>
                                                </c:if>
                                            </div>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                                <div class="ott-right">
                                    <c:choose>
                                        <c:when test="${ott.ottName == '극장'}">
                                            <span class="ott-theater-badge">상영중</span>
                                        </c:when>
                                        <c:otherwise>
                                            <c:if test="${ott.isStreaming}">
                                                <span class="ott-streaming">구독 시청</span>
                                            </c:if>
                                            <c:if test="${not empty ott.rentPrice}">
                                                <div class="ott-price">대여 ${ott.rentPrice}원</div>
                                            </c:if>
                                            <c:if test="${not empty ott.buyPrice}">
                                                <div class="ott-price">구매 ${ott.buyPrice}원</div>
                                            </c:if>
                                            <c:if test="${not empty ott.baseUrl}">
                                                <a href="${ott.baseUrl}" target="_blank" class="ott-link">바로가기 &rarr;</a>
                                            </c:if>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p style="color: #666;">현재 제공 중인 OTT 정보가 없습니다.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Director -->
        <c:if test="${not empty content.directors}">
            <div class="section">
                <h2 class="section-title">감독</h2>
                <div class="director-list">
                    <c:forEach var="director" items="${content.directors}">
                        <div class="director-tag">
                            <span class="director-name">${director.name}</span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <!-- Synopsis -->
        <c:if test="${not empty content.synopsis}">
            <div class="section">
                <h2 class="section-title">줄거리</h2>
                <p class="synopsis-text">${content.synopsis}</p>
            </div>
        </c:if>

        <!-- Cast -->
        <c:if test="${not empty content.actors}">
            <div class="section">
                <h2 class="section-title">출연진</h2>
                <div class="cast-list">
                    <c:forEach var="actor" items="${content.actors}">
                        <div class="cast-card">
                            <div class="cast-avatar">
                                <c:choose>
                                    <c:when test="${not empty actor.profileImage}">
                                        <img src="${actor.profileImage}" alt="${actor.name}">
                                    </c:when>
                                    <c:otherwise>
                                        ${fn:substring(actor.name, 0, 1)}
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="cast-name">${actor.name}</div>
                            <c:if test="${not empty actor.characterName}">
                                <div class="cast-role">${actor.characterName} 역</div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <!-- My Rating -->
        <div class="section">
            <h2 class="section-title">내 평가</h2>
            <sec:authorize access="isAuthenticated()">
                <div class="my-rating" id="myRating" data-content-id="${content.id}">
                    <div class="traffic-btns">
                        <button class="traffic-btn red ${myTrafficColor == 'RED' ? 'active' : ''}"
                                data-color="RED" title="별로예요">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                            </svg>
                            <span>별로예요</span>
                        </button>
                        <button class="traffic-btn yellow ${myTrafficColor == 'YELLOW' ? 'active' : ''}"
                                data-color="YELLOW" title="보통이에요">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                            </svg>
                            <span>보통이에요</span>
                        </button>
                        <button class="traffic-btn green ${myTrafficColor == 'GREEN' ? 'active' : ''}"
                                data-color="GREEN" title="추천해요">
                            <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor">
                                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
                            </svg>
                            <span>추천해요</span>
                        </button>
                    </div>
                    <div class="rating-score">
                        <label>별점</label>
                        <div class="star-rating" id="starRating">
                            <c:forEach begin="1" end="5" var="i">
                                <span class="star ${not empty myScore && myScore >= i ? 'filled' : ''}" data-value="${i}">&#9733;</span>
                            </c:forEach>
                        </div>
                        <span class="score-value" id="scoreValue">${myScore != null ? myScore : ''}</span>
                    </div>
                    <div class="rating-comment">
                        <input type="text" id="ratingComment" placeholder="한줄평을 남겨보세요"
                               value="${myComment}" maxlength="200">
                        <button id="ratingSubmitBtn" class="rating-submit-btn">평가하기</button>
                    </div>
                </div>
            </sec:authorize>
            <sec:authorize access="isAnonymous()">
                <div class="login-prompt">
                    <p>평가하려면 <a href="/login">로그인</a>이 필요합니다.</p>
                </div>
            </sec:authorize>
        </div>

        <!-- Reviews -->
        <div class="section">
            <h2 class="section-title">리뷰</h2>
            <sec:authorize access="isAuthenticated()">
                <div class="review-form" id="reviewForm">
                    <textarea id="reviewBody" placeholder="이 작품에 대한 리뷰를 작성해보세요" rows="4" maxlength="2000"></textarea>
                    <div class="review-form-bottom">
                        <label class="spoiler-check">
                            <input type="checkbox" id="reviewSpoiler"> 스포일러 포함
                        </label>
                        <button id="reviewSubmitBtn" class="review-submit-btn">리뷰 작성</button>
                    </div>
                </div>
            </sec:authorize>
            <div id="reviewList" data-content-id="${content.id}"></div>
            <div id="reviewMore" class="review-more" style="display:none;">
                <button id="reviewMoreBtn" class="review-more-btn">더보기</button>
            </div>
        </div>

    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script>
    (function() {
        var contentId = parseInt('${content.id}');
        var selectedColor = '<c:out value="${myTrafficColor}"/>' || null;
        var selectedScore = '<c:out value="${myScore}"/>' !== '' ? parseFloat('<c:out value="${myScore}"/>') : null;

        // === Traffic Light Rating ===
        var trafficBtns = document.querySelectorAll('.traffic-btn');
        trafficBtns.forEach(function(btn) {
            btn.addEventListener('click', function() {
                trafficBtns.forEach(function(b) { b.classList.remove('active'); });
                this.classList.add('active');
                selectedColor = this.getAttribute('data-color');
            });
        });

        // === Star Rating ===
        var stars = document.querySelectorAll('.star');
        var scoreValue = document.getElementById('scoreValue');
        stars.forEach(function(star) {
            star.addEventListener('click', function() {
                selectedScore = parseInt(this.getAttribute('data-value'));
                scoreValue.textContent = selectedScore;
                stars.forEach(function(s) {
                    s.classList.toggle('filled',
                        parseInt(s.getAttribute('data-value')) <= selectedScore);
                });
            });
            star.addEventListener('mouseenter', function() {
                var val = parseInt(this.getAttribute('data-value'));
                stars.forEach(function(s) {
                    s.classList.toggle('hover',
                        parseInt(s.getAttribute('data-value')) <= val);
                });
            });
            star.addEventListener('mouseleave', function() {
                stars.forEach(function(s) { s.classList.remove('hover'); });
            });
        });

        // === Submit Rating ===
        var submitBtn = document.getElementById('ratingSubmitBtn');
        if (submitBtn) {
            submitBtn.addEventListener('click', function() {
                if (!selectedColor) {
                    alert('추천/보통/별로 중 하나를 선택해주세요.');
                    return;
                }
                var comment = document.getElementById('ratingComment').value.trim();

                fetch('/api/rating', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        contentId: contentId,
                        trafficColor: selectedColor,
                        score: selectedScore,
                        comment: comment
                    })
                })
                .then(function(res) {
                    if (res.status === 401) { location.href = '/login'; return; }
                    return res.json();
                })
                .then(function(data) {
                    if (data) {
                        alert('평가가 저장되었습니다.');
                        location.reload();
                    }
                });
            });
        }

        // === Reviews ===
        var reviewPage = 0;
        var reviewTotal = 0;

        function loadReviews(page) {
            fetch('/api/review/content/' + contentId + '?page=' + page)
                .then(function(res) { return res.json(); })
                .then(function(data) {
                    reviewTotal = data.totalPages;
                    var list = document.getElementById('reviewList');
                    var html = '';

                    if (data.content.length === 0 && page === 0) {
                        html = '<p class="no-reviews">아직 리뷰가 없습니다. 첫 리뷰를 남겨보세요!</p>';
                    }

                    for (var i = 0; i < data.content.length; i++) {
                        var r = data.content[i];
                        var bodyText = r.hasSpoiler
                            ? '<span class="spoiler-tag">스포일러</span> <span class="spoiler-blur">' + r.body + '</span>'
                            : r.body;

                        html += '<div class="review-card">'
                            + '<div class="review-header">'
                            + '<span class="review-nickname">' + r.nickname + '</span>'
                            + '<span class="review-date">' + r.createdAt.substring(0, 10) + '</span>'
                            + '</div>';
                        html += '<div class="review-body">' + bodyText + '</div>'
                            + '<div class="review-footer">'
                            + '<button class="review-like-btn" data-review-id="' + r.id + '">'
                            + '&#10084; ' + r.likeCount
                            + '</button>'
                            + '</div>'
                            + '</div>';
                    }

                    if (page === 0) {
                        list.innerHTML = html;
                    } else {
                        list.innerHTML += html;
                    }

                    var moreDiv = document.getElementById('reviewMore');
                    moreDiv.style.display = (page + 1 < reviewTotal) ? 'block' : 'none';
                });
        }

        loadReviews(0);

        document.getElementById('reviewMoreBtn').addEventListener('click', function() {
            reviewPage++;
            loadReviews(reviewPage);
        });

        // === Submit Review ===
        var reviewSubmitBtn = document.getElementById('reviewSubmitBtn');
        if (reviewSubmitBtn) {
            reviewSubmitBtn.addEventListener('click', function() {
                var body = document.getElementById('reviewBody').value.trim();
                if (!body) { alert('리뷰 내용을 입력해주세요.'); return; }

                var hasSpoiler = document.getElementById('reviewSpoiler').checked;

                fetch('/api/review', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        contentId: contentId,
                        body: body,
                        hasSpoiler: hasSpoiler
                    })
                })
                .then(function(res) {
                    if (res.status === 401) { location.href = '/login'; return; }
                    return res.json();
                })
                .then(function(data) {
                    if (data) {
                        document.getElementById('reviewBody').value = '';
                        document.getElementById('reviewSpoiler').checked = false;
                        reviewPage = 0;
                        loadReviews(0);
                    }
                });
            });
        }

        // === Review Like (delegate) ===
        document.getElementById('reviewList').addEventListener('click', function(e) {
            var btn = e.target.closest('.review-like-btn');
            if (!btn) return;

            var reviewId = btn.getAttribute('data-review-id');
            fetch('/api/review/' + reviewId + '/like', { method: 'POST' })
                .then(function(res) {
                    if (res.status === 401) { location.href = '/login'; return; }
                    return res.json();
                })
                .then(function(data) {
                    if (data) {
                        reviewPage = 0;
                        loadReviews(0);
                    }
                });
        });
    })();
    </script>
</body>
</html>
