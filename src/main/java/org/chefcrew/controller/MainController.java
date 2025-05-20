package org.chefcrew.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.Map;
import java.util.HashMap;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class MainController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final Logger logger = LoggerFactory.getLogger(MainController.class);
    
    @Value("${server.address}")
    private String serverAddress;
    
    @Value("${server.port}")
    private String serverPort;

    @GetMapping("/dbtest")
    public Map<String, Object> testDatabase() {
        Map<String, Object> response = new HashMap<>();
        try {
            String result = jdbcTemplate.queryForObject(
                "SELECT DATABASE() as db", 
                String.class
            );
            response.put("status", "success");
            response.put("database", result);
            response.put("message", "Database connection successful");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
        }
        return response;
    }

    // 헬스 체크 엔드포인트 추가
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> apiHealthCheck() {
        logger.info("API Health check endpoint called");
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "API Server is running");
        response.put("timestamp", new Date());
        return ResponseEntity.ok(response);
    }

    // 서버 설정 정보를 제공하는 엔드포인트
    @GetMapping("/api/config")
    public ResponseEntity<Map<String, Object>> getServerConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("serverAddress", serverAddress);
        config.put("serverPort", serverPort);
        config.put("apiBaseUrl", "http://" + serverAddress + ":" + serverPort);
        return ResponseEntity.ok(config);
    }

    // 회원가입 메서드 제거 - UserController에서 담당
}
