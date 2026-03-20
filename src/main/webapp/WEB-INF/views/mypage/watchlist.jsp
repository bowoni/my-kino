<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 콘텐츠 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/profile.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <h1 class="page-title">내 콘텐츠</h1>

        <!-- 탭 -->
        <div class="watchlist-tabs">
            <a href="/mypage/watchlist?status=WANT_TO_WATCH"
               class="watchlist-tab ${currentStatus == 'WANT_TO_WATCH' ? 'active' : ''}">
                찜했어요 <span class="tab-count">${wantCount}</span>
            </a>
            <a href="/mypage/watchlist?status=WATCHING"
               class="watchlist-tab ${currentStatus == 'WATCHING' ? 'active' : ''}">
                보는중 <span class="tab-count">${watchingCount}</span>
            </a>
            <a href="/mypage/watchlist?status=WATCHED"
               class="watchlist-tab ${currentStatus == 'WATCHED' ? 'active' : ''}">
                봤어요 <span class="tab-count">${watchedCount}</span>
            </a>
        </div>

        <!-- 콘텐츠 그리드 -->
        <c:choose>
            <c:when test="${not empty items}">
                <div class="watchlist-grid">
                    <c:forEach var="item" items="${items}">
                        <a href="/content/${item.content.id}" class="content-card">
                            <div class="card-poster">
                                <c:choose>
                                    <c:when test="${not empty item.content.posterUrl}">
                                        <img src="${item.content.posterUrl}" alt="<c:out value='${item.content.title}'/>">
                                    </c:when>
                                    <c:otherwise>
                                        <span>${item.content.title.substring(0, Math.min(4, item.content.title.length()))}</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="card-title"><c:out value="${item.content.title}"/></div>
                            <div class="card-year">${item.content.releaseYear} · ${item.content.contentType}</div>
                        </a>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <p>아직 콘텐츠가 없습니다.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>
