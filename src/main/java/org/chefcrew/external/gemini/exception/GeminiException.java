package org.chefcrew.external.gemini.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GeminiException {
    /**
     * 500 INTERNAL SEVER ERROR
     */
    GEMINI_API_EXTERNAL_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "gemini ai api 사용 중 문제가 발생하였습니다.");
    int status;
    String errorMessage;
}

