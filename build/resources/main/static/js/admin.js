/**
 * TMDB 관리 페이지 스크립트
 */
function showLoading() {
    document.getElementById('loading').style.display = 'block';
    document.getElementById('resultSection').style.display = 'none';
}

function hideLoading() {
    document.getElementById('loading').style.display = 'none';
}

function showResult(data) {
    hideLoading();
    var section = document.getElementById('resultSection');
    var content = document.getElementById('resultContent');

    var html = '<div class="result-summary">';
    html += '<div class="result-stat"><span class="stat-num">' + (data.totalFetched || 0) + '</span><span class="stat-label">조회됨</span></div>';
    html += '<div class="result-stat success"><span class="stat-num">' + (data.saved || 0) + '</span><span class="stat-label">새로 저장</span></div>';
    html += '<div class="result-stat skip"><span class="stat-num">' + (data.skipped || 0) + '</span><span class="stat-label">이미 존재</span></div>';
    html += '</div>';

    if (data.savedTitles && data.savedTitles.length > 0) {
        html += '<div class="result-list">';
        html += '<h3>저장된 영화</h3>';
        html += '<ul>';
        for (var i = 0; i < data.savedTitles.length; i++) {
            html += '<li>' + MyKino.escapeHtml(data.savedTitles[i]) + '</li>';
        }
        html += '</ul></div>';
    }

    if (data.errors && data.errors.length > 0) {
        html += '<div class="result-errors">';
        html += '<h3>오류</h3>';
        html += '<ul>';
        for (var i = 0; i < data.errors.length; i++) {
            html += '<li>' + MyKino.escapeHtml(data.errors[i]) + '</li>';
        }
        html += '</ul></div>';
    }

    if (data.message) {
        html += '<p class="result-message">' + data.message + '</p>';
    }

    content.innerHTML = html;
    section.style.display = 'block';
}

function syncPopular() {
    var page = document.getElementById('popularPage').value;
    showLoading();

    MyKino.fetchJson('/api/admin/tmdb/sync-popular?page=' + page, { method: 'POST' })
        .then(function(data) { showResult(data); })
        .catch(function(err) {
            hideLoading();
            alert('오류 발생: ' + err.message);
        });
}

function searchAndSync() {
    var keyword = document.getElementById('searchKeyword').value.trim();
    if (!keyword) {
        alert('검색어를 입력해주세요.');
        return;
    }
    showLoading();

    MyKino.fetchJson('/api/admin/tmdb/search?keyword=' + encodeURIComponent(keyword), { method: 'POST' })
        .then(function(data) { showResult(data); })
        .catch(function(err) {
            hideLoading();
            alert('오류 발생: ' + err.message);
        });
}
