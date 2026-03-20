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

            <div class="social-divider">
                <span>또는</span>
            </div>

            <div class="social-login-btns">
                <a href="/oauth2/authorization/kakao" class="social-btn kakao-btn">
                    <svg class="social-icon" viewBox="0 0 24 24" fill="currentColor">
                        <path d="M12 3C6.48 3 2 6.36 2 10.44c0 2.62 1.74 4.93 4.38 6.25l-1.12 4.12c-.1.36.3.65.6.44l4.88-3.22c.42.04.84.07 1.26.07 5.52 0 10-3.36 10-7.66S17.52 3 12 3z"/>
                    </svg>
                    카카오로 시작하기
                </a>
                <a href="/oauth2/authorization/google" class="social-btn google-btn">
                    <svg class="social-icon" viewBox="0 0 24 24">
                        <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92a5.06 5.06 0 0 1-2.2 3.32v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.1z" fill="#4285F4"/>
                        <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
                        <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
                        <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
                    </svg>
                    Google로 시작하기
                </a>
            </div>

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
