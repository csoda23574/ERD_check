package org.chefcrew.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.chefcrew.jwt.JwtAuthenticationFilter;
import org.chefcrew.jwt.JwtExceptionFilter;
import org.chefcrew.jwt.JwtService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterBeanConfig {

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtService jwtService) {
        return new JwtAuthenticationFilter(jwtService);
    }

    @Bean
    public JwtExceptionFilter jwtExceptionFilter(ObjectMapper objectMapper) {
        return new JwtExceptionFilter(objectMapper);
    }
}

