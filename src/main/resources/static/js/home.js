/**
 * MyKino - 홈 페이지
 */
(function() {
    var heroInput = document.getElementById('heroSearchInput');
    var heroAC = document.getElementById('heroAutocomplete');

    var handleAutocomplete = MyKino.debounce(function() {
        var q = heroInput.value.trim();
        if (q.length < 1) { heroAC.style.display = 'none'; return; }

        MyKino.fetchJson('/api/public/autocomplete?q=' + encodeURIComponent(q))
            .then(function(data) {
                if (data.length === 0) { heroAC.style.display = 'none'; return; }
                var h = '';
                for (var i = 0; i < data.length; i++) {
                    var d = data[i];
                    var title = MyKino.escapeHtml(d.title);
                    var img = d.posterUrl
                        ? '<img src="' + d.posterUrl + '">'
                        : '<span>' + title.substring(0,1) + '</span>';
                    h += '<a href="/content/tmdb/' + d.tmdbId + '?type=' + (d.mediaType || 'movie') + '" class="hero-ac-item">'
                        + '<div class="hero-ac-poster">' + img + '</div>'
                        + '<div class="hero-ac-info"><div class="hero-ac-title">' + title + '</div>'
                        + '<div class="hero-ac-meta">' + (d.releaseYear||'') + ' · ' + d.contentType + '</div></div></a>';
                }
                heroAC.innerHTML = h;
                heroAC.style.display = 'block';
            });
    }, 300);

    heroInput.addEventListener('input', handleAutocomplete);

    document.addEventListener('click', function(e) {
        if (!heroInput.contains(e.target) && !heroAC.contains(e.target)) {
            heroAC.style.display = 'none';
        }
    });

    document.getElementById('heroSearchForm').addEventListener('submit', function(e) {
        e.preventDefault();
        var q = heroInput.value.trim();
        if (q.length > 0) {
            location.href = '/search?q=' + encodeURIComponent(q);
        }
    });

    // === 공통 카드 렌더 ===
    function renderCard(item) {
        var title = MyKino.escapeHtml(item.title);
        var poster = item.posterUrl
            ? '<img src="' + item.posterUrl + '" alt="' + title + '">'
            : '<span>' + title.substring(0, 4) + '</span>';
        var score = item.voteAverage
            ? '<div class="card-score tmdb-score">' + item.voteAverage + '</div>'
            : '';
        return '<a href="/content/tmdb/' + item.tmdbId + '?type=' + (item.mediaType || 'movie') + '" class="content-card">'
            + '<div class="card-poster">' + poster + score + '</div>'
            + '<div class="card-title">' + title + '</div>'
            + '<div class="card-year">' + (item.releaseYear || '') + ' · ' + item.contentType + '</div>'
            + '</a>';
    }

    // === 오늘 이거? ===
    function loadTodayPick() {
        MyKino.fetchJson('/api/public/tmdb/today-pick')
            .then(function(data) {
                if (!data || !data.title) return;
                var section = document.getElementById('todayPickSection');
                var card = document.getElementById('todayPickCard');
                var title = MyKino.escapeHtml(data.title);
                var bg = data.backdropUrl
                    ? 'background-image: url(\'' + data.backdropUrl + '\')'
                    : '';
                var synopsis = data.synopsis ? MyKino.escapeHtml(data.synopsis) : '';
                if (synopsis.length > 120) synopsis = synopsis.substring(0, 120) + '...';

                card.innerHTML = '<a href="/content/tmdb/' + data.tmdbId + '?type=' + (data.mediaType || 'movie') + '" class="today-pick-link">'
                    + '<div class="today-pick-bg" style="' + bg + '">'
                    + '<div class="today-pick-overlay">'
                    + '<div class="today-pick-info">'
                    + '<span class="today-pick-type">' + (data.contentType || '') + '</span>'
                    + '<h3 class="today-pick-title">' + title + '</h3>'
                    + (synopsis ? '<p class="today-pick-synopsis">' + synopsis + '</p>' : '')
                    + '</div></div></div></a>';
                section.style.display = 'block';
            });
    }

    loadTodayPick();
    var refreshBtn = document.getElementById('todayPickRefresh');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', function() {
            this.classList.add('spinning');
            var self = this;
            loadTodayPick();
            setTimeout(function() { self.classList.remove('spinning'); }, 600);
        });
    }

    // === 내가 찜한 콘텐츠 ===
    var watchlistSection = document.getElementById('myWatchlistSection');
    if (watchlistSection) {
        MyKino.fetchJson('/api/home/my-watchlist')
            .then(function(data) {
                if (!data || data.length === 0) return;
                var scroll = document.getElementById('myWatchlistScroll');
                var html = '';
                for (var i = 0; i < data.length; i++) {
                    var item = data[i];
                    var title = MyKino.escapeHtml(item.title);
                    var poster = item.posterUrl
                        ? '<img src="' + item.posterUrl + '" alt="' + title + '">'
                        : '<span>' + title.substring(0, 4) + '</span>';
                    html += '<a href="/content/' + item.contentId + '" class="scroll-card">'
                        + '<div class="scroll-poster">' + poster + '</div>'
                        + '<div class="scroll-title">' + title + '</div>'
                        + '</a>';
                }
                scroll.innerHTML = html;
                watchlistSection.style.display = 'block';
            })
            .catch(function() {});
    }

    // === 개봉 예정작 ===
    MyKino.fetchJson('/api/public/tmdb/upcoming?page=0')
        .then(function(data) {
            if (!data || !data.content || data.content.length === 0) return;
            var scroll = document.getElementById('upcomingScroll');
            var html = '';
            var items = data.content;
            for (var i = 0; i < Math.min(items.length, 20); i++) {
                var item = items[i];
                var title = MyKino.escapeHtml(item.title);
                var poster = item.posterUrl
                    ? '<img src="' + item.posterUrl + '" alt="' + title + '">'
                    : '<span>' + title.substring(0, 4) + '</span>';
                html += '<a href="/content/tmdb/' + item.tmdbId + '?type=movie" class="scroll-card">'
                    + '<div class="scroll-poster">' + poster + '</div>'
                    + '<div class="scroll-title">' + title + '</div>'
                    + '<div class="scroll-meta">' + (item.releaseYear || '') + '</div>'
                    + '</a>';
            }
            scroll.innerHTML = html;
            document.getElementById('upcomingSection').style.display = 'block';
        });

    // === 최신 리뷰 한줄평 ===
    MyKino.fetchJson('/api/public/home/latest-reviews')
        .then(function(data) {
            if (!data || data.length === 0) return;
            var scroll = document.getElementById('latestReviewScroll');
            var html = '';
            for (var i = 0; i < data.length; i++) {
                var r = data[i];
                var body = MyKino.escapeHtml(r.body);
                if (body.length > 80) body = body.substring(0, 80) + '...';
                var nickname = MyKino.escapeHtml(r.nickname);
                var contentTitle = MyKino.escapeHtml(r.contentTitle);

                html += '<a href="/content/' + r.contentId + '" class="review-scroll-card">'
                    + '<div class="review-scroll-header">'
                    + (r.posterUrl ? '<img class="review-scroll-poster" src="' + r.posterUrl + '">' : '')
                    + '<div class="review-scroll-content-title">' + contentTitle + '</div>'
                    + '</div>'
                    + '<p class="review-scroll-body">' + (r.hasSpoiler ? '<span class="spoiler-tag">스포일러</span> ' : '') + body + '</p>'
                    + '<div class="review-scroll-footer">'
                    + '<span class="review-scroll-nickname">' + nickname + '</span>'
                    + '<span class="review-scroll-date">' + r.createdAt.substring(0, 10) + '</span>'
                    + '</div></a>';
            }
            scroll.innerHTML = html;
            document.getElementById('latestReviewsSection').style.display = 'block';
        });

    // === 가로 스크롤 섹션 로딩 ===
    function loadHorizontalSection(scrollId, sectionId, apiUrl) {
        MyKino.fetchJson(apiUrl + '?page=0')
            .then(function(data) {
                if (!data || !data.content || data.content.length === 0) return;
                var scroll = document.getElementById(scrollId);
                var html = '';
                var items = data.content;
                for (var i = 0; i < Math.min(items.length, 20); i++) {
                    var item = items[i];
                    var title = MyKino.escapeHtml(item.title);
                    var poster = item.posterUrl
                        ? '<img src="' + item.posterUrl + '" alt="' + title + '">'
                        : '<span>' + title.substring(0, 4) + '</span>';
                    var score = item.voteAverage
                        ? '<div class="scroll-score tmdb-score">' + item.voteAverage + '</div>'
                        : '';
                    html += '<a href="/content/tmdb/' + item.tmdbId + '?type=' + (item.mediaType || 'movie') + '" class="scroll-card">'
                        + '<div class="scroll-poster">' + poster + score + '</div>'
                        + '<div class="scroll-title">' + title + '</div>'
                        + '<div class="scroll-meta">' + (item.releaseYear || '') + ' · ' + item.contentType + '</div>'
                        + '</a>';
                }
                scroll.innerHTML = html;
                document.getElementById(sectionId).style.display = 'block';
            });
    }

    loadHorizontalSection('nowPlayingScroll', 'nowPlayingSection', '/api/public/tmdb/now-playing');
    loadHorizontalSection('popularScroll', 'popularSection', '/api/public/tmdb/popular');
})();
