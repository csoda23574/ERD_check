package org.chefcrew.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/system") // URL 경로 변경 - "/api"에서 "/api/system"으로 변경
public class HealthCheckController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("timestamp", System.currentTimeMillis());
        
        try {
            // DB 연결 테스트
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            response.put("database", "UP");
            response.put("dbTest", result);
        } catch (Exception e) {
            response.put("database", "DOWN");
            response.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/server-config") // URL 경로 변경 - "/config"에서 "/server-config"로 변경
    public ResponseEntity<Map<String, Object>> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("serverAddress", "localhost");
        config.put("serverPort", 8081);
        config.put("apiVersion", "1.0");
        return ResponseEntity.ok(config);
    }
}
