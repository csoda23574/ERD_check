package org.chefcrew.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorException {

    REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "잘못된 요청입니다."),
    ALREADY_EXIST_EMAIL(HttpStatus.BAD_REQUEST.value(), "이미 가입된 이메일입니다"),
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "유저가 존재하지 않습니다."),
    PASSWORD_NOT_ACCORD(HttpStatus.BAD_REQUEST.value(), "비밀번호가 불일치합니다."),
    RECIPE_NOT_FOUND(HttpStatus.BAD_REQUEST.value(), "레시피를 찾을 수 없습니다."),
    OPEN_API_SERVER_ERROR(HttpStatus.SERVICE_UNAVAILABLE.value(), "공공데이터 서버가 작동하지 않습니다."),

    // JWT Token Errors
    UNKNOWN_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "알 수 없는 토큰입니다."),
    WRONG_TYPE_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "잘못된 형식의 토큰입니다."),
    TIME_EXPIRED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "만료된 토큰입니다."),
    UNSUPPORTED_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "지원하지 않는 토큰입니다."),
    WRONG_SIGNATURE_TOKEN_EXCEPTION(HttpStatus.UNAUTHORIZED.value(), "잘못된 서명의 토큰입니다."),

    // File/Image Errors
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "이미지를 찾을 수 없습니다."),
    FILE_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "잘못된 파일 요청입니다."),
    FILE_SIZE_BAD_REQUEST(HttpStatus.BAD_REQUEST.value(), "파일 크기가 너무 큽니다."),
    IMAGE_INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "이미지 처리 중 서버 오류가 발생했습니다."); // SEVER -> SERVER 오타 수정

    int status;
    String errorMessage;

}