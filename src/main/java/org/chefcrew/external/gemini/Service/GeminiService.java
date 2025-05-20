package org.chefcrew.external.gemini.Service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.chefcrew.external.gemini.dto.request.GeminiRequest;
import org.chefcrew.external.gemini.dto.response.GeminiResponse;
import org.chefcrew.external.gemini.exception.GeminiCustomException;
import org.chefcrew.external.gemini.exception.GeminiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class GeminiService {
    public static final String GEMINI_FLASH = "gemini-2.0-flash";
    public static final String DEFAULT_REQUEST = "을 대체할 수 있는 식재료를 식재료명으로만 답변해줘. 여러 식재료가 존재한다면 ','로 구분해서 제시해줘";

    private final WebClient webClient;
    private final String geminiApiKey;

    public GeminiService(@Value("${gemini.api.base-url}") String baseUrl,
                         @Value("${gemini.api.key}") String geminiApiKey) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .build();
        this.geminiApiKey = geminiApiKey;
    }

    private GeminiResponse getCompletion(GeminiRequest request) {
        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1beta/models/{model}:generateContent")
                        .queryParam("key", geminiApiKey)
                        .build(GEMINI_FLASH))
                .header("Content-Type", "application/json")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();
    }

    public Mono<List<String>> getAlternativeFoodAsync(String menuName, String replaceFood) {
        return Mono.fromCallable(() -> {
            GeminiRequest geminiRequest = getGeminiRequest(menuName, replaceFood);
            GeminiResponse response;

            try {
                response = getCompletion(geminiRequest);
            } catch (Exception e) {
                throw new GeminiCustomException(GeminiException.GEMINI_API_EXTERNAL_EXCEPTION); // 꼭 다시 던지거나, 빈 리스트 반환
            }

            List<String> result = response.getCandidates()
                    .stream()
                    .findFirst()
                    .flatMap(candidate -> candidate.getContent().getParts()
                            .stream()
                            .findFirst()
                            .map(GeminiResponse.TextPart::getText))
                    .map(text -> text.replaceAll("[\\[\\]]", ""))
                    .map(text -> text.split(","))
                    .map(array -> Arrays.stream(array)
                            .map(String::trim)
                            .collect(Collectors.toList()))
                    .orElse(Collections.emptyList());

            return result;
        }).subscribeOn(Schedulers.boundedElastic());
    }

    public GeminiRequest getGeminiRequest(String recipeName, String foodName) {
        String request = recipeName + "에서 " + foodName + DEFAULT_REQUEST;
        log.info("Gemini API 요청 프롬프트: {}", request); // 프롬프트 로깅 추가
        return new GeminiRequest(request);
    }
}
