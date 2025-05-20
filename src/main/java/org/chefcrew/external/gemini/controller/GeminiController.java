package org.chefcrew.external.gemini.controller;

import lombok.RequiredArgsConstructor;
import org.chefcrew.external.gemini.Service.GeminiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/external/gemini")
@RequiredArgsConstructor
public class GeminiController {

    private final GeminiService geminiService;

    @GetMapping("/alternatives")
    public Mono<ResponseEntity<List<String>>> getAlternativeFoods(
            @RequestParam String menuName,
            @RequestParam String replaceFood) {
        
        return geminiService.getAlternativeFoodAsync(menuName, replaceFood)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
