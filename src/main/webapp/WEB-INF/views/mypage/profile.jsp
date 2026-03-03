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
            <div class="profile-header">
                <div class="profile-avatar">
                    ${user.nickname.substring(0, 1)}
                </div>
                <div>
                    <div class="profile-name">${user.nickname}</div>
                    <div class="profile-email">${user.email}</div>
                </div>
            </div>

            <div class="profile-info">
                <div class="profile-info-row">
                    <span class="profile-info-label">닉네임</span>
                    <span class="profile-info-value">${user.nickname}</span>
                </div>
                <div class="profile-info-row">
                    <span class="profile-info-label">이메일</span>
                    <span class="profile-info-value">${user.email}</span>
                </div>
                <div class="profile-info-row">
                    <span class="profile-info-label">자기소개</span>
                    <span class="profile-info-value">
                        <c:choose>
                            <c:when test="${not empty user.bio}">${user.bio}</c:when>
                            <c:otherwise><span style="color:#666">아직 작성하지 않았습니다.</span></c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="profile-info-row">
                    <span class="profile-info-label">가입 방식</span>
                    <span class="profile-info-value">${user.provider}</span>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>
</body>
</html>
