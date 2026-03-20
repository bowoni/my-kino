<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${content.title}"/> - MyKino</title>
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
                    <img src="${content.posterUrl}" alt="${fn:escapeXml(content.title)}">
                </c:when>
                <c:otherwise>
                    포스터 없음
                </c:otherwise>
            </c:choose>
        </div>

        <div class="content-info">
            <h1 class="content-title"><c:out value="${content.title}"/></h1>
            <c:if test="${not empty content.titleEng}">
                <p class="content-title-eng"><c:out value="${content.titleEng}"/></p>
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

            <!-- Watch Status -->
            <sec:authorize access="isAuthenticated()">
                <div class="watch-status-btns" id="watchStatusBtns" data-content-id="${content.id}">
                    <button class="watch-btn ${myWatchStatus == 'WANT_TO_WATCH' ? 'active' : ''}"
                            data-status="WANT_TO_WATCH">
                        <span class="watch-icon">&#128278;</span> 찜
                    </button>
                    <button class="watch-btn ${myWatchStatus == 'WATCHING' ? 'active' : ''}"
                            data-status="WATCHING">
                        <span class="watch-icon">&#9200;</span> 보는중
                    </button>
                    <button class="watch-btn ${myWatchStatus == 'WATCHED' ? 'active' : ''}"
                            data-status="WATCHED">
                        <span class="watch-icon">&#9989;</span> 봤어요
                    </button>
                </div>
            </sec:authorize>
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
                                            <c:choose>
                                                <c:when test="${not empty ott.logoUrl}">
                                                    <img class="ott-logo-img" src="${ott.logoUrl}" alt="${fn:escapeXml(ott.ottName)}">
                                                </c:when>
                                                <c:otherwise>
                                                    <div class="ott-logo">${fn:substring(ott.ottName, 0, 2)}</div>
                                                </c:otherwise>
                                            </c:choose>
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
                            <span class="director-name"><c:out value="${director.name}"/></span>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <!-- Synopsis -->
        <c:if test="${not empty content.synopsis}">
            <div class="section">
                <h2 class="section-title">줄거리</h2>
                <p class="synopsis-text"><c:out value="${content.synopsis}"/></p>
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
                                        <img src="${actor.profileImage}" alt="${fn:escapeXml(actor.name)}">
                                    </c:when>
                                    <c:otherwise>
                                        ${fn:substring(actor.name, 0, 1)}
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="cast-name"><c:out value="${actor.name}"/></div>
                            <c:if test="${not empty actor.characterName}">
                                <div class="cast-role"><c:out value="${actor.characterName}"/> 역</div>
                            </c:if>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>

        <!-- 평가 및 리뷰 -->
        <div class="section">
            <h2 class="section-title">평가 및 리뷰</h2>
            <sec:authorize access="isAuthenticated()">
                <div class="my-rating" id="myRating" data-content-id="${content.id}"
                     data-traffic-color="${myTrafficColor}" data-score="${myScore}">
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
                    <textarea id="reviewBody" placeholder="이 작품에 대한 평가를 남겨보세요 (선택)" rows="3" maxlength="2000"></textarea>
                    <div class="rating-form-bottom">
                        <label class="spoiler-check">
                            <input type="checkbox" id="reviewSpoiler"> 스포일러 포함
                        </label>
                        <button id="submitBtn" class="rating-submit-btn">평가하기</button>
                    </div>
                </div>
            </sec:authorize>
            <sec:authorize access="isAnonymous()">
                <div class="login-prompt">
                    <p>평가하려면 <a href="/login">로그인</a>이 필요합니다.</p>
                </div>
            </sec:authorize>
            <div id="reviewList" data-content-id="${content.id}"></div>
            <div id="reviewMore" class="review-more" style="display:none;">
                <button id="reviewMoreBtn" class="review-more-btn">더보기</button>
            </div>
        </div>

    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script src="/js/util.js"></script>
    <script src="/js/content-detail.js"></script>
</body>
</html>
