-- ==========================================
-- 장르 데이터
-- ==========================================
INSERT INTO genres (name) VALUES ('액션');
INSERT INTO genres (name) VALUES ('로맨스');
INSERT INTO genres (name) VALUES ('코미디');
INSERT INTO genres (name) VALUES ('공포');
INSERT INTO genres (name) VALUES ('SF');
INSERT INTO genres (name) VALUES ('스릴러');
INSERT INTO genres (name) VALUES ('드라마');
INSERT INTO genres (name) VALUES ('판타지');
INSERT INTO genres (name) VALUES ('애니메이션');
INSERT INTO genres (name) VALUES ('다큐멘터리');
INSERT INTO genres (name) VALUES ('범죄');
INSERT INTO genres (name) VALUES ('미스터리');
INSERT INTO genres (name) VALUES ('음악');
INSERT INTO genres (name) VALUES ('전쟁');
INSERT INTO genres (name) VALUES ('역사');

-- ==========================================
-- TMDB 장르 ID → 장르 매핑
-- ==========================================
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 28 FROM genres WHERE name = '액션';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 12 FROM genres WHERE name = '액션';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 37 FROM genres WHERE name = '액션';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 10749 FROM genres WHERE name = '로맨스';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 35 FROM genres WHERE name = '코미디';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 27 FROM genres WHERE name = '공포';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 878 FROM genres WHERE name = 'SF';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 53 FROM genres WHERE name = '스릴러';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 18 FROM genres WHERE name = '드라마';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 10751 FROM genres WHERE name = '드라마';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 10770 FROM genres WHERE name = '드라마';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 14 FROM genres WHERE name = '판타지';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 16 FROM genres WHERE name = '애니메이션';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 99 FROM genres WHERE name = '다큐멘터리';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 80 FROM genres WHERE name = '범죄';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 9648 FROM genres WHERE name = '미스터리';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 10402 FROM genres WHERE name = '음악';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 10752 FROM genres WHERE name = '전쟁';
INSERT INTO genre_tmdb_mappings (genre_id, tmdb_genre_id) SELECT id, 36 FROM genres WHERE name = '역사';

-- ==========================================
-- OTT 플랫폼 데이터
-- ==========================================
INSERT INTO ott_platforms (name, logo_url, base_url, tmdb_provider_id) VALUES ('넷플릭스', '/images/ott/netflix.svg', 'https://www.netflix.com', 8);
INSERT INTO ott_platforms (name, logo_url, base_url, tmdb_provider_id) VALUES ('티빙', '/images/ott/tving.svg', 'https://www.tving.com', 1883);
INSERT INTO ott_platforms (name, logo_url, base_url, tmdb_provider_id) VALUES ('웨이브', '/images/ott/wavve.svg', 'https://www.wavve.com', 356);
INSERT INTO ott_platforms (name, logo_url, base_url, tmdb_provider_id) VALUES ('왓챠', '/images/ott/watcha.svg', 'https://www.watcha.com', 97);
INSERT INTO ott_platforms (name, logo_url, base_url, tmdb_provider_id) VALUES ('디즈니+', '/images/ott/disneyplus.svg', 'https://www.disneyplus.com', 337);
INSERT INTO ott_platforms (name, logo_url, base_url, tmdb_provider_id) VALUES ('극장', NULL, NULL, NULL);

-- ==========================================
-- 탐색 카테고리 데이터
-- ==========================================
INSERT INTO explore_categories (category_key, label, media_type, base_genre, sort_order) VALUES ('all', '전체', 'all', NULL, 1);
INSERT INTO explore_categories (category_key, label, media_type, base_genre, sort_order) VALUES ('movie', '영화', 'movie', NULL, 2);
INSERT INTO explore_categories (category_key, label, media_type, base_genre, sort_order) VALUES ('drama', '드라마', 'tv', '18', 3);
INSERT INTO explore_categories (category_key, label, media_type, base_genre, sort_order) VALUES ('animation', '애니메이션', 'all', '16', 4);
INSERT INTO explore_categories (category_key, label, media_type, base_genre, sort_order) VALUES ('variety', '예능', 'tv', '10764', 5);
INSERT INTO explore_categories (category_key, label, media_type, base_genre, sort_order) VALUES ('current_affairs', '시사/교양', 'tv', '99|10763', 6);

