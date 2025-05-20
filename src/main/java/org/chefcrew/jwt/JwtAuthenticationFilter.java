package org.chefcrew.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.chefcrew.jwt.constants.JWTConstants;
import org.chefcrew.jwt.constants.WhiteListConstants;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws IOException, ServletException {
        for (String whiteUrl : WhiteListConstants.FILTER_WHITE_LIST) {
            if (pathMatcher.match(whiteUrl, request.getRequestURI())) {
                filterChain.doFilter(request, response);
                return;
            }
        }
        final String token = getJwtFromRequest(request);

        if (token != null) {
            JwtValidationType tokenType = jwtService.verifyToken(token);
            String userId = jwtService.getUserFromJwt(token);
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userId, null, List.of()
            );
            if (tokenType == JwtValidationType.VALID_ACCESS) {
                request.setAttribute(JWTConstants.USER_ID, userId);
                request.setAttribute(JWTConstants.TOKEN_TYPE, tokenType);
            }
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring("Bearer ".length());
        }
        return null;
    }
}
