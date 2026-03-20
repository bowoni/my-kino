<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>마이페이지 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/profile.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <h1 class="page-title">마이페이지</h1>

        <div class="profile-card">
            <!-- 프로필 헤더 (클릭 → 수정 페이지) -->
            <a href="/mypage/edit" class="profile-header-link">
                <div class="profile-header">
                    <div class="profile-avatar">
                        <c:choose>
                            <c:when test="${not empty user.profileImage}">
                                <img src="${user.profileImage}" alt="프로필">
                            </c:when>
                            <c:otherwise>
                                ${user.nickname.substring(0, 1)}
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <div class="profile-header-info">
                        <div class="profile-name"><c:out value="${user.nickname}"/></div>
                        <div class="profile-email"><c:out value="${user.email}"/></div>
                    </div>
                    <span class="profile-chevron">&#8250;</span>
                </div>
            </a>

            <!-- 찜/보는중/봤어요 카운터 -->
            <div class="watch-stats">
                <a href="/mypage/watchlist?status=WANT_TO_WATCH" class="watch-stat-item">
                    <span class="watch-stat-count">${wantCount}</span>
                    <span class="watch-stat-label">찜했어요</span>
                </a>
                <a href="/mypage/watchlist?status=WATCHING" class="watch-stat-item">
                    <span class="watch-stat-count">${watchingCount}</span>
                    <span class="watch-stat-label">보는중</span>
                </a>
                <a href="/mypage/watchlist?status=WATCHED" class="watch-stat-item">
                    <span class="watch-stat-count">${watchedCount}</span>
                    <span class="watch-stat-label">봤어요</span>
                </a>
            </div>

            <!-- 리뷰 카운트 -->
            <a href="/mypage/reviews" class="review-stat-row">
                <span>작성한 리뷰 <strong>${reviewCount}</strong>개</span>
                <span class="profile-chevron">&#8250;</span>
            </a>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>
