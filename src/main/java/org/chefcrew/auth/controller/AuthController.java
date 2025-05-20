package org.chefcrew.auth.controller;

import lombok.RequiredArgsConstructor;
import org.chefcrew.auth.dto.KakaoAuthRequest;
import org.chefcrew.auth.service.AuthService;
import org.chefcrew.jwt.JwtService; // JwtService 임포트
import org.chefcrew.jwt.JwtValidationType; // JwtValidationType 임포트
import org.chefcrew.jwt.constants.JWTConstants; // JWTConstants 임포트 (ACCESS_TOKEN, REFRESH_TOKEN 타입 문자열 정의 필요)
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtService jwtService; // JwtService 의존성 주입

    // 토큰 만료 시간 (밀리초 단위, 실제로는 application.properties 등에서 관리하는 것이 좋음)
    // 예시: 액세스 토큰 1시간, 리프레시 토큰 24시간
    private static final Long ACCESS_TOKEN_EXPIRATION_MS = 1L * 60 * 60 * 1000; // 1시간
    private static final Long REFRESH_TOKEN_EXPIRATION_MS = 24L * 60 * 60 * 1000; // 24시간

    @PostMapping("/auth")
    public ResponseEntity<?> kakaoAuth(@RequestBody KakaoAuthRequest request) {
        // 1. authService를 통해 카카오 로그인 처리 및 사용자 정보 가져오기
        // authService.kakaoLogin(request)는 사용자 정보를 담은 Map을 반환한다고 가정
        Map<String, Object> originalResponseFromAuthService = authService.kakaoLogin(request);

        // 2. 반환된 사용자 정보에서 사용자 ID 추출
        // 클라이언트 로그에서 "user_id: 1"을 확인했으므로, 이를 사용자의 고유 식별자로 사용
        Object userIdObj = originalResponseFromAuthService.get("user_id");
        if (userIdObj == null) {
            // 사용자 ID가 없는 경우 오류 처리 (예: 로그인 실패 또는 응답 구조 다름)
            return ResponseEntity.status(500).body(Map.of("error", "User ID not found after Kakao login processing", "details", originalResponseFromAuthService));
        }
        String userId = userIdObj.toString();

        // 3. JwtService를 사용하여 서버 자체의 액세스 토큰 및 리프레시 토큰 발급
        String newAccessToken = jwtService.issuedToken(userId, ACCESS_TOKEN_EXPIRATION_MS, JWTConstants.ACCESS_TOKEN);
        String newRefreshToken = jwtService.issuedToken(userId, REFRESH_TOKEN_EXPIRATION_MS, JWTConstants.REFRESH_TOKEN);

        // 4. 클라이언트에 전달할 새로운 응답 Map 구성
        // 기존 authService 응답 내용을 유지하면서, access_token과 refresh_token을 새로 발급한 JWT로 교체
        Map<String, Object> responseToClient = new HashMap<>(originalResponseFromAuthService);
        responseToClient.put("access_token", newAccessToken); // 자체 발급한 액세스 토큰
        responseToClient.put("refresh_token", newRefreshToken); // 자체 발급한 리프레시 토큰
        
        // Kakao에서 받은 원래 토큰 정보(originalResponseFromAuthService 내의 "token" 객체 또는 최상위 access_token, refresh_token)는
        // 클라이언트 혼동을 피하기 위해 제거하거나, 클라이언트가 사용하지 않도록 명확히 해야 합니다.
        // 현재는 최상위 access_token, refresh_token을 덮어쓰므로, 클라이언트가 이를 사용하게 됩니다.

        return ResponseEntity.ok(responseToClient);
    }

    @PostMapping("/auth/token/health")
    public ResponseEntity<?> checkTokenHealth(@RequestHeader(value = "token", required = false) String refreshTokenFromHeader) {
        if (refreshTokenFromHeader == null || refreshTokenFromHeader.isEmpty()) {
            return ResponseEntity.status(401).body(Map.of("status", 401, "message", "Refresh token is missing"));
        }

        // JwtService를 사용하여 전달받은 리프레시 토큰 검증
        JwtValidationType validationType = jwtService.verifyToken(refreshTokenFromHeader);

        if (validationType == JwtValidationType.VALID_REFRESH) {
            // 리프레시 토큰이 유효한 경우, 사용자 ID를 추출하여 새 액세스 토큰 발급
            String userId = jwtService.getUserFromJwt(refreshTokenFromHeader);
            String newAccessToken = jwtService.issuedToken(userId, ACCESS_TOKEN_EXPIRATION_MS, JWTConstants.ACCESS_TOKEN);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("status", 200);
            responseBody.put("message", "Refresh token is valid and new access token issued");
            responseBody.put("access_token", newAccessToken); // 새로 발급한 액세스 토큰
            return ResponseEntity.ok(responseBody);

        } else if (validationType == JwtValidationType.EXPIRED) {
            return ResponseEntity.status(401).body(Map.of("status", 401, "message", "Refresh token is expired"));
        } else { // INVALID 또는 기타 상태
            return ResponseEntity.status(401).body(Map.of("status", 401, "message", "Refresh token is invalid"));
        }
    }
}