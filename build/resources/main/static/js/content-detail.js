/**
 * 콘텐츠 상세 페이지 스크립트
 */
(function() {
    'use strict';

    var ratingEl = document.getElementById('myRating');
    var reviewListEl = document.getElementById('reviewList');
    var contentId = reviewListEl ? parseInt(reviewListEl.dataset.contentId) : null;

    if (!contentId) return;

    // === Watch Status (찜/보는중/봤어요) ===
    var watchBtnsContainer = document.getElementById('watchStatusBtns');
    if (watchBtnsContainer) {
        var watchBtns = watchBtnsContainer.querySelectorAll('.watch-btn');
        watchBtns.forEach(function(btn) {
            btn.addEventListener('click', function() {
                var status = this.getAttribute('data-status');
                MyKino.fetchJson('/api/watchlist', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ contentId: contentId, status: status })
                }).then(function(data) {
                    watchBtns.forEach(function(b) { b.classList.remove('active'); });
                    if (data.status) {
                        watchBtnsContainer.querySelector('[data-status="' + data.status + '"]')
                            .classList.add('active');
                    }
                }).catch(function(err) {
                    console.error('찜 상태 변경 실패:', err);
                });
            });
        });
    }

    // Read initial state from data-* attributes
    var selectedColor = (ratingEl && ratingEl.dataset.trafficColor) || null;
    var selectedScore = (ratingEl && ratingEl.dataset.score !== '')
        ? parseFloat(ratingEl.dataset.score)
        : null;

    // === Traffic Light Rating ===
    var trafficBtns = document.querySelectorAll('.traffic-btn');
    trafficBtns.forEach(function(btn) {
        btn.addEventListener('click', function() {
            trafficBtns.forEach(function(b) { b.classList.remove('active'); });
            this.classList.add('active');
            selectedColor = this.getAttribute('data-color');
        });
    });

    // === Star Rating ===
    var stars = document.querySelectorAll('.star');
    var scoreValue = document.getElementById('scoreValue');
    stars.forEach(function(star) {
        star.addEventListener('click', function() {
            selectedScore = parseInt(this.getAttribute('data-value'));
            scoreValue.textContent = selectedScore;
            stars.forEach(function(s) {
                s.classList.toggle('filled',
                    parseInt(s.getAttribute('data-value')) <= selectedScore);
            });
        });
        star.addEventListener('mouseenter', function() {
            var val = parseInt(this.getAttribute('data-value'));
            stars.forEach(function(s) {
                s.classList.toggle('hover',
                    parseInt(s.getAttribute('data-value')) <= val);
            });
        });
        star.addEventListener('mouseleave', function() {
            stars.forEach(function(s) { s.classList.remove('hover'); });
        });
    });

    // === Submit (Rating + Review 통합) ===
    var submitBtn = document.getElementById('submitBtn');
    if (submitBtn) {
        submitBtn.addEventListener('click', function() {
            if (!selectedColor) {
                alert('추천/보통/별로 중 하나를 선택해주세요.');
                return;
            }

            submitBtn.disabled = true;

            // 1. Rating 저장
            var ratingData = {
                contentId: contentId,
                trafficColor: selectedColor
            };
            if (selectedScore != null) {
                ratingData.score = selectedScore;
            }
            var ratingPromise = MyKino.fetchJson('/api/rating', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(ratingData)
            });

            // 2. 리뷰 텍스트가 있으면 Review도 저장
            var reviewText = document.getElementById('reviewBody').value.trim();
            var promises = [ratingPromise];

            if (reviewText) {
                var hasSpoiler = document.getElementById('reviewSpoiler').checked;
                var reviewPromise = MyKino.fetchJson('/api/review', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        contentId: contentId,
                        body: reviewText,
                        hasSpoiler: hasSpoiler
                    })
                });
                promises.push(reviewPromise);
            }

            Promise.all(promises)
                .then(function() {
                    alert('평가가 저장되었습니다.');
                    location.reload();
                })
                .catch(function(err) {
                    console.error('저장 실패:', err);
                    alert('저장에 실패했습니다.');
                    submitBtn.disabled = false;
                });
        });
    }

    // === Reviews ===
    var reviewPage = 0;
    var reviewTotal = 0;

    function loadReviews(page) {
        MyKino.fetchJson('/api/review/content/' + contentId + '?page=' + page)
            .then(function(data) {
                reviewTotal = data.totalPages;
                var list = reviewListEl;
                var html = '';

                if (data.content.length === 0 && page === 0) {
                    html = '<p class="no-reviews">아직 리뷰가 없습니다. 첫 리뷰를 남겨보세요!</p>';
                }

                for (var i = 0; i < data.content.length; i++) {
                    var r = data.content[i];
                    var escapedBody = MyKino.escapeHtml(r.body);
                    var escapedNickname = MyKino.escapeHtml(r.nickname);
                    var bodyText = r.hasSpoiler
                        ? '<span class="spoiler-tag">스포일러</span> <span class="spoiler-blur">' + escapedBody + '</span>'
                        : escapedBody;

                    html += '<div class="review-card">'
                        + '<div class="review-header">'
                        + '<span class="review-nickname">' + escapedNickname + '</span>'
                        + '<span class="review-date">' + r.createdAt.substring(0, 10) + '</span>'
                        + '</div>';
                    html += '<div class="review-body">' + bodyText + '</div>'
                        + '<div class="review-footer">'
                        + '<button class="review-like-btn" data-review-id="' + r.id + '">'
                        + '&#10084; ' + r.likeCount
                        + '</button>'
                        + '</div>'
                        + '</div>';
                }

                if (page === 0) {
                    list.innerHTML = html;
                } else {
                    list.innerHTML += html;
                }

                var moreDiv = document.getElementById('reviewMore');
                moreDiv.style.display = (page + 1 < reviewTotal) ? 'block' : 'none';
            })
            .catch(function(err) {
                console.error('리뷰 로드 실패:', err);
            });
    }

    loadReviews(0);

    document.getElementById('reviewMoreBtn').addEventListener('click', function() {
        reviewPage++;
        loadReviews(reviewPage);
    });

    // === Review Like (delegate) ===
    reviewListEl.addEventListener('click', function(e) {
        var btn = e.target.closest('.review-like-btn');
        if (!btn) return;

        var reviewId = btn.getAttribute('data-review-id');
        MyKino.fetchJson('/api/review/' + reviewId + '/like', { method: 'POST' })
            .then(function(data) {
                reviewPage = 0;
                loadReviews(0);
            })
            .catch(function(err) {
                console.error('좋아요 실패:', err);
            });
    });
})();
