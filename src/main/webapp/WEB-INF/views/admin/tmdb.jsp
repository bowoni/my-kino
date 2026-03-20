<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>TMDB 데이터 관리 - MyKino Admin</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/admin.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <div class="admin-header">
            <h1>TMDB 데이터 관리</h1>
            <p>TMDB API를 통해 영화 데이터를 가져옵니다.</p>
        </div>

        <!-- 인기 영화 동기화 -->
        <div class="admin-section">
            <h2>인기 영화 가져오기</h2>
            <p class="section-desc">TMDB 인기 영화 목록에서 데이터를 가져옵니다. (페이지당 20편)</p>
            <div class="sync-controls">
                <label>페이지:
                    <select id="popularPage">
                        <option value="1">1 (1~20위)</option>
                        <option value="2">2 (21~40위)</option>
                        <option value="3">3 (41~60위)</option>
                        <option value="4">4 (61~80위)</option>
                        <option value="5">5 (81~100위)</option>
                    </select>
                </label>
                <button id="btnSyncPopular" class="btn-primary" onclick="syncPopular()">
                    인기 영화 가져오기
                </button>
            </div>
        </div>

        <!-- 검색 동기화 -->
        <div class="admin-section">
            <h2>영화 검색 후 가져오기</h2>
            <p class="section-desc">TMDB에서 영화를 검색하고 상위 5개를 가져옵니다.</p>
            <div class="sync-controls">
                <input type="text" id="searchKeyword" placeholder="영화 제목 입력..."
                       onkeypress="if(event.key==='Enter') searchAndSync()">
                <button id="btnSearch" class="btn-primary" onclick="searchAndSync()">
                    검색 후 가져오기
                </button>
            </div>
        </div>

        <!-- 결과 영역 -->
        <div class="admin-section" id="resultSection" style="display:none;">
            <h2>동기화 결과</h2>
            <div id="resultContent"></div>
        </div>

        <!-- 로딩 -->
        <div id="loading" style="display:none;">
            <div class="loading-spinner">
                <div class="spinner"></div>
                <p>TMDB에서 데이터를 가져오는 중...</p>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script src="/js/util.js"></script>
    <script src="/js/admin.js"></script>
</body>
</html>
