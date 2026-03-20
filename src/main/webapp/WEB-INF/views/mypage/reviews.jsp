<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>내 리뷰 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/profile.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <h1 class="page-title">내 리뷰</h1>

        <c:choose>
            <c:when test="${not empty reviews.content}">
                <div class="mypage-review-list">
                    <c:forEach var="review" items="${reviews.content}">
                        <a href="/content/${review.content.id}" class="mypage-review-card">
                            <div class="mypage-review-poster">
                                <c:if test="${not empty review.content.posterUrl}">
                                    <img src="${review.content.posterUrl}" alt="<c:out value='${review.content.title}'/>">
                                </c:if>
                            </div>
                            <div class="mypage-review-body">
                                <div class="mypage-review-content-title"><c:out value="${review.content.title}"/></div>
                                <c:if test="${review.hasSpoiler}">
                                    <span class="spoiler-tag">스포일러</span>
                                </c:if>
                                <p class="mypage-review-text"><c:out value="${review.body}"/></p>
                                <div class="mypage-review-meta">
                                    <fmt:formatDate value="${review.createdAt}" pattern="yyyy.MM.dd"/>
                                </div>
                            </div>
                        </a>
                    </c:forEach>
                </div>

                <!-- 페이징 -->
                <c:if test="${reviews.totalPages > 1}">
                    <div class="pagination">
                        <c:forEach var="i" begin="0" end="${reviews.totalPages - 1}">
                            <a href="/mypage/reviews?page=${i}"
                               class="page-link ${reviews.number == i ? 'active' : ''}">${i + 1}</a>
                        </c:forEach>
                    </div>
                </c:if>
            </c:when>
            <c:otherwise>
                <div class="empty-state">
                    <p>작성한 리뷰가 없습니다.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>
