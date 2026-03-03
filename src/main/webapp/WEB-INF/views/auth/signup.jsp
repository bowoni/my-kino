<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/auth.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="auth-container">
        <div class="auth-card">
            <h1 class="auth-title">회원가입</h1>
            <p class="auth-subtitle">MyKino와 함께 콘텐츠를 탐색하세요</p>

            <c:if test="${not empty errorMessage}">
                <div class="alert alert-error">${errorMessage}</div>
            </c:if>

            <form action="/signup" method="post" id="signupForm">
                <div class="form-group">
                    <label class="form-label" for="email">이메일</label>
                    <input type="email" id="email" name="email" class="form-input"
                           placeholder="이메일을 입력하세요" required
                           value="${signupRequest.email}">
                    <span id="emailHint" class="form-hint"></span>
                </div>

                <div class="form-group">
                    <label class="form-label" for="nickname">닉네임</label>
                    <input type="text" id="nickname" name="nickname" class="form-input"
                           placeholder="2~10자로 입력하세요" required
                           value="${signupRequest.nickname}">
                    <span id="nicknameHint" class="form-hint"></span>
                </div>

                <div class="form-group">
                    <label class="form-label" for="password">비밀번호</label>
                    <input type="password" id="password" name="password" class="form-input"
                           placeholder="4~20자로 입력하세요" required>
                </div>

                <div class="form-group">
                    <label class="form-label" for="passwordConfirm">비밀번호 확인</label>
                    <input type="password" id="passwordConfirm" name="passwordConfirm" class="form-input"
                           placeholder="비밀번호를 다시 입력하세요" required>
                    <span id="passwordHint" class="form-hint"></span>
                </div>

                <button type="submit" class="btn-primary">가입하기</button>
            </form>

            <div class="auth-links">
                이미 계정이 있으신가요? <a href="/login">로그인</a>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script src="https://code.jquery.com/jquery-3.7.1.min.js"></script>
    <script>
    $(function() {
        // 이메일 중복 체크
        var emailTimer;
        $('#email').on('keyup', function() {
            clearTimeout(emailTimer);
            var email = $(this).val();
            if (email.length < 5) {
                $('#emailHint').text('').removeClass('valid invalid');
                return;
            }
            emailTimer = setTimeout(function() {
                $.get('/api/public/check-email', { email: email }, function(available) {
                    if (available) {
                        $('#emailHint').text('사용 가능한 이메일입니다.').removeClass('invalid').addClass('valid');
                    } else {
                        $('#emailHint').text('이미 사용 중인 이메일입니다.').removeClass('valid').addClass('invalid');
                    }
                });
            }, 500);
        });

        // 닉네임 중복 체크
        var nicknameTimer;
        $('#nickname').on('keyup', function() {
            clearTimeout(nicknameTimer);
            var nickname = $(this).val();
            if (nickname.length < 2) {
                $('#nicknameHint').text('').removeClass('valid invalid');
                return;
            }
            nicknameTimer = setTimeout(function() {
                $.get('/api/public/check-nickname', { nickname: nickname }, function(available) {
                    if (available) {
                        $('#nicknameHint').text('사용 가능한 닉네임입니다.').removeClass('invalid').addClass('valid');
                    } else {
                        $('#nicknameHint').text('이미 사용 중인 닉네임입니다.').removeClass('valid').addClass('invalid');
                    }
                });
            }, 500);
        });

        // 비밀번호 확인 체크
        $('#passwordConfirm').on('keyup', function() {
            var pw = $('#password').val();
            var pwConfirm = $(this).val();
            if (pwConfirm.length === 0) {
                $('#passwordHint').text('').removeClass('valid invalid');
                return;
            }
            if (pw === pwConfirm) {
                $('#passwordHint').text('비밀번호가 일치합니다.').removeClass('invalid').addClass('valid');
            } else {
                $('#passwordHint').text('비밀번호가 일치하지 않습니다.').removeClass('valid').addClass('invalid');
            }
        });
    });
    </script>
</body>
</html>
