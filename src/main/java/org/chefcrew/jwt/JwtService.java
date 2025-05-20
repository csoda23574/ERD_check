package org.chefcrew.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.chefcrew.jwt.constants.JWTConstants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import static io.jsonwebtoken.Jwts.*;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    protected void init() {
        jwtSecret = Base64.getEncoder()
                .encodeToString(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String issuedToken(String userId, Long tokenExpirationTime, String tokenType) {
        final Date now = new Date();
        final Claims claims = claims()
                .setSubject("token")
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenExpirationTime));
        claims.put(JWTConstants.USER_ID, userId);
        claims.put(JWTConstants.TOKEN_TYPE, tokenType);
        return builder()
                .setHeaderParam(Header.TYPE, Header.JWT_TYPE)
                .setClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public JwtValidationType verifyToken(String token) {
        try {
            Claims claims = parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
            String type = (String) claims.get(JWTConstants.TOKEN_TYPE);
            if (JWTConstants.ACCESS_TOKEN.equals(type)) {
                return JwtValidationType.VALID_ACCESS;
            } else if (JWTConstants.REFRESH_TOKEN.equals(type)) {
                return JwtValidationType.VALID_REFRESH;
            }
        } catch (ExpiredJwtException e) {
            return JwtValidationType.EXPIRED;
        } catch (Exception e) {
            return JwtValidationType.INVALID;
        }
        return JwtValidationType.INVALID;
    }

    public String getUserFromJwt(String token) {
        Claims claims = parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
        return (String) claims.get(JWTConstants.USER_ID);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }
}
