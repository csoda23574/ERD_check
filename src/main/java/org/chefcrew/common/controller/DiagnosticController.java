package org.chefcrew.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/diagnostic")
public class DiagnosticController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/db-info")
    public ResponseEntity<Map<String, Object>> getDatabaseInfo() {
        Map<String, Object> info = new HashMap<>();
        
        // 기본 서버 정보
        info.put("timestamp", System.currentTimeMillis());
        info.put("javaVersion", System.getProperty("java.version"));
        
        try {
            // DB 연결 정보
            info.put("dbStatus", "UP");
            
            // 현재 선택된 데이터베이스 확인
            String currentDb = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            info.put("currentDatabase", currentDb);
            
            // 테이블 정보
            List<Map<String, Object>> tables = jdbcTemplate.queryForList(
                "SHOW TABLES");
            info.put("tables", tables);
            
            // USER 테이블 특별 정보
            try {
                List<Map<String, Object>> userTableInfo = jdbcTemplate.queryForList(
                    "DESCRIBE user");
                info.put("userTableSchema", userTableInfo);
                
                int userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM user", Integer.class);
                info.put("userCount", userCount);
                
                if (userCount > 0) {
                    List<Map<String, Object>> latestUsers = jdbcTemplate.queryForList(
                        "SELECT * FROM user ORDER BY user_id DESC LIMIT 5");
                    info.put("latestUsers", latestUsers);
                }
            } catch (Exception e) {
                info.put("userTableError", e.getMessage());
            }
            
        } catch (Exception e) {
            info.put("dbStatus", "ERROR");
            info.put("error", e.getMessage());
        }
        
        return ResponseEntity.ok(info);
    }

    @GetMapping("/db-status")
    public ResponseEntity<?> checkDatabase() {
        Map<String, Object> status = new HashMap<>();
        
        try {
            // 현재 선택된 데이터베이스
            String currentDb = jdbcTemplate.queryForObject("SELECT DATABASE()", String.class);
            status.put("currentDatabase", currentDb);
            
            // 현재 DB의 테이블 목록
            List<Map<String, Object>> tables = jdbcTemplate.queryForList("SHOW TABLES");
            status.put("tables", tables);
            
            // 사용자 테이블 정보
            try {
                List<Map<String, Object>> userSchema = jdbcTemplate.queryForList("DESCRIBE user");
                status.put("userTableSchema", userSchema);
                
                int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM user", Integer.class);
                status.put("userCount", count);
                
                // 최근 추가된 사용자
                if (count > 0) {
                    List<Map<String, Object>> users = jdbcTemplate.queryForList(
                        "SELECT * FROM user ORDER BY user_id DESC LIMIT 5");
                    status.put("recentUsers", users);
                }
            } catch (Exception e) {
                status.put("userTableError", e.getMessage());
            }
            
            // recipematedb 스키마에서 명시적 테이블 확인
            try {
                int count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM recipematedb.user", Integer.class);
                status.put("recipematedbUserCount", count);
            } catch (Exception e) {
                status.put("recipematedbUserError", e.getMessage());
            }
            
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            status.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(status);
        }
    }
}
