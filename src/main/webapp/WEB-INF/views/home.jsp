<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>MyKino - OTT 통합검색</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/home.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <div class="hero">
            <h1 class="hero-title">어디서 볼 수 있을까?<br><span>MyKino</span>에서 찾아보세요</h1>
            <p class="hero-desc">넷플릭스, 티빙, 웨이브, 왓챠, 디즈니+ 한 번에 검색</p>
            <div class="hero-search">
                <form class="hero-search-form" id="heroSearchForm">
                    <input type="text" placeholder="영화, 드라마 제목을 검색하세요"
                           id="heroSearchInput" autocomplete="off">
                </form>
                <div class="hero-autocomplete" id="heroAutocomplete"></div>
            </div>
        </div>

        <sec:authorize access="isAuthenticated()">
            <!-- 내가 찜한 콘텐츠 -->
            <div class="home-section" id="myWatchlistSection" style="display:none;">
                <h2 class="home-section-title">내가 찜한 콘텐츠</h2>
                <div class="horizontal-scroll" id="myWatchlistScroll"></div>
            </div>
        </sec:authorize>

        <!-- 오늘 이거? -->
        <div class="home-section" id="todayPickSection" style="display:none;">
            <div class="section-header">
                <h2 class="home-section-title">오늘 이거?</h2>
                <button class="refresh-btn" id="todayPickRefresh" title="다른 추천">&#8635;</button>
            </div>
            <div class="today-pick-card" id="todayPickCard"></div>
        </div>

        <!-- 인기 콘텐츠 -->
        <div class="home-section" id="popularSection" style="display:none;">
            <h2 class="home-section-title">인기 콘텐츠</h2>
            <div class="horizontal-scroll" id="popularScroll"></div>
        </div>

        <!-- 신작 콘텐츠 -->
        <div class="home-section" id="nowPlayingSection" style="display:none;">
            <h2 class="home-section-title">신작 콘텐츠</h2>
            <div class="horizontal-scroll" id="nowPlayingScroll"></div>
        </div>

        <!-- 개봉 예정작 -->
        <div class="home-section" id="upcomingSection" style="display:none;">
            <h2 class="home-section-title">개봉 예정작</h2>
            <div class="horizontal-scroll" id="upcomingScroll"></div>
        </div>

        <!-- 최신 리뷰 한줄평 -->
        <div class="home-section" id="latestReviewsSection" style="display:none;">
            <h2 class="home-section-title">최신 리뷰 한줄평</h2>
            <div class="horizontal-scroll" id="latestReviewScroll"></div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script src="/js/util.js"></script>
    <script src="/js/home.js"></script>
</body>
</html>
