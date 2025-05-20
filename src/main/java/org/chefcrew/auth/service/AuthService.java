package org.chefcrew.auth.service;

import lombok.RequiredArgsConstructor;
import org.chefcrew.auth.dto.KakaoAuthRequest;
import org.chefcrew.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.ParameterizedTypeReference;
import java.util.HashMap;
import java.util.Map;
import org.chefcrew.user.entity.User;

@Service
@RequiredArgsConstructor
public class AuthService {
    @Value("${kakao.rest.api.key}")
    private String kakaoRestApiKey;
    @Value("${kakao.redirect.uri}")
    private String kakaoRedirectUri;

    @Autowired
    private UserRepository userRepository;

    public Map<String, Object> kakaoLogin(KakaoAuthRequest request) {
        // 1. 카카오 토큰 요청
        String tokenUrl = "https://kauth.kakao.com/oauth/token";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", kakaoRestApiKey);
        params.add("redirect_uri", request.getRedirectUri() != null ? request.getRedirectUri() : kakaoRedirectUri);
        params.add("code", request.getCode());
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);
        ResponseEntity<Map<String, Object>> tokenResponse = restTemplate.exchange(
            tokenUrl,
            HttpMethod.POST,
            entity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> tokenMap = tokenResponse.getBody();
        if (tokenMap == null || !tokenMap.containsKey("access_token")) {
            throw new RuntimeException("카카오 토큰 발급 실패");
        }
        String accessToken = (String) tokenMap.get("access_token");
        // 2. 사용자 정보 요청
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        HttpHeaders userHeaders = new HttpHeaders();
        userHeaders.setBearerAuth(accessToken);
        HttpEntity<Void> httpUserEntity = new HttpEntity<>(userHeaders); // 변수명 변경
        ResponseEntity<Map<String, Object>> userResponse = restTemplate.exchange(
            userInfoUrl,
            HttpMethod.GET,
            httpUserEntity,
            new ParameterizedTypeReference<Map<String, Object>>() {}
        );
        Map<String, Object> userInfo = userResponse.getBody();
        // 3. 회원 등록 여부 확인
        boolean isRegistered = false;
        User userEntity = null;
        Long kakaoId = null;
        if (userInfo != null && userInfo.get("id") != null) {
            try {
                kakaoId = Long.parseLong(userInfo.get("id").toString());
            } catch (NumberFormatException e) {
                kakaoId = null;
            }
            if (kakaoId != null) {
                // kakaoId로 회원 존재 여부 확인
                userEntity = userRepository.findByKakaoId(kakaoId);
                isRegistered = userEntity != null;
                if (!isRegistered) {
                    // 카카오 정보에서 이메일, 닉네임 등 추출
                    String email = null;
                    String userName = null;
                    if (userInfo.get("kakao_account") instanceof Map) {
                        Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
                        email = kakaoAccount.getOrDefault("email", null) != null ? kakaoAccount.get("email").toString() : null;
                        if (kakaoAccount.get("profile") instanceof Map) {
                            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
                            userName = profile.getOrDefault("nickname", null) != null ? profile.get("nickname").toString() : null;
                        }
                    }
                    // email이 null이면 kakaoId 기반 더미 이메일 생성
                    if (email == null) {
                        email = "kakao_" + kakaoId + "@noemail.com";
                    }
                    String nickname = userName != null ? userName : ("user" + kakaoId);
                    String socialId = "user" + kakaoId;
                    String refreshToken = tokenMap.get("refresh_token") != null ? tokenMap.get("refresh_token").toString() : null;
                    userEntity = new User(email, null, userName, kakaoId, nickname, null, refreshToken, socialId);
                    userRepository.addUser(userEntity);
                    // DB에 저장된 userId 등 정보 보장 위해 kakaoId로 다시 조회
                    userEntity = userRepository.findByKakaoId(kakaoId);
                    isRegistered = true;
                    // 신규 사용자 DB 정보 전체 출력
                    System.out.println("[KAKAO SIGNUP] 신규 사용자 등록: " + (userEntity != null ? userEntity.toString() : null));
                } else {
                    // 기존 사용자 DB 정보 전체 출력
                    System.out.println("[KAKAO LOGIN] 기존 사용자: " + (userEntity != null ? userEntity.toString() : null));
                }
                // 로그인/회원가입 로그 남기기
                System.out.println("[KAKAO LOGIN] userId=" + (userEntity != null ? userEntity.getUserId() : null)
                        + ", kakaoId=" + kakaoId
                        + ", userName=" + (userEntity != null ? userEntity.getUserName() : null)
                        + ", email=" + (userEntity != null ? userEntity.getEmail() : null)
                        + ", isRegistered=" + isRegistered);
            }
        }
        // 4. 토큰 및 사용자 정보 반환
        Map<String, Object> result = new HashMap<>();
        result.put("access_token", tokenMap.get("access_token"));
        result.put("refresh_token", tokenMap.get("refresh_token"));
        result.put("user_id", userEntity != null ? userEntity.getUserId() : null); // DB PK 반환
        result.put("kakao_id", kakaoId); // 카카오ID도 반환
        if (userEntity != null) {
            result.put("user_name", userEntity.getUserName());
            result.put("email", userEntity.getEmail());
        }
        result.put("token", tokenMap); // 기존 구조도 유지
        result.put("user", userInfo);  // 기존 구조도 유지
        result.put("isRegistered", isRegistered);
        return result;
    }
}
