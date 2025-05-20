package org.chefcrew.common.exception;

import org.chefcrew.common.response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(final CustomException e) {
        ErrorResponse errorResponse = new ErrorResponse(
                e.getErrorException().status,
                e.getErrorException().errorMessage
        );

        return ResponseEntity
                .status(HttpStatus.valueOf(e.getErrorException().status))
                .contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .body(errorResponse);

    }
}
