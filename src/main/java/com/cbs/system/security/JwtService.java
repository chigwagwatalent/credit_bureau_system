package com.cbs.system.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private final Key key;
    private final long expirySeconds;

    public JwtService(
        @Value("${app.jwt.secret:change-this-secret-please-change}") String secret,
        @Value("${app.jwt.expiry-seconds:7200}") long expirySeconds // 2 hours
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirySeconds = expirySeconds;
    }

    public String generate(String username) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(expirySeconds)))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String validateAndGetSubject(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
