<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:if test="${not empty keyword}"><c:out value="${keyword}"/> - </c:if>검색 - MyKino</title>
    <link rel="stylesheet" href="/css/common.css">
    <link rel="stylesheet" href="/css/search.css">
</head>
<body>
    <%@ include file="/WEB-INF/views/common/header.jsp" %>

    <div class="main-content">
        <!-- 탭 메뉴 -->
        <div class="search-tabs">
            <button class="search-tab active" data-tab="search">콘텐츠 검색</button>
            <button class="search-tab" data-tab="ai">AI 추천/질문</button>
        </div>

        <!-- 콘텐츠 검색 탭 -->
        <div class="tab-content active" id="tab-search">
            <!-- 검색바 -->
            <div class="search-bar">
                <form class="search-form" id="searchForm">
                    <input type="text" value="${fn:escapeXml(q)}" placeholder="영화, 드라마 제목을 검색하세요"
                           id="searchInput" autocomplete="off">
                    <button type="submit" class="search-btn">검색</button>
                </form>
                <div class="autocomplete-list" id="autocompleteList"></div>
            </div>

            <!-- 검색 결과 -->
            <c:if test="${not empty keyword}">
                <div class="search-info">
                    <h2>'<span class="highlight"><c:out value="${keyword}"/></span>' 검색 결과</h2>
                    <c:if test="${not empty results}">
                        <p class="result-count">총 ${results.totalElements}건</p>
                    </c:if>
                </div>

                <c:choose>
                    <c:when test="${not empty results && results.totalElements > 0}">
                        <div class="search-grid">
                            <c:forEach var="item" items="${results.content}">
                                <a href="/content/tmdb/${item.tmdbId}?type=${item.mediaType}" class="search-card">
                                    <div class="card-poster">
                                        <c:choose>
                                            <c:when test="${not empty item.posterUrl}">
                                                <img src="${item.posterUrl}" alt="${item.title}">
                                            </c:when>
                                            <c:otherwise>
                                                ${fn:substring(item.title, 0, 4)}
                                            </c:otherwise>
                                        </c:choose>
                                    </div>
                                    <div class="card-info">
                                        <div class="card-title">${item.title}</div>
                                        <c:if test="${not empty item.titleEng}">
                                            <div class="card-title-eng">${item.titleEng}</div>
                                        </c:if>
                                        <div class="card-meta">
                                            <c:if test="${not empty item.releaseYear}">
                                                <span>${item.releaseYear}</span>
                                            </c:if>
                                            <span>${item.contentType}</span>
                                        </div>
                                        <c:if test="${not empty item.synopsis}">
                                            <div class="card-synopsis">${fn:substring(item.synopsis, 0, 80)}...</div>
                                        </c:if>
                                    </div>
                                </a>
                            </c:forEach>
                        </div>

                        <!-- 페이징 -->
                        <c:if test="${results.totalPages > 1}">
                            <div class="pagination" id="pagination" data-keyword="${fn:escapeXml(q)}">
                                <c:if test="${results.number > 0}">
                                    <a href="#" data-page="${results.number - 1}" class="page-btn">&laquo; 이전</a>
                                </c:if>

                                <c:forEach begin="0" end="${results.totalPages - 1}" var="i">
                                    <c:choose>
                                        <c:when test="${i == results.number}">
                                            <span class="page-btn active">${i + 1}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="#" data-page="${i}" class="page-btn">${i + 1}</a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:forEach>

                                <c:if test="${results.number < results.totalPages - 1}">
                                    <a href="#" data-page="${results.number + 1}" class="page-btn">다음 &raquo;</a>
                                </c:if>
                            </div>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <div class="no-results">
                            <p class="no-results-icon">:(</p>
                            <p class="no-results-text">'<c:out value="${keyword}"/>'에 대한 검색 결과가 없습니다.</p>
                            <p class="no-results-hint">다른 검색어로 시도해보세요.</p>
                        </div>
                    </c:otherwise>
                </c:choose>
            </c:if>

            <!-- 검색어 미입력 시 -->
            <c:if test="${empty keyword}">
                <div class="search-empty">
                    <p>영화나 드라마 제목을 검색해보세요.</p>
                </div>
            </c:if>
        </div>

        <!-- AI 질문 탭 -->
        <div class="tab-content" id="tab-ai">
            <div class="ai-chat-container">
                <div class="ai-chat-messages" id="aiChatMessages">
                    <div class="ai-message ai-welcome">
                        <div class="ai-avatar">AI</div>
                        <div class="ai-bubble">
                            안녕하세요! 영화/드라마 AI 어시스턴트입니다.<br>
                            무엇이든 물어보세요. 추천, 줄거리, 배우 정보 등 도움을 드릴게요.
                        </div>
                    </div>
                </div>
                <div class="ai-chat-input-area">
                    <form class="ai-chat-form" id="aiChatForm">
                        <input type="text" id="aiChatInput" placeholder="영화/드라마에 대해 질문해보세요..." autocomplete="off">
                        <button type="submit" class="ai-send-btn" id="aiSendBtn">전송</button>
                    </form>
                </div>
            </div>
        </div>
    </div>

    <%@ include file="/WEB-INF/views/common/footer.jsp" %>

    <script src="/js/util.js"></script>
    <script src="/js/search.js"></script>
    <script src="/js/ai-chat.js"></script>
</body>
</html>
