package com.mykino.service;

import com.mykino.entity.*;
import com.mykino.enums.CastRoleType;
import com.mykino.enums.ContentType;
import com.mykino.repository.CastMemberRepository;
import com.mykino.repository.ContentRepository;
import com.mykino.repository.GenreTmdbMappingRepository;
import com.mykino.repository.OttPlatformRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class TmdbService {

    private final RestTemplate restTemplate;
    private final ContentRepository contentRepository;
    private final GenreTmdbMappingRepository genreTmdbMappingRepository;
    private final CastMemberRepository castMemberRepository;
    private final OttPlatformRepository ottPlatformRepository;

    @Value("${tmdb.api.key}")
    private String apiKey;

    @Value("${tmdb.api.base-url}")
    private String baseUrl;

    @Value("${tmdb.api.image-base-url}")
    private String imageBaseUrl;

    // =============================================
    // 리스트 API (DB 저장 없이 TMDB 직접 조회)
    // =============================================

    /**
     * 인기 콘텐츠 (영화+TV 혼합) - trending 사용
     */
    public Map<String, Object> getPopular(int page) {
        String url = baseUrl + "/trending/all/week?api_key=" + apiKey
                + "&language=ko-KR&page=" + (page + 1);
        return fetchMultiList(url, page);
    }

    /**
     * 평점순 (영화/TV 번갈아)
     */
    public Map<String, Object> getTopRated(int page) {
        int actualPage = page / 2;
        if (page % 2 == 0) {
            String url = baseUrl + "/movie/top_rated?api_key=" + apiKey
                    + "&language=ko-KR&region=KR&page=" + (actualPage + 1);
            return fetchMovieList(url, page, true);
        } else {
            String url = baseUrl + "/tv/top_rated?api_key=" + apiKey
                    + "&language=ko-KR&page=" + (actualPage + 1);
            return fetchTvList(url, page, true);
        }
    }

    /**
     * 현재 상영중/방영중 (영화/TV 번갈아)
     */
    public Map<String, Object> getNowPlaying(int page) {
        int actualPage = page / 2;
        if (page % 2 == 0) {
            String url = baseUrl + "/movie/now_playing?api_key=" + apiKey
                    + "&language=ko-KR&region=KR&page=" + (actualPage + 1);
            return fetchMovieList(url, page, true);
        } else {
            String url = baseUrl + "/tv/on_the_air?api_key=" + apiKey
                    + "&language=ko-KR&page=" + (actualPage + 1);
            return fetchTvList(url, page, true);
        }
    }

    /**
     * 통합 Discover (카테고리별 mediaType 지원)
     * @param genre     TMDB 장르 ID (콤마=AND, 파이프=OR), null이면 장르 필터 없음
     * @param mediaType "all"(번갈아), "movie", "tv"
     * @param sortBy    정렬 기준
     * @param page      0-based 페이지
     */
    public Map<String, Object> discover(String genre, String mediaType, String sortBy,
                                        String provider, int page) {
        if ("all".equals(mediaType)) {
            int actualPage = page / 2;
            if (page % 2 == 0) {
                return fetchMovieList(buildDiscoverUrl("movie", genre, sortBy, provider, actualPage), page, true);
            } else {
                String tvSort = sortBy.replace("primary_release_date", "first_air_date");
                return fetchTvList(buildDiscoverUrl("tv", genre, tvSort, provider, actualPage), page, true);
            }
        } else if ("movie".equals(mediaType)) {
            return fetchMovieList(buildDiscoverUrl("movie", genre, sortBy, provider, page), page, false);
        } else {
            String tvSort = sortBy.replace("primary_release_date", "first_air_date");
            return fetchTvList(buildDiscoverUrl("tv", genre, tvSort, provider, page), page, false);
        }
    }

    private String buildDiscoverUrl(String type, String genre, String sortBy,
                                    String provider, int zeroBasedPage) {
        StringBuilder url = new StringBuilder();
        url.append(baseUrl).append("/discover/").append(type)
           .append("?api_key=").append(apiKey)
           .append("&language=ko-KR")
           .append("&sort_by=").append(sortBy)
           .append("&page=").append(zeroBasedPage + 1);
        if ("movie".equals(type)) {
            url.append("&region=KR");
        }
        if (genre != null && !genre.isEmpty()) {
            url.append("&with_genres=").append(genre);
        }
        if (provider != null && !provider.isEmpty()) {
            url.append("&with_watch_providers=").append(provider)
               .append("&watch_region=KR");
        }
        return url.toString();
    }

    /**
     * 통합 검색 (영화+TV) - search/multi 사용
     */
    public Map<String, Object> searchMulti(String query, int page) {
        String url = baseUrl + "/search/multi?api_key=" + apiKey
                + "&language=ko-KR&query=" + query + "&page=" + (page + 1) + "&region=KR";
        return fetchMultiList(url, page);
    }

    /**
     * TMDB 워치 프로바이더 로고 맵 (providerId -> 로고URL)
     */
    @SuppressWarnings("unchecked")
    public Map<Integer, String> getWatchProviderLogos() {
        String url = baseUrl + "/watch/providers/movie?api_key=" + apiKey
                + "&language=ko-KR&watch_region=KR";
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        Map<Integer, String> logoMap = new HashMap<>();
        if (results != null) {
            for (Map<String, Object> provider : results) {
                Integer providerId = ((Number) provider.get("provider_id")).intValue();
                String logoPath = (String) provider.get("logo_path");
                if (logoPath != null) {
                    logoMap.put(providerId, imageBaseUrl + "/w92" + logoPath);
                }
            }
        }
        return logoMap;
    }

    /**
     * TMDB 장르 목록 (한글)
     */
    public List<Map<String, Object>> getTmdbGenres() {
        List<Map<String, Object>> genres = new ArrayList<>();
        String[] genreNames = {
            "액션", "애니메이션", "코미디", "범죄", "다큐멘터리", "드라마",
            "판타지", "역사", "공포", "음악", "미스터리",
            "로맨스", "SF", "스릴러", "전쟁"
        };
        int[] genreIds = {28, 16, 35, 80, 99, 18, 14, 36, 27, 10402, 9648, 10749, 878, 53, 10752};

        for (int i = 0; i < genreIds.length; i++) {
            Map<String, Object> g = new LinkedHashMap<>();
            g.put("id", genreIds[i]);
            g.put("name", genreNames[i]);
            genres.add(g);
        }
        return genres;
    }

    // =============================================
    // 공통 파싱 메서드
    // =============================================

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchMovieList(String url, int page, boolean alternating) {
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> movies = (List<Map<String, Object>>) response.get("results");

        List<Map<String, Object>> items = new ArrayList<>();
        if (movies != null) {
            for (Map<String, Object> movie : movies) {
                items.add(parseMovieItem(movie));
            }
        }

        int tmdbTotalPages = ((Number) response.get("total_pages")).intValue();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", items);
        result.put("number", page);
        result.put("totalPages", alternating ? Math.min(tmdbTotalPages * 2, 500) : Math.min(tmdbTotalPages, 500));
        result.put("totalElements", ((Number) response.get("total_results")).longValue());
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchTvList(String url, int page, boolean alternating) {
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> shows = (List<Map<String, Object>>) response.get("results");

        List<Map<String, Object>> items = new ArrayList<>();
        if (shows != null) {
            for (Map<String, Object> show : shows) {
                items.add(parseTvItem(show));
            }
        }

        int tmdbTotalPages = ((Number) response.get("total_pages")).intValue();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", items);
        result.put("number", page);
        result.put("totalPages", alternating ? Math.min(tmdbTotalPages * 2, 500) : Math.min(tmdbTotalPages, 500));
        result.put("totalElements", ((Number) response.get("total_results")).longValue());
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> fetchMultiList(String url, int page) {
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> results = (List<Map<String, Object>>) response.get("results");

        List<Map<String, Object>> items = new ArrayList<>();
        if (results != null) {
            for (Map<String, Object> item : results) {
                String mediaType = (String) item.get("media_type");
                if ("movie".equals(mediaType)) {
                    items.add(parseMovieItem(item));
                } else if ("tv".equals(mediaType)) {
                    items.add(parseTvItem(item));
                }
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("content", items);
        result.put("number", page);
        result.put("totalPages", Math.min(((Number) response.get("total_pages")).intValue(), 500));
        result.put("totalElements", ((Number) response.get("total_results")).longValue());
        return result;
    }

    private Map<String, Object> parseMovieItem(Map<String, Object> movie) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("tmdbId", ((Number) movie.get("id")).longValue());
        item.put("mediaType", "movie");

        String title = (String) movie.get("title");
        String originalTitle = (String) movie.get("original_title");
        item.put("title", title);
        item.put("titleEng", (title != null && title.equals(originalTitle)) ? null : originalTitle);

        String releaseDate = (String) movie.get("release_date");
        if (releaseDate != null && releaseDate.length() >= 4) {
            item.put("releaseYear", releaseDate.substring(0, 4));
        }

        String posterPath = (String) movie.get("poster_path");
        item.put("posterUrl", posterPath != null ? imageBaseUrl + "/w500" + posterPath : null);
        item.put("synopsis", movie.get("overview"));
        item.put("contentType", "영화");

        Number voteAvg = (Number) movie.get("vote_average");
        if (voteAvg != null) {
            item.put("voteAverage", Math.round(voteAvg.doubleValue() * 10.0) / 10.0);
        }

        return item;
    }

    private Map<String, Object> parseTvItem(Map<String, Object> tv) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("tmdbId", ((Number) tv.get("id")).longValue());
        item.put("mediaType", "tv");

        String name = (String) tv.get("name");
        String originalName = (String) tv.get("original_name");
        item.put("title", name);
        item.put("titleEng", (name != null && name.equals(originalName)) ? null : originalName);

        String firstAirDate = (String) tv.get("first_air_date");
        if (firstAirDate != null && firstAirDate.length() >= 4) {
            item.put("releaseYear", firstAirDate.substring(0, 4));
        }

        String posterPath = (String) tv.get("poster_path");
        item.put("posterUrl", posterPath != null ? imageBaseUrl + "/w500" + posterPath : null);
        item.put("synopsis", tv.get("overview"));
        item.put("contentType", "TV 시리즈");

        Number voteAvg = (Number) tv.get("vote_average");
        if (voteAvg != null) {
            item.put("voteAverage", Math.round(voteAvg.doubleValue() * 10.0) / 10.0);
        }

        return item;
    }

    // =============================================
    // DB 동기화 메서드
    // =============================================

    /**
     * 단일 영화 상세 정보 가져와서 저장
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public Content syncSingleMovie(Long tmdbId) {
        Optional<Content> existing = contentRepository.findByTmdbIdAndContentType(tmdbId, ContentType.MOVIE);
        if (existing.isPresent()) {
            return existing.get();
        }

        String url = baseUrl + "/movie/" + tmdbId + "?api_key=" + apiKey
                + "&language=ko-KR&append_to_response=credits,watch/providers,release_dates";

        Map<String, Object> data = restTemplate.getForObject(url, Map.class);

        String title = (String) data.get("title");
        String originalTitle = (String) data.get("original_title");
        String titleEng = (title != null && title.equals(originalTitle)) ? null : originalTitle;

        String releaseDate = (String) data.get("release_date");
        Integer releaseYear = null;
        if (releaseDate != null && releaseDate.length() >= 4) {
            releaseYear = Integer.parseInt(releaseDate.substring(0, 4));
        }

        Integer runtime = data.get("runtime") != null
                ? ((Number) data.get("runtime")).intValue() : null;

        List<Map<String, Object>> countries =
                (List<Map<String, Object>>) data.get("production_countries");
        String country = null;
        if (countries != null && !countries.isEmpty()) {
            country = (String) countries.get(0).get("name");
        }

        String ageRating = extractKoreanCertification(data);
        String synopsis = (String) data.get("overview");

        String posterPath = (String) data.get("poster_path");
        String backdropPath = (String) data.get("backdrop_path");
        String posterUrl = posterPath != null ? imageBaseUrl + "/w500" + posterPath : null;
        String backdropUrl = backdropPath != null ? imageBaseUrl + "/w780" + backdropPath : null;

        Double voteAverage = data.get("vote_average") != null
                ? ((Number) data.get("vote_average")).doubleValue() : null;

        Content content = Content.builder()
                .tmdbId(tmdbId)
                .title(title)
                .titleEng(titleEng)
                .contentType(ContentType.MOVIE)
                .releaseYear(releaseYear)
                .runtime(runtime)
                .country(country)
                .ageRating(ageRating)
                .synopsis(synopsis)
                .posterUrl(posterUrl)
                .backdropUrl(backdropUrl)
                .voteAverage(voteAverage)
                .build();

        content = contentRepository.save(content);
        mapGenres(content, data);
        mapCredits(content, data);
        mapWatchProviders(content, data);
        mapTheaterRelease(content, data);

        return content;
    }

    /**
     * 한국 극장 상영 여부 확인 (release_dates type 2,3 = Theatrical)
     */
    @SuppressWarnings("unchecked")
    private void mapTheaterRelease(Content content, Map<String, Object> data) {
        Map<String, Object> releaseDates = (Map<String, Object>) data.get("release_dates");
        if (releaseDates == null) return;

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) releaseDates.get("results");
        if (results == null) return;

        for (Map<String, Object> entry : results) {
            if ("KR".equals(entry.get("iso_3166_1"))) {
                List<Map<String, Object>> dates =
                        (List<Map<String, Object>>) entry.get("release_dates");
                if (dates == null) continue;

                for (Map<String, Object> rd : dates) {
                    Number typeNum = (Number) rd.get("type");
                    if (typeNum == null) continue;
                    int type = typeNum.intValue();
                    // type 2 = Theatrical (limited), 3 = Theatrical
                    if (type == 2 || type == 3) {
                        String dateStr = (String) rd.get("release_date");
                        if (dateStr != null && dateStr.length() >= 10) {
                            LocalDate releaseDate = LocalDate.parse(dateStr.substring(0, 10));
                            LocalDate now = LocalDate.now();
                            // 개봉일이 과거 60일 이내이거나 미래(개봉 예정)이면 극장 상영중
                            if (releaseDate.isAfter(now.minusDays(60)) && releaseDate.isBefore(now.plusDays(30))) {
                                ottPlatformRepository.findByName("극장").ifPresent(ott -> {
                                    ContentOtt co = ContentOtt.builder()
                                            .content(content)
                                            .ottPlatform(ott)
                                            .isStreaming(false)
                                            .build();
                                    content.getContentOtts().add(co);
                                });
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 단일 TV 시리즈 상세 정보 가져와서 저장
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public Content syncSingleTv(Long tmdbId) {
        Optional<Content> existing = contentRepository.findByTmdbIdAndContentType(tmdbId, ContentType.DRAMA);
        if (existing.isPresent()) {
            return existing.get();
        }

        String url = baseUrl + "/tv/" + tmdbId + "?api_key=" + apiKey
                + "&language=ko-KR&append_to_response=credits,watch/providers,content_ratings";

        Map<String, Object> data = restTemplate.getForObject(url, Map.class);

        String name = (String) data.get("name");
        String originalName = (String) data.get("original_name");
        String titleEng = (name != null && name.equals(originalName)) ? null : originalName;

        String firstAirDate = (String) data.get("first_air_date");
        Integer releaseYear = null;
        if (firstAirDate != null && firstAirDate.length() >= 4) {
            releaseYear = Integer.parseInt(firstAirDate.substring(0, 4));
        }

        // 에피소드 런타임
        List<Number> runtimes = (List<Number>) data.get("episode_run_time");
        Integer runtime = (runtimes != null && !runtimes.isEmpty())
                ? runtimes.get(0).intValue() : null;

        // 제작 국가
        List<Map<String, Object>> countries =
                (List<Map<String, Object>>) data.get("production_countries");
        String country = null;
        if (countries != null && !countries.isEmpty()) {
            country = (String) countries.get(0).get("name");
        }

        // 연령등급
        String ageRating = extractTvKoreanRating(data);

        String synopsis = (String) data.get("overview");

        String posterPath = (String) data.get("poster_path");
        String backdropPath = (String) data.get("backdrop_path");
        String posterUrl = posterPath != null ? imageBaseUrl + "/w500" + posterPath : null;
        String backdropUrl = backdropPath != null ? imageBaseUrl + "/w780" + backdropPath : null;

        Double voteAverage = data.get("vote_average") != null
                ? ((Number) data.get("vote_average")).doubleValue() : null;

        Content content = Content.builder()
                .tmdbId(tmdbId)
                .title(name)
                .titleEng(titleEng)
                .contentType(ContentType.DRAMA)
                .releaseYear(releaseYear)
                .runtime(runtime)
                .country(country)
                .ageRating(ageRating)
                .synopsis(synopsis)
                .posterUrl(posterUrl)
                .backdropUrl(backdropUrl)
                .voteAverage(voteAverage)
                .build();

        content = contentRepository.save(content);
        mapGenres(content, data);
        mapCredits(content, data);
        mapWatchProviders(content, data);

        return content;
    }

    /**
     * TMDB 인기 영화 동기화 (관리자용)
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> syncPopularMovies(int page) {
        String url = baseUrl + "/movie/popular?api_key=" + apiKey
                + "&language=ko-KR&region=KR&page=" + page;

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> movies = (List<Map<String, Object>>) response.get("results");

        int saved = 0, skipped = 0;
        List<String> savedTitles = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (Map<String, Object> movie : movies) {
            Long tmdbId = ((Number) movie.get("id")).longValue();
            if (contentRepository.existsByTmdbIdAndContentType(tmdbId, ContentType.MOVIE)) {
                skipped++;
                continue;
            }
            try {
                Content content = syncSingleMovie(tmdbId);
                saved++;
                savedTitles.add(content.getTitle());
            } catch (Exception e) {
                String title = (String) movie.get("title");
                log.error("TMDB 영화 동기화 실패 [{}]: {}", title, e.getMessage());
                errors.add(title + " - " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalFetched", movies.size());
        result.put("saved", saved);
        result.put("skipped", skipped);
        result.put("savedTitles", savedTitles);
        result.put("errors", errors);
        return result;
    }

    /**
     * TMDB 영화 검색 후 동기화 (관리자용)
     */
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> searchAndSync(String keyword) {
        String url = baseUrl + "/search/movie?api_key=" + apiKey
                + "&language=ko-KR&query=" + keyword + "&region=KR";

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        List<Map<String, Object>> movies = (List<Map<String, Object>>) response.get("results");

        if (movies == null || movies.isEmpty()) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("totalFetched", 0);
            result.put("saved", 0);
            result.put("message", "검색 결과가 없습니다.");
            return result;
        }

        int limit = Math.min(5, movies.size());
        int saved = 0, skipped = 0;
        List<String> savedTitles = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < limit; i++) {
            Map<String, Object> movie = movies.get(i);
            Long tmdbId = ((Number) movie.get("id")).longValue();
            if (contentRepository.existsByTmdbIdAndContentType(tmdbId, ContentType.MOVIE)) {
                skipped++;
                continue;
            }
            try {
                Content content = syncSingleMovie(tmdbId);
                saved++;
                savedTitles.add(content.getTitle());
            } catch (Exception e) {
                String title = (String) movie.get("title");
                log.error("TMDB 영화 동기화 실패 [{}]: {}", title, e.getMessage());
                errors.add(title + " - " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalFetched", limit);
        result.put("saved", saved);
        result.put("skipped", skipped);
        result.put("savedTitles", savedTitles);
        result.put("errors", errors);
        return result;
    }

    // =============================================
    // 내부 헬퍼 메서드
    // =============================================

    @SuppressWarnings("unchecked")
    private void mapGenres(Content content, Map<String, Object> data) {
        List<Map<String, Object>> genres = (List<Map<String, Object>>) data.get("genres");
        if (genres == null) return;

        Set<Long> addedGenreIds = new HashSet<>();

        for (Map<String, Object> genreData : genres) {
            Integer tmdbGenreId = ((Number) genreData.get("id")).intValue();

            genreTmdbMappingRepository.findByTmdbGenreId(tmdbGenreId).ifPresent(mapping -> {
                Genre genre = mapping.getGenre();
                if (addedGenreIds.add(genre.getId())) {
                    ContentGenre cg = new ContentGenre(content, genre);
                    content.getContentGenres().add(cg);
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void mapCredits(Content content, Map<String, Object> data) {
        Map<String, Object> credits = (Map<String, Object>) data.get("credits");
        if (credits == null) return;

        List<Map<String, Object>> crew = (List<Map<String, Object>>) credits.get("crew");
        if (crew != null) {
            for (Map<String, Object> person : crew) {
                if ("Director".equals(person.get("job"))) {
                    CastMember director = findOrCreateCastMember(person);
                    ContentCast cc = new ContentCast(content, director,
                            CastRoleType.DIRECTOR, null, 0);
                    content.getContentCasts().add(cc);
                }
            }
        }

        List<Map<String, Object>> cast = (List<Map<String, Object>>) credits.get("cast");
        if (cast != null) {
            int limit = Math.min(10, cast.size());
            for (int i = 0; i < limit; i++) {
                Map<String, Object> person = cast.get(i);
                CastMember actor = findOrCreateCastMember(person);
                String character = (String) person.get("character");
                ContentCast cc = new ContentCast(content, actor,
                        CastRoleType.ACTOR, character, i + 1);
                content.getContentCasts().add(cc);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void mapWatchProviders(Content content, Map<String, Object> data) {
        Map<String, Object> watchProviders = (Map<String, Object>) data.get("watch/providers");
        if (watchProviders == null) return;

        Map<String, Object> results = (Map<String, Object>) watchProviders.get("results");
        if (results == null) return;

        Map<String, Object> krData = (Map<String, Object>) results.get("KR");
        if (krData == null) return;

        Set<Long> addedOttIds = new HashSet<>();

        List<Map<String, Object>> flatrate = (List<Map<String, Object>>) krData.get("flatrate");
        if (flatrate != null) {
            for (Map<String, Object> provider : flatrate) {
                Integer providerId = ((Number) provider.get("provider_id")).intValue();

                ottPlatformRepository.findByTmdbProviderId(providerId).ifPresent(ott -> {
                    if (addedOttIds.add(ott.getId())) {
                        ContentOtt co = ContentOtt.builder()
                                .content(content)
                                .ottPlatform(ott)
                                .isStreaming(true)
                                .build();
                        content.getContentOtts().add(co);
                    }
                });
            }
        }

        List<Map<String, Object>> rent = (List<Map<String, Object>>) krData.get("rent");
        if (rent != null) {
            for (Map<String, Object> provider : rent) {
                Integer providerId = ((Number) provider.get("provider_id")).intValue();

                ottPlatformRepository.findByTmdbProviderId(providerId).ifPresent(ott -> {
                    if (addedOttIds.add(ott.getId())) {
                        ContentOtt co = ContentOtt.builder()
                                .content(content)
                                .ottPlatform(ott)
                                .isStreaming(false)
                                .build();
                        content.getContentOtts().add(co);
                    }
                });
            }
        }
    }

    private CastMember findOrCreateCastMember(Map<String, Object> person) {
        Long tmdbPersonId = ((Number) person.get("id")).longValue();

        return castMemberRepository.findByTmdbId(tmdbPersonId)
                .orElseGet(() -> {
                    String name = (String) person.get("name");
                    String originalName = (String) person.get("original_name");
                    String nameEng = (name != null && name.equals(originalName)) ? null : originalName;

                    String profilePath = (String) person.get("profile_path");
                    String profileImage = profilePath != null
                            ? imageBaseUrl + "/w185" + profilePath : null;

                    CastMember cm = CastMember.builder()
                            .name(name)
                            .nameEng(nameEng)
                            .profileImage(profileImage)
                            .tmdbId(tmdbPersonId)
                            .build();

                    return castMemberRepository.save(cm);
                });
    }

    @SuppressWarnings("unchecked")
    private String extractKoreanCertification(Map<String, Object> data) {
        Map<String, Object> releaseDates = (Map<String, Object>) data.get("release_dates");
        if (releaseDates == null) return null;

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) releaseDates.get("results");
        if (results == null) return null;

        for (Map<String, Object> entry : results) {
            if ("KR".equals(entry.get("iso_3166_1"))) {
                List<Map<String, Object>> dates =
                        (List<Map<String, Object>>) entry.get("release_dates");
                if (dates != null && !dates.isEmpty()) {
                    String cert = (String) dates.get(0).get("certification");
                    if (cert != null && !cert.isEmpty()) {
                        return mapCertification(cert);
                    }
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private String extractTvKoreanRating(Map<String, Object> data) {
        Map<String, Object> contentRatings = (Map<String, Object>) data.get("content_ratings");
        if (contentRatings == null) return null;

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) contentRatings.get("results");
        if (results == null) return null;

        for (Map<String, Object> entry : results) {
            if ("KR".equals(entry.get("iso_3166_1"))) {
                String rating = (String) entry.get("rating");
                if (rating != null && !rating.isEmpty()) {
                    return mapCertification(rating);
                }
            }
        }
        return null;
    }

    private String mapCertification(String cert) {
        switch (cert) {
            case "All": case "전체": return "전체";
            case "12": return "12세";
            case "15": return "15세";
            case "18": case "R": case "청불": return "청소년 관람불가";
            default: return cert;
        }
    }
}
