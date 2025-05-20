package org.chefcrew.user.dto.response;

public record GetUserInfoResponse(
        long userId,
        String userName,
        String email,
        String nickname,
        String profile // 프로필 이미지 URI 필드 추가
) {
}
