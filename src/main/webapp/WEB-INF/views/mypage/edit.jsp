<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>프로필 수정 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/profile.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <h1 class="page-title">프로필 수정</h1>

        <div class="profile-card">
            <c:if test="${not empty error}">
                <div class="alert alert-error">${error}</div>
            </c:if>
            <c:if test="${not empty success}">
                <div class="alert alert-success">${success}</div>
            </c:if>

            <form action="/mypage/edit" method="post" enctype="multipart/form-data" class="edit-form">
                <!-- 프로필 이미지 -->
                <div class="edit-avatar-section">
                    <div class="edit-avatar" id="avatarPreview">
                        <c:choose>
                            <c:when test="${not empty user.profileImage}">
                                <img src="${user.profileImage}" alt="프로필" id="avatarImg">
                            </c:when>
                            <c:otherwise>
                                <span id="avatarInitial">${user.nickname.substring(0, 1)}</span>
                            </c:otherwise>
                        </c:choose>
                    </div>
                    <label for="profileImageFile" class="edit-avatar-btn">사진 변경</label>
                    <input type="file" id="profileImageFile" name="profileImageFile"
                           accept="image/*" style="display:none;">
                </div>

                <!-- 닉네임 -->
                <div class="edit-group">
                    <label class="edit-label" for="nickname">닉네임</label>
                    <input type="text" id="nickname" name="nickname" class="edit-input"
                           value="<c:out value='${user.nickname}'/>"
                           placeholder="2~10자로 입력하세요" required>
                    <span id="nicknameHint" class="edit-hint"></span>
                </div>

                <!-- 자기소개 -->
                <div class="edit-group">
                    <label class="edit-label" for="bio">자기소개</label>
                    <textarea id="bio" name="bio" class="edit-textarea"
                              rows="3" maxlength="200"
                              placeholder="자기소개를 입력하세요 (최대 200자)"><c:out value="${user.bio}"/></textarea>
                    <span class="edit-char-count"><span id="bioCount">${user.bio != null ? user.bio.length() : 0}</span>/200</span>
                </div>

                <!-- 이메일 (읽기전용) -->
                <div class="edit-group">
                    <label class="edit-label">이메일</label>
                    <input type="text" class="edit-input edit-readonly" value="<c:out value='${user.email}'/>" readonly>
                </div>

                <!-- 가입 방식 (읽기전용) -->
                <div class="edit-group">
                    <label class="edit-label">가입 방식</label>
                    <input type="text" class="edit-input edit-readonly" value="${user.provider}" readonly>
                </div>

                <button type="submit" class="btn-primary edit-submit">저장하기</button>
            </form>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script src="/js/util.js"></script>
    <script src="/js/mypage-edit.js"></script>
</body>
</html>
