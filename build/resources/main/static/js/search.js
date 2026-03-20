/**
 * MyKino - 검색 결과 페이지
 */
(function() {
    var searchInput = document.getElementById('searchInput');
    var autocompleteList = document.getElementById('autocompleteList');

    var handleAutocomplete = MyKino.debounce(function() {
        var query = searchInput.value.trim();

        if (query.length < 1) {
            autocompleteList.style.display = 'none';
            return;
        }

        MyKino.fetchJson('/api/public/autocomplete?q=' + encodeURIComponent(query))
            .then(function(data) {
                if (data.length === 0) {
                    autocompleteList.style.display = 'none';
                    return;
                }

                var html = '';
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    var title = MyKino.escapeHtml(item.title);
                    var poster = item.posterUrl
                        ? '<img src="' + item.posterUrl + '" alt="">'
                        : '<span class="ac-no-img">' + title.substring(0, 1) + '</span>';

                    html += '<a href="/content/tmdb/' + item.tmdbId + '?type=' + (item.mediaType || 'movie') + '" class="ac-item">'
                        + '<div class="ac-poster">' + poster + '</div>'
                        + '<div class="ac-info">'
                        + '<div class="ac-title">' + title + '</div>'
                        + '<div class="ac-meta">' + (item.releaseYear || '') + ' · ' + item.contentType + '</div>'
                        + '</div></a>';
                }
                autocompleteList.innerHTML = html;
                autocompleteList.style.display = 'block';
            });
    }, 300);

    searchInput.addEventListener('input', handleAutocomplete);

    // 바깥 클릭 시 자동완성 닫기
    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !autocompleteList.contains(e.target)) {
            autocompleteList.style.display = 'none';
        }
    });

    // 포커스 시 자동완성 다시 표시
    searchInput.addEventListener('focus', function() {
        if (autocompleteList.innerHTML.trim() !== '' && this.value.trim().length >= 1) {
            autocompleteList.style.display = 'block';
        }
    });

    // 폼 제출 시 %20 인코딩 적용
    document.getElementById('searchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        var q = searchInput.value.trim();
        if (q.length > 0) {
            location.href = '/search?q=' + encodeURIComponent(q);
        }
    });

    // 페이징 클릭 시 %20 인코딩 적용
    var pagination = document.getElementById('pagination');
    if (pagination) {
        pagination.addEventListener('click', function(e) {
            var link = e.target.closest('a[data-page]');
            if (link) {
                e.preventDefault();
                var keyword = pagination.getAttribute('data-keyword');
                var page = link.getAttribute('data-page');
                location.href = '/search?q=' + encodeURIComponent(keyword) + '&page=' + page;
            }
        });
    }
})();
