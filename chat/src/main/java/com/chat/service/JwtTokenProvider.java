package com.chat.service;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtTokenProvider {

    @Value("{spring.jwt.secret}")
    private String secretKey;

    private Long tokenValudMilisecond = 1000L * 60 * 60;

    /**
     * 이름으로 Jwt Token 생성
     */
    public String generateToken(String name) {
        Date now = new Date();
        return Jwts.builder()
                .setId(name)
                .setIssuedAt(now) // 토큰 발행일자
                .setExpiration(new Date(now.getTime() + tokenValudMilisecond)) // 유효시간 설정
                .signWith(SignatureAlgorithm.HS256, secretKey) // 암호화 알고리즘 + secret값 세팅
                .compact();
    }

    /**
     * Jwt Token을 복호화하여 이름을 얻음
     */
    public String getUserNameFromJwt(String jwt) {
        return getClaims(jwt).getBody().getId();
    }

    /**
     * Jwt Token의 유효성을 체크
     */
    public boolean validateToken(String jwt) {
        return this.getClaims(jwt) != null;
    }

    private Jws<Claims> getClaims(String jwt) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwt);
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
            throw ex;
        } catch (MalformedJwtException ex) {
            log.error("Invlid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }
}
