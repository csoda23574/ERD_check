package org.chefcrew.config;

import lombok.RequiredArgsConstructor;
import org.chefcrew.jwt.UserIdResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.lang.NonNull;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final UserIdResolver userIdResolver;

    @Override
    public void addCorsMappings(@NonNull CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("http://localhost:8081", "http://localhost:8080", "exp://192.168.0.*:*") // 허용할 출처
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 헤더 허용
                .allowCredentials(true) // 인증 정보 허용
                .maxAge(3600); // 1시간 동안 pre-flight 요청 캐시
    }

    @Override
    public void addArgumentResolvers(@NonNull List<org.springframework.web.method.support.HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdResolver);
    }
}