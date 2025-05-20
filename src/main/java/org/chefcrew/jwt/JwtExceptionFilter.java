package org.chefcrew.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chefcrew.common.exception.CustomException;
import org.chefcrew.common.exception.ErrorException;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtExceptionFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        response.setCharacterEncoding("utf-8");
        try {
            filterChain.doFilter(request, response);
        } catch (CustomException e) {
            if (e.getErrorException().equals(ErrorException.UNKNOWN_TOKEN_EXCEPTION)) {
                setErrorResponse(response, ErrorException.UNKNOWN_TOKEN_EXCEPTION);
            } else if (e.getErrorException().equals(ErrorException.WRONG_TYPE_TOKEN_EXCEPTION)) {
                setErrorResponse(response, ErrorException.WRONG_TYPE_TOKEN_EXCEPTION);
            } else if (e.getErrorException().equals(ErrorException.TIME_EXPIRED_TOKEN_EXCEPTION)) {
                setErrorResponse(response, ErrorException.TIME_EXPIRED_TOKEN_EXCEPTION);
            } else if (e.getErrorException().equals(ErrorException.UNSUPPORTED_TOKEN_EXCEPTION)) {
                setErrorResponse(response, ErrorException.UNSUPPORTED_TOKEN_EXCEPTION);
            } else if (e.getErrorException().equals(ErrorException.WRONG_SIGNATURE_TOKEN_EXCEPTION)) {
                setErrorResponse(response, ErrorException.WRONG_SIGNATURE_TOKEN_EXCEPTION);
            }
        }
    }

    private void setErrorResponse(HttpServletResponse response, ErrorException errorException) {
        response.setStatus(errorException.getStatus());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponse errorResponse = new ErrorResponse(errorException.getStatus(), errorException.getErrorMessage());
        try {
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Data
    public static class ErrorResponse {
        private final Integer code;
        private final String message;
    }

}