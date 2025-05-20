package org.chefcrew.external.gemini.exception;

import lombok.Getter;

@Getter
public class GeminiCustomException extends RuntimeException {
    private final GeminiException errorException;

    public GeminiCustomException(final GeminiException errorException) {
        super(String.valueOf(errorException.errorMessage));
        this.errorException = errorException;
    }

    public GeminiException getErrorException() {
        return errorException;
    }
}
