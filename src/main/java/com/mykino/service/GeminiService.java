package com.mykino.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;

@Service
@Slf4j
public class GeminiService {

    private final RestTemplate restTemplate;
    private final TmdbService tmdbService;
    private final KobisService kobisService;
    private final ExecutorService executor = Executors.newFixedThreadPool(2);

    @Value("${gemini.api.key:}")
    private String apiKey;

    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent";

    private static final String SYSTEM_PROMPT =
            "당신은 영화와 TV 프로그램 전문가 AI 어시스턴트입니다. " +
            "사용자의 영화/드라마 관련 질문에 정확하고 구체적으로 답변해주세요. " +
            "출연진, 감독, 줄거리, 시청률, 평점, 흥행 성적, 추천 등 영화/TV와 관련된 모든 질문에 답할 수 있습니다. " +
            "사용자 메시지에 [참고 데이터]가 포함되어 있으면 반드시 해당 데이터를 우선적으로 활용하여 답변하세요. " +
            "특히 관객수, 매출액 등 수치 정보는 [참고 데이터]에 있는 KOBIS 데이터를 기준으로 답변하세요. " +
            "[참고 데이터]에 없는 수치 정보는 절대 추측하거나 지어내지 마세요. " +
            "데이터에 없는 경우 '현재 제공된 데이터에서 확인할 수 없습니다'라고 솔직하게 답변하세요. " +
            "영화/TV와 관련 없는 질문에는 정중하게 영화/TV 관련 질문을 해달라고 안내해주세요. " +
            "답변은 한국어로 해주세요. 오늘 날짜는 " + LocalDate.now() + "입니다.";

    public GeminiService(@Qualifier("geminiRestTemplate") RestTemplate restTemplate,
                         TmdbService tmdbService,
                         KobisService kobisService) {
        this.restTemplate = restTemplate;
        this.tmdbService = tmdbService;
        this.kobisService = kobisService;
    }

    /**
     * AI 채팅 (TMDB + KOBIS 데이터 병렬 조회 후 Gemini 호출)
     */
    @SuppressWarnings("unchecked")
    public String chat(String userMessage, List<Map<String, String>> history) {
        if (apiKey == null || apiKey.isEmpty()) {
            return "AI 기능이 현재 비활성화 상태입니다. 관리자에게 문의해주세요.";
        }

        try {
            // TMDB + KOBIS 병렬 조회
            String contextData = fetchContextData(userMessage);

            String url = GEMINI_URL + "?key=" + apiKey;

            List<Map<String, Object>> contents = new ArrayList<>();

            // 대화 히스토리 추가
            if (history != null) {
                for (Map<String, String> msg : history) {
                    Map<String, Object> content = new HashMap<>();
                    content.put("role", "user".equals(msg.get("role")) ? "user" : "model");
                    content.put("parts", Collections.singletonList(
                            Collections.singletonMap("text", msg.get("text"))
                    ));
                    contents.add(content);
                }
            }

            // 현재 사용자 메시지 + 컨텍스트 데이터
            String enrichedMessage = userMessage;
            if (!contextData.isEmpty()) {
                enrichedMessage = userMessage + "\n\n[참고 데이터]\n" + contextData;
            }

            Map<String, Object> userMsg = new HashMap<>();
            userMsg.put("role", "user");
            userMsg.put("parts", Collections.singletonList(
                    Collections.singletonMap("text", enrichedMessage)
            ));
            contents.add(userMsg);

            // 시스템 인스트럭션
            Map<String, Object> systemInstruction = new HashMap<>();
            systemInstruction.put("parts", Collections.singletonList(
                    Collections.singletonMap("text", SYSTEM_PROMPT)
            ));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("system_instruction", systemInstruction);
            requestBody.put("contents", contents);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

            if (response.getBody() != null) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map<String, Object> contentMap = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) contentMap.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }

            return "응답을 처리할 수 없습니다. 다시 시도해주세요.";

        } catch (Exception e) {
            log.error("Gemini API 호출 실패: {}", e.getMessage());
            return "AI 서비스에 일시적인 문제가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }

    /**
     * TMDB + KOBIS 데이터 병렬 조회
     */
    @SuppressWarnings("unchecked")
    private String fetchContextData(String userMessage) {
        try {
            // TMDB 검색과 KOBIS 박스오피스를 병렬로 조회
            Future<String> tmdbFuture = executor.submit(() -> {
                try {
                    Map<String, Object> searchResult = tmdbService.searchMulti(userMessage, 0);
                    List<Map<String, Object>> items = (List<Map<String, Object>>) searchResult.get("content");
                    if (items == null || items.isEmpty()) return "";

                    StringBuilder sb = new StringBuilder("[TMDB 검색 결과]\n");
                    int count = 0;
                    for (Map<String, Object> item : items) {
                        if (count >= 5) break;
                        sb.append("- ").append(item.get("title"));
                        if (item.get("titleEng") != null) {
                            sb.append(" (").append(item.get("titleEng")).append(")");
                        }
                        if (item.get("releaseYear") != null) {
                            sb.append(" [").append(item.get("releaseYear")).append("]");
                        }
                        sb.append(", 유형: ").append(item.get("contentType"));
                        if (item.get("voteAverage") != null) {
                            sb.append(", TMDB평점: ").append(item.get("voteAverage"));
                        }
                        if (item.get("synopsis") != null) {
                            String synopsis = String.valueOf(item.get("synopsis"));
                            if (synopsis.length() > 100) synopsis = synopsis.substring(0, 100) + "...";
                            sb.append(", 줄거리: ").append(synopsis);
                        }
                        sb.append("\n");
                        count++;
                    }
                    return sb.toString();
                } catch (Exception e) {
                    log.debug("TMDB 검색 실패: {}", e.getMessage());
                    return "";
                }
            });

            Future<String> kobisFuture = executor.submit(() -> {
                try {
                    return kobisService.getBoxOfficeSummary();
                } catch (Exception e) {
                    log.debug("KOBIS 조회 실패: {}", e.getMessage());
                    return "";
                }
            });

            String tmdbData = tmdbFuture.get(10, TimeUnit.SECONDS);
            String kobisData = kobisFuture.get(10, TimeUnit.SECONDS);

            StringBuilder context = new StringBuilder();
            if (!tmdbData.isEmpty()) context.append(tmdbData).append("\n");
            if (!kobisData.isEmpty()) context.append(kobisData);

            return context.toString().trim();

        } catch (Exception e) {
            log.debug("컨텍스트 데이터 조회 실패: {}", e.getMessage());
            return "";
        }
    }
}
