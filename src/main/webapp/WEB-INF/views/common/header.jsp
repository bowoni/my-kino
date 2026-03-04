<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<header class="header">
    <div class="header-inner">
        <a href="/" class="logo">MyKino</a>

        <nav class="nav">
            <a href="/search" class="nav-link nav-search" aria-label="검색">
                <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                    <circle cx="11" cy="11" r="8"/>
                    <line x1="21" y1="21" x2="16.65" y2="16.65"/>
                </svg>
            </a>
            <a href="/explore" class="nav-link">탐색</a>
            <sec:authorize access="isAnonymous()">
                <a href="/login" class="nav-link">로그인</a>
                <a href="/signup" class="nav-link btn-signup">회원가입</a>
            </sec:authorize>

            <sec:authorize access="isAuthenticated()">
                <span class="nav-user">
                    <sec:authentication property="principal.user.nickname" />님
                </span>
                <a href="/mypage" class="nav-link">마이페이지</a>
                <form action="/logout" method="post" class="logout-form">
                    <button type="submit" class="nav-link btn-logout">로그아웃</button>
                </form>
            </sec:authorize>
        </nav>
    </div>
</header>