-- 전체 서브장르
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 28, '액션', 1 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 16, '애니메이션', 2 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 35, '코미디', 3 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 80, '범죄', 4 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 99, '다큐멘터리', 5 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 18, '드라마', 6 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 14, '판타지', 7 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 36, '역사', 8 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 27, '공포', 9 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10402, '음악', 10 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 9648, '미스터리', 11 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10749, '로맨스', 12 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 878, 'SF', 13 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 53, '스릴러', 14 FROM explore_categories WHERE category_key = 'all';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10752, '전쟁', 15 FROM explore_categories WHERE category_key = 'all';

-- 영화 서브장르
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 28, '액션', 1 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 16, '애니메이션', 2 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 35, '코미디', 3 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 80, '범죄', 4 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 99, '다큐멘터리', 5 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 18, '드라마', 6 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 14, '판타지', 7 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 36, '역사', 8 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 27, '공포', 9 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10402, '음악', 10 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 9648, '미스터리', 11 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10749, '로맨스', 12 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 878, 'SF', 13 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 53, '스릴러', 14 FROM explore_categories WHERE category_key = 'movie';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10752, '전쟁', 15 FROM explore_categories WHERE category_key = 'movie';

-- 드라마 서브장르
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 80, '범죄', 1 FROM explore_categories WHERE category_key = 'drama';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 9648, '미스터리', 2 FROM explore_categories WHERE category_key = 'drama';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10749, '로맨스', 3 FROM explore_categories WHERE category_key = 'drama';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 35, '코미디', 4 FROM explore_categories WHERE category_key = 'drama';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10765, 'SF/판타지', 5 FROM explore_categories WHERE category_key = 'drama';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10768, '전쟁/정치', 6 FROM explore_categories WHERE category_key = 'drama';

-- 애니메이션 서브장르
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 28, '액션', 1 FROM explore_categories WHERE category_key = 'animation';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 35, '코미디', 2 FROM explore_categories WHERE category_key = 'animation';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 14, '판타지', 3 FROM explore_categories WHERE category_key = 'animation';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 878, 'SF', 4 FROM explore_categories WHERE category_key = 'animation';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10749, '로맨스', 5 FROM explore_categories WHERE category_key = 'animation';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 18, '드라마', 6 FROM explore_categories WHERE category_key = 'animation';

-- 예능 서브장르
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 35, '코미디', 1 FROM explore_categories WHERE category_key = 'variety';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10767, '토크', 2 FROM explore_categories WHERE category_key = 'variety';

-- 시사/교양 서브장르
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 99, '다큐멘터리', 1 FROM explore_categories WHERE category_key = 'current_affairs';
INSERT INTO explore_category_genres (category_id, tmdb_genre_id, genre_name, sort_order) SELECT id, 10763, '뉴스', 2 FROM explore_categories WHERE category_key = 'current_affairs';

-- ==========================================
-- 테스트 사용자 (비밀번호: test1234 → BCrypt)
-- ==========================================
INSERT INTO users (email, password, nickname, bio, role, provider, created_at, updated_at)
VALUES ('test@mykino.com', '$2a$10$p5hG6nryEV4slfoCm0AfVuCfGzaRU80mZLKKnLFjR3E9xZqKBq1gi', '테스트유저', '영화를 사랑하는 사람', 'USER', 'LOCAL', NOW(), NOW());

INSERT INTO users (email, password, nickname, bio, role, provider, created_at, updated_at)
VALUES ('admin@mykino.com', '$2a$10$p5hG6nryEV4slfoCm0AfVuCfGzaRU80mZLKKnLFjR3E9xZqKBq1gi', '관리자', '서비스 관리자', 'ADMIN', 'LOCAL', NOW(), NOW());
