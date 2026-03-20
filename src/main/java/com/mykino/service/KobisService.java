package com.mykino.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class KobisService {

    private final RestTemplate restTemplate;

    @Value("${kobis.api.key:}")
    private String apiKey;

    @Value("${kobis.api.base-url}")
    private String baseUrl;

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyyMMdd");

    public KobisService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 일별 박스오피스 조회 (어제 기준)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getDailyBoxOffice() {
        if (apiKey == null || apiKey.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            String targetDate = LocalDate.now().minusDays(1).format(DATE_FMT);
            String url = baseUrl + "/boxoffice/searchDailyBoxOfficeList.json"
                    + "?key=" + apiKey + "&targetDt=" + targetDate;

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return Collections.emptyList();

            Map<String, Object> result = (Map<String, Object>) response.get("boxOfficeResult");
            if (result == null) return Collections.emptyList();

            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("dailyBoxOfficeList");
            if (list == null) return Collections.emptyList();

            List<Map<String, String>> boxOffice = new ArrayList<>();
            for (Map<String, Object> item : list) {
                Map<String, String> movie = new HashMap<>();
                movie.put("rank", String.valueOf(item.get("rank")));
                movie.put("movieNm", String.valueOf(item.get("movieNm")));
                movie.put("openDt", String.valueOf(item.get("openDt")));
                movie.put("audiCnt", String.valueOf(item.get("audiCnt")));
                movie.put("audiAcc", String.valueOf(item.get("audiAcc")));
                movie.put("salesAcc", String.valueOf(item.get("salesAcc")));
                movie.put("movieCd", String.valueOf(item.get("movieCd")));
                boxOffice.add(movie);
            }
            return boxOffice;

        } catch (Exception e) {
            log.error("KOBIS 일별 박스오피스 조회 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * 영화 상세 정보 조회
     */
    @SuppressWarnings("unchecked")
    public Map<String, String> getMovieDetail(String movieCd) {
        if (apiKey == null || apiKey.isEmpty()) {
            return Collections.emptyMap();
        }

        try {
            String url = baseUrl + "/movie/searchMovieInfo.json"
                    + "?key=" + apiKey + "&movieCd=" + movieCd;

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return Collections.emptyMap();

            Map<String, Object> result = (Map<String, Object>) response.get("movieInfoResult");
            if (result == null) return Collections.emptyMap();

            Map<String, Object> info = (Map<String, Object>) result.get("movieInfo");
            if (info == null) return Collections.emptyMap();

            Map<String, String> detail = new HashMap<>();
            detail.put("movieNm", String.valueOf(info.get("movieNm")));
            detail.put("showTm", String.valueOf(info.get("showTm")));
            detail.put("openDt", String.valueOf(info.get("openDt")));
            detail.put("prdtYear", String.valueOf(info.get("prdtYear")));
            detail.put("typeNm", String.valueOf(info.get("typeNm")));

            // 장르
            List<Map<String, String>> genres = (List<Map<String, String>>) info.get("genres");
            if (genres != null && !genres.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map<String, String> g : genres) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(g.get("genreNm"));
                }
                detail.put("genres", sb.toString());
            }

            // 감독
            List<Map<String, String>> directors = (List<Map<String, String>>) info.get("directors");
            if (directors != null && !directors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (Map<String, String> d : directors) {
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(d.get("peopleNm"));
                }
                detail.put("directors", sb.toString());
            }

            // 배우 (상위 5명)
            List<Map<String, String>> actors = (List<Map<String, String>>) info.get("actors");
            if (actors != null && !actors.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                int count = 0;
                for (Map<String, String> a : actors) {
                    if (count >= 5) break;
                    if (sb.length() > 0) sb.append(", ");
                    sb.append(a.get("peopleNm"));
                    count++;
                }
                detail.put("actors", sb.toString());
            }

            // 관람등급
            List<Map<String, String>> audits = (List<Map<String, String>>) info.get("audits");
            if (audits != null && !audits.isEmpty()) {
                detail.put("watchGradeNm", audits.get(0).get("watchGradeNm"));
            }

            return detail;

        } catch (Exception e) {
            log.error("KOBIS 영화 상세 조회 실패: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 주간 박스오피스 조회 (지난주 기준)
     */
    @SuppressWarnings("unchecked")
    public List<Map<String, String>> getWeeklyBoxOffice() {
        if (apiKey == null || apiKey.isEmpty()) {
            return Collections.emptyList();
        }

        try {
            String targetDate = LocalDate.now().minusDays(7).format(DATE_FMT);
            String url = baseUrl + "/boxoffice/searchWeeklyBoxOfficeList.json"
                    + "?key=" + apiKey + "&targetDt=" + targetDate + "&weekGb=0";

            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            if (response == null) return Collections.emptyList();

            Map<String, Object> result = (Map<String, Object>) response.get("boxOfficeResult");
            if (result == null) return Collections.emptyList();

            List<Map<String, Object>> list = (List<Map<String, Object>>) result.get("weeklyBoxOfficeList");
            if (list == null) return Collections.emptyList();

            List<Map<String, String>> boxOffice = new ArrayList<>();
            for (Map<String, Object> item : list) {
                Map<String, String> movie = new HashMap<>();
                movie.put("rank", String.valueOf(item.get("rank")));
                movie.put("movieNm", String.valueOf(item.get("movieNm")));
                movie.put("openDt", String.valueOf(item.get("openDt")));
                movie.put("audiCnt", String.valueOf(item.get("audiCnt")));
                movie.put("audiAcc", String.valueOf(item.get("audiAcc")));
                movie.put("movieCd", String.valueOf(item.get("movieCd")));
                boxOffice.add(movie);
            }
            return boxOffice;

        } catch (Exception e) {
            log.error("KOBIS 주간 박스오피스 조회 실패: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * AI 프롬프트용 박스오피스 요약 텍스트 생성 (일별 + 주간)
     */
    public String getBoxOfficeSummary() {
        StringBuilder sb = new StringBuilder();

        // 일별 박스오피스
        List<Map<String, String>> daily = getDailyBoxOffice();
        if (!daily.isEmpty()) {
            sb.append("[한국 일별 박스오피스 - ").append(LocalDate.now().minusDays(1)).append(" 기준]\n");
            for (Map<String, String> movie : daily) {
                sb.append(movie.get("rank")).append("위: ")
                  .append(movie.get("movieNm"))
                  .append(" (개봉: ").append(movie.get("openDt"))
                  .append(", 일일관객: ").append(formatNumber(movie.get("audiCnt")))
                  .append("명, 누적관객: ").append(formatNumber(movie.get("audiAcc")))
                  .append("명)\n");
            }
        }

        // 주간 박스오피스 (일별에 없는 영화만 추가)
        List<Map<String, String>> weekly = getWeeklyBoxOffice();
        if (!weekly.isEmpty()) {
            Set<String> dailyMovies = new HashSet<>();
            for (Map<String, String> m : daily) {
                dailyMovies.add(m.get("movieNm"));
            }

            StringBuilder weekSb = new StringBuilder();
            for (Map<String, String> movie : weekly) {
                if (!dailyMovies.contains(movie.get("movieNm"))) {
                    weekSb.append("- ").append(movie.get("movieNm"))
                           .append(" (개봉: ").append(movie.get("openDt"))
                           .append(", 주간관객: ").append(formatNumber(movie.get("audiCnt")))
                           .append("명, 누적관객: ").append(formatNumber(movie.get("audiAcc")))
                           .append("명)\n");
                }
            }

            if (weekSb.length() > 0) {
                sb.append("\n[주간 박스오피스 추가 영화]\n").append(weekSb);
            }
        }

        return sb.toString();
    }

    private String formatNumber(String num) {
        try {
            long n = Long.parseLong(num);
            if (n >= 10000) {
                return String.format("%.1f만", n / 10000.0);
            }
            return String.format("%,d", n);
        } catch (NumberFormatException e) {
            return num;
        }
    }
}
