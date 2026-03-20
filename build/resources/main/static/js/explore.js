/**
 * 탐색 페이지 스크립트
 */
(function() {
    var FILTER_ALL = 'ALL';
    var SCROLL_THRESHOLD = 500;
    var CATEGORIES = [];
    var OTT_PROVIDERS = [];

    var grid = document.getElementById('exploreGrid');
    var loading = document.getElementById('exploreLoading');
    var emptyState = document.getElementById('exploreEmpty');
    var currentCategory = null;
    var currentProvider = '';
    var currentSort = 'popularity.desc';
    var page = 0;
    var isLoading = false;
    var hasMore = true;

    // 필터 상태
    var filterState = {
        genres: [],
        country: [],
        year: '',
        voteMin: '',
        certification: ''
    };

    // 모달 내부 임시 상태
    var tempFilter = {
        genres: [],
        country: [],
        year: '',
        voteMin: '',
        certification: ''
    };

    // 고정 데이터
    var COUNTRIES = [
        { code: 'KR', name: '한국' },
        { code: 'US', name: '미국' },
        { code: 'JP', name: '일본' },
        { code: 'GB', name: '영국' },
        { code: 'FR', name: '프랑스' },
        { code: 'DE', name: '독일' },
        { code: 'ES', name: '스페인' },
        { code: 'IN', name: '인도' },
        { code: 'CN', name: '중국' },
        { code: 'TW', name: '대만' }
    ];

    var YEARS = [
        { value: '2025', label: '2025' },
        { value: '2024', label: '2024' },
        { value: '2023', label: '2023' },
        { value: '2022', label: '2022' },
        { value: '2021', label: '2021' },
        { value: '2020', label: '2020' },
        { value: '2010s', label: '2010년대' },
        { value: '2000s', label: '2000년대' },
        { value: '~1999', label: '~1999' }
    ];

    var VOTE_OPTIONS = [
        { value: '9', label: '9점 이상' },
        { value: '8', label: '8점 이상' },
        { value: '7', label: '7점 이상' },
        { value: '6', label: '6점 이상' },
        { value: '5', label: '5점 이상' }
    ];

    var CERTIFICATIONS = [
        { value: 'All', label: '전체관람가' },
        { value: '12', label: '12세이상관람가' },
        { value: '15', label: '15세이상관람가' },
        { value: '18', label: '청소년관람불가' }
    ];

    // 모달 요소
    var filterModalOverlay = document.getElementById('filterModalOverlay');
    var filterBtn = document.getElementById('filterBtn');
    var filterModalClose = document.getElementById('filterModalClose');
    var filterResetBtn = document.getElementById('filterResetBtn');
    var filterApplyBtn = document.getElementById('filterApplyBtn');
    var filterTagsContainer = document.getElementById('filterTags');

    // DB에서 카테고리 + OTT 데이터 fetch 후 초기화
    Promise.all([
        MyKino.fetchJson('/api/public/tmdb/categories'),
        MyKino.fetchJson('/api/public/tmdb/ott-providers')
    ]).then(function(results) {
        CATEGORIES = results[0];
        OTT_PROVIDERS = results[1];
        currentCategory = CATEGORIES[0];
        initFilters();
        loadMore();
    }).catch(function(err) {
        console.error('초기 데이터 로드 실패:', err);
    });

    function initFilters() {
        // OTT 이미지 버튼 렌더링
        var ottFilter = document.getElementById('ottFilter');
        var allOtt = document.createElement('button');
        allOtt.className = 'ott-img-chip active';
        allOtt.setAttribute('data-provider', '');
        allOtt.textContent = '전체';
        ottFilter.appendChild(allOtt);

        for (var j = 0; j < OTT_PROVIDERS.length; j++) {
            var btn = document.createElement('button');
            btn.className = 'ott-img-chip';
            btn.setAttribute('data-provider', OTT_PROVIDERS[j].id);
            btn.setAttribute('title', OTT_PROVIDERS[j].name);
            if (OTT_PROVIDERS[j].logoUrl) {
                var img = document.createElement('img');
                img.src = OTT_PROVIDERS[j].logoUrl;
                img.alt = OTT_PROVIDERS[j].name;
                btn.appendChild(img);
            } else {
                btn.textContent = OTT_PROVIDERS[j].name;
            }
            ottFilter.appendChild(btn);
        }

        // 카테고리 칩 렌더링
        var catFilter = document.getElementById('categoryFilter');
        for (var i = 0; i < CATEGORIES.length; i++) {
            var catBtn = document.createElement('button');
            catBtn.className = 'category-chip' + (i === 0 ? ' active' : '');
            catBtn.setAttribute('data-category', CATEGORIES[i].key);
            catBtn.textContent = CATEGORIES[i].label;
            catFilter.appendChild(catBtn);
        }

        // 고정 필터 칩 렌더링
        renderStaticChips();
    }

    function renderStaticChips() {
        // 국가
        var countryContainer = document.getElementById('filterCountryChips');
        for (var i = 0; i < COUNTRIES.length; i++) {
            var btn = document.createElement('button');
            btn.className = 'filter-chip';
            btn.setAttribute('data-value', COUNTRIES[i].code);
            btn.textContent = COUNTRIES[i].name;
            countryContainer.appendChild(btn);
        }

        // 공개연도
        var yearContainer = document.getElementById('filterYearChips');
        for (var i = 0; i < YEARS.length; i++) {
            var btn = document.createElement('button');
            btn.className = 'filter-chip';
            btn.setAttribute('data-value', YEARS[i].value);
            btn.textContent = YEARS[i].label;
            yearContainer.appendChild(btn);
        }

        // TMDB 평점
        var voteContainer = document.getElementById('filterVoteChips');
        for (var i = 0; i < VOTE_OPTIONS.length; i++) {
            var btn = document.createElement('button');
            btn.className = 'filter-chip';
            btn.setAttribute('data-value', VOTE_OPTIONS[i].value);
            btn.textContent = VOTE_OPTIONS[i].label;
            voteContainer.appendChild(btn);
        }

        // 관람등급
        var certContainer = document.getElementById('filterCertChips');
        for (var i = 0; i < CERTIFICATIONS.length; i++) {
            var btn = document.createElement('button');
            btn.className = 'filter-chip';
            btn.setAttribute('data-value', CERTIFICATIONS[i].value);
            btn.textContent = CERTIFICATIONS[i].label;
            certContainer.appendChild(btn);
        }
    }

    function renderGenreChips() {
        var container = document.getElementById('filterGenreChips');
        container.innerHTML = '';

        if (!currentCategory) return;
        var subs = currentCategory.subGenres;

        for (var i = 0; i < subs.length; i++) {
            var btn = document.createElement('button');
            btn.className = 'filter-chip';
            btn.setAttribute('data-value', String(subs[i].id));
            btn.textContent = subs[i].name;
            if (arrayContains(tempFilter.genres, String(subs[i].id))) {
                btn.classList.add('selected');
            }
            container.appendChild(btn);
        }

        // 장르가 없으면 섹션 숨기기
        document.getElementById('filterGenreSection').style.display = subs.length === 0 ? 'none' : '';
    }

    // 모달 열기
    filterBtn.addEventListener('click', function() {
        tempFilter = {
            genres: filterState.genres.slice(),
            country: filterState.country.slice(),
            year: filterState.year,
            voteMin: filterState.voteMin,
            certification: filterState.certification
        };
        renderGenreChips();
        syncAllChipStates();
        syncSelectAllButtons();
        updateCertVisibility();
        filterModalOverlay.classList.add('open');
    });

    // 모달 닫기
    filterModalClose.addEventListener('click', function() {
        filterModalOverlay.classList.remove('open');
    });

    filterModalOverlay.addEventListener('click', function(e) {
        if (e.target === filterModalOverlay) {
            filterModalOverlay.classList.remove('open');
        }
    });

    // 초기화
    filterResetBtn.addEventListener('click', function() {
        tempFilter = { genres: [], country: [], year: '', voteMin: '', certification: '' };
        renderGenreChips();
        syncAllChipStates();
        syncSelectAllButtons();
    });

    // 적용
    filterApplyBtn.addEventListener('click', function() {
        filterState = {
            genres: tempFilter.genres.slice(),
            country: tempFilter.country.slice(),
            year: tempFilter.year,
            voteMin: tempFilter.voteMin,
            certification: tempFilter.certification
        };
        filterModalOverlay.classList.remove('open');
        renderFilterTags();
        resetAndLoad();
    });

    // "전체 선택" 버튼 클릭 (이벤트 위임)
    document.getElementById('filterModalBody').addEventListener('click', function(e) {
        var allBtn = e.target.closest('.filter-select-all');
        if (allBtn) {
            var sectionId = allBtn.getAttribute('data-section');
            var type = allBtn.getAttribute('data-type');
            var section = document.getElementById(sectionId);

            if (type === 'multi') {
                var allValues = getAllChipValues(section);
                var arr = (sectionId === 'filterGenreSection') ? tempFilter.genres : tempFilter.country;
                // 이미 전체 선택 → 해제, 아니면 → 전체 선택
                if (arr.length === allValues.length) {
                    arr.length = 0;
                } else {
                    arr.length = 0;
                    for (var i = 0; i < allValues.length; i++) arr.push(allValues[i]);
                }
                updateChipsUI(section, arr, true);
            } else {
                var key = allBtn.getAttribute('data-key');
                // 이미 전체 선택 → 해제, 아니면 → 전체 선택
                tempFilter[key] = (tempFilter[key] === FILTER_ALL) ? '' : FILTER_ALL;
                updateChipsUI(section, tempFilter[key], false);
            }
            syncSelectAllButtons();
            return;
        }

        // 필터 칩 클릭
        var chip = e.target.closest('.filter-chip');
        if (!chip) return;

        var section = chip.closest('.filter-section');
        if (!section) return;
        var sectionId = section.id;
        var value = chip.getAttribute('data-value');

        if (sectionId === 'filterGenreSection') {
            toggleMulti(tempFilter.genres, value, section);
        } else if (sectionId === 'filterCountrySection') {
            toggleMulti(tempFilter.country, value, section);
        } else if (sectionId === 'filterYearSection') {
            toggleSingle('year', value, section);
        } else if (sectionId === 'filterVoteSection') {
            toggleSingle('voteMin', value, section);
        } else if (sectionId === 'filterCertSection') {
            toggleSingle('certification', value, section);
        }
        syncSelectAllButtons();
    });

    function toggleMulti(arr, value, section) {
        var idx = arrayIndexOf(arr, value);
        if (idx !== -1) {
            arr.splice(idx, 1);
        } else {
            arr.push(value);
        }
        updateChipsUI(section, arr, true);
    }

    function toggleSingle(key, value, section) {
        tempFilter[key] = (tempFilter[key] === value) ? '' : value;
        updateChipsUI(section, tempFilter[key], false);
    }

    function updateChipsUI(section, state, isMulti) {
        var chips = section.querySelectorAll('.filter-chip');
        chips.forEach(function(chip) {
            var v = chip.getAttribute('data-value');
            if (isMulti) {
                chip.classList.toggle('selected', arrayContains(state, v));
            } else {
                // FILTER_ALL → 모든 칩 선택, '' → 아무것도 선택 안됨, 값 → 해당 칩만
                chip.classList.toggle('selected', state === FILTER_ALL || v === state);
            }
        });
    }

    function syncAllChipStates() {
        updateChipsUI(document.getElementById('filterCountrySection'), tempFilter.country, true);
        updateChipsUI(document.getElementById('filterYearSection'), tempFilter.year, false);
        updateChipsUI(document.getElementById('filterVoteSection'), tempFilter.voteMin, false);
        updateChipsUI(document.getElementById('filterCertSection'), tempFilter.certification, false);
    }

    function syncSelectAllButtons() {
        var allBtns = document.querySelectorAll('.filter-select-all');
        allBtns.forEach(function(btn) {
            var sectionId = btn.getAttribute('data-section');
            var type = btn.getAttribute('data-type');
            var section = document.getElementById(sectionId);
            var isActive;

            if (type === 'multi') {
                var totalChips = section.querySelectorAll('.filter-chip').length;
                if (sectionId === 'filterGenreSection') {
                    isActive = totalChips > 0 && tempFilter.genres.length === totalChips;
                } else {
                    isActive = totalChips > 0 && tempFilter.country.length === totalChips;
                }
            } else {
                var key = btn.getAttribute('data-key');
                isActive = tempFilter[key] === FILTER_ALL;
            }

            btn.classList.toggle('active', isActive);
        });
    }

    function updateCertVisibility() {
        var certSection = document.getElementById('filterCertSection');
        // 관람등급은 TV 전용 카테고리에서만 숨김
        if (currentCategory && currentCategory.mediaType === 'tv') {
            certSection.style.display = 'none';
        } else {
            certSection.style.display = '';
        }
    }

    // 활성 필터 태그 렌더링
    function renderFilterTags() {
        filterTagsContainer.innerHTML = '';
        var hasFilters = false;

        // 장르 (전체 선택이면 태그 생략)
        var genreSection = document.getElementById('filterGenreSection');
        var allGenreCount = genreSection ? genreSection.querySelectorAll('.filter-chip').length : 0;
        if (filterState.genres.length > 0 && filterState.genres.length < allGenreCount) {
            for (var i = 0; i < filterState.genres.length; i++) {
                appendTag(getGenreName(filterState.genres[i]));
                hasFilters = true;
            }
        }

        // 국가 (전체 선택이면 태그 생략)
        if (filterState.country.length > 0 && filterState.country.length < COUNTRIES.length) {
            for (var i = 0; i < filterState.country.length; i++) {
                appendTag(getCountryName(filterState.country[i]));
                hasFilters = true;
            }
        }

        // 연도
        if (filterState.year && filterState.year !== FILTER_ALL) {
            appendTag(getYearLabel(filterState.year));
            hasFilters = true;
        }

        // 평점
        if (filterState.voteMin && filterState.voteMin !== FILTER_ALL) {
            appendTag(filterState.voteMin + '점 이상');
            hasFilters = true;
        }

        // 관람등급
        if (filterState.certification && filterState.certification !== FILTER_ALL) {
            appendTag(getCertLabel(filterState.certification));
            hasFilters = true;
        }

        filterTagsContainer.style.display = hasFilters ? 'flex' : 'none';
    }

    function appendTag(text) {
        var tag = document.createElement('span');
        tag.className = 'filter-tag';
        tag.textContent = text;
        filterTagsContainer.appendChild(tag);
    }

    function getGenreName(genreId) {
        if (!currentCategory) return genreId;
        var subs = currentCategory.subGenres;
        for (var i = 0; i < subs.length; i++) {
            if (String(subs[i].id) === genreId) return subs[i].name;
        }
        return genreId;
    }

    function getCountryName(code) {
        for (var i = 0; i < COUNTRIES.length; i++) {
            if (COUNTRIES[i].code === code) return COUNTRIES[i].name;
        }
        return code;
    }

    function getYearLabel(value) {
        for (var i = 0; i < YEARS.length; i++) {
            if (YEARS[i].value === value) return YEARS[i].label;
        }
        return value;
    }

    function getCertLabel(value) {
        for (var i = 0; i < CERTIFICATIONS.length; i++) {
            if (CERTIFICATIONS[i].value === value) return CERTIFICATIONS[i].label;
        }
        return value;
    }

    // 카테고리 클릭
    document.getElementById('categoryFilter').addEventListener('click', function(e) {
        var btn = e.target.closest('.category-chip');
        if (!btn) return;
        document.querySelectorAll('.category-chip').forEach(function(el) { el.classList.remove('active'); });
        btn.classList.add('active');

        var key = btn.getAttribute('data-category');
        for (var i = 0; i < CATEGORIES.length; i++) {
            if (CATEGORIES[i].key === key) {
                currentCategory = CATEGORIES[i];
                break;
            }
        }

        // 카테고리 변경 시 필터 초기화
        filterState = { genres: [], country: [], year: '', voteMin: '', certification: '' };
        renderFilterTags();
        resetAndLoad();
    });

    // OTT 클릭
    document.getElementById('ottFilter').addEventListener('click', function(e) {
        var btn = e.target.closest('.ott-img-chip');
        if (!btn) return;
        document.querySelectorAll('.ott-img-chip').forEach(function(el) { el.classList.remove('active'); });
        btn.classList.add('active');
        currentProvider = btn.getAttribute('data-provider');
        resetAndLoad();
    });

    // 정렬 드롭다운
    var sortToggle = document.getElementById('sortToggle');
    var sortMenu = document.getElementById('sortMenu');
    var sortDropdown = document.getElementById('sortDropdown');
    var sortLabel = document.getElementById('sortLabel');

    sortToggle.addEventListener('click', function() {
        sortDropdown.classList.toggle('open');
    });

    sortMenu.addEventListener('click', function(e) {
        var option = e.target.closest('.sort-option');
        if (!option) return;
        sortMenu.querySelectorAll('.sort-option').forEach(function(el) { el.classList.remove('active'); });
        option.classList.add('active');
        sortLabel.textContent = option.textContent;
        currentSort = option.getAttribute('data-sort');
        sortDropdown.classList.remove('open');
        resetAndLoad();
    });

    document.addEventListener('click', function(e) {
        if (!sortDropdown.contains(e.target)) {
            sortDropdown.classList.remove('open');
        }
    });

    function resetAndLoad() {
        page = 0;
        hasMore = true;
        grid.innerHTML = '';
        emptyState.style.display = 'none';
        loadMore();
    }

    function buildGenreParam(baseGenre, selectedGenres) {
        var genreStr = selectedGenres.length > 0 ? selectedGenres.join('|') : '';
        if (!baseGenre && !genreStr) return null;
        if (!baseGenre) return genreStr;
        if (!genreStr) return baseGenre;

        // baseGenre가 OR(|)이고 선택된 장르가 모두 그 안에 있으면 선택 장르만 사용
        if (baseGenre.indexOf('|') !== -1) {
            var parts = baseGenre.split('|');
            var allInBase = true;
            for (var i = 0; i < selectedGenres.length; i++) {
                var found = false;
                for (var j = 0; j < parts.length; j++) {
                    if (parts[j] === selectedGenres[i]) { found = true; break; }
                }
                if (!found) { allInBase = false; break; }
            }
            if (allInBase) return genreStr;
        }

        return baseGenre + ',' + genreStr;
    }

    function renderCard(item) {
        var escapedTitle = MyKino.escapeHtml(item.title);
        var poster = item.posterUrl
            ? '<img src="' + item.posterUrl + '" alt="' + escapedTitle + '">'
            : '<span>' + escapedTitle.substring(0, 4) + '</span>';
        var score = item.voteAverage
            ? '<div class="explore-score tmdb-score">' + item.voteAverage + '</div>'
            : '';
        return '<a href="/content/tmdb/' + item.tmdbId + '?type=' + (item.mediaType || 'movie') + '" class="explore-card">'
            + '<div class="explore-poster">' + poster + score + '</div>'
            + '<div class="explore-title">' + escapedTitle + '</div>'
            + '<div class="explore-meta">' + (item.releaseYear || '') + ' · ' + item.contentType + '</div>'
            + '</a>';
    }

    function loadMore() {
        if (isLoading || !hasMore || !currentCategory) return;
        isLoading = true;
        loading.style.display = 'flex';

        // 전체 선택된 경우 필터 미적용 처리
        var genreSection = document.getElementById('filterGenreSection');
        var genresToSend = filterState.genres;
        if (genreSection && filterState.genres.length === genreSection.querySelectorAll('.filter-chip').length) {
            genresToSend = [];
        }
        var countryToSend = filterState.country;
        if (filterState.country.length === COUNTRIES.length) {
            countryToSend = [];
        }

        var effectiveGenre = buildGenreParam(currentCategory.baseGenre, genresToSend);
        var url = '/api/public/tmdb/discover?sort=' + currentSort
                + '&page=' + page
                + '&mediaType=' + currentCategory.mediaType;
        if (effectiveGenre) {
            url += '&genre=' + encodeURIComponent(effectiveGenre);
        }
        if (currentProvider) {
            url += '&provider=' + currentProvider;
        }
        if (countryToSend.length > 0) {
            url += '&country=' + encodeURIComponent(countryToSend.join('|'));
        }
        if (filterState.year && filterState.year !== FILTER_ALL) {
            url += '&year=' + encodeURIComponent(filterState.year);
        }
        if (filterState.voteMin && filterState.voteMin !== FILTER_ALL) {
            url += '&voteMin=' + filterState.voteMin;
        }
        if (filterState.certification && filterState.certification !== FILTER_ALL) {
            url += '&certification=' + encodeURIComponent(filterState.certification);
        }

        MyKino.fetchJson(url)
            .then(function(data) {
                var items = data.content;
                if (page === 0 && items.length === 0) {
                    emptyState.style.display = 'block';
                }
                var html = '';
                for (var i = 0; i < items.length; i++) {
                    html += renderCard(items[i]);
                }
                grid.insertAdjacentHTML('beforeend', html);
                page++;
                hasMore = page < data.totalPages;
                isLoading = false;
                loading.style.display = 'none';
            })
            .catch(function() {
                isLoading = false;
                loading.style.display = 'none';
            });
    }

    // 유틸
    function getAllChipValues(section) {
        var values = [];
        var chips = section.querySelectorAll('.filter-chip');
        chips.forEach(function(chip) {
            values.push(chip.getAttribute('data-value'));
        });
        return values;
    }

    function arrayContains(arr, value) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] === value) return true;
        }
        return false;
    }

    function arrayIndexOf(arr, value) {
        for (var i = 0; i < arr.length; i++) {
            if (arr[i] === value) return i;
        }
        return -1;
    }

    // 무한스크롤
    window.addEventListener('scroll', function() {
        if ((window.innerHeight + window.scrollY) >= document.body.offsetHeight - SCROLL_THRESHOLD) {
            loadMore();
        }
    });
})();
