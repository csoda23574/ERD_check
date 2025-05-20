package org.chefcrew.common.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private final ErrorException errorException;

    public CustomException(final ErrorException errorException) {
        super(String.valueOf(errorException.errorMessage));
        this.errorException = errorException;
    }
}
