package org.chefcrew.jwt;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.chefcrew.jwt.constants.JWTConstants;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class UserIdResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(@NonNull MethodParameter parameter) {
        // PathVariable(Long) 방식은 더 이상 지원하지 않음
        return parameter.hasParameterAnnotation(UserId.class) && Long.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
                                  @NonNull NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object userIdObj = request.getAttribute(JWTConstants.USER_ID);
        if (userIdObj == null) {
            throw new RuntimeException("USER_ID를 가져오지 못했습니다.\n\n[해결 방법]\n- 경로에 userId를 붙이지 마세요.\n- 반드시 JWT 토큰을 Authorization 헤더로 보내세요.\n예시: Authorization: Bearer {jwt-access-token}");
        }
        try {
            return Long.parseLong(userIdObj.toString());
        } catch (NumberFormatException e) {
            throw new RuntimeException("USER_ID 파싱 실패: " + userIdObj);
        }
    }
}
