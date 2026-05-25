package com.gaia.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    private final String secret;
    private final Duration expiration;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs
    ) {
        this.secret = secret;
        this.expiration = Duration.ofMillis(expirationMs);
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date expiresAt = new Date(now.getTime() + expiration.toMillis());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("authorities", userDetails.getAuthorities().stream()
                        .map(Object::toString)
                        .toList())
                .issuedAt(now)
                .expiration(expiresAt)
                .signWith(signingKey())
                .compact();
    }

    public String extractUsername(String token) {
        return claims(token).getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && claims(token).getExpiration().after(new Date());
    }

    private Claims claims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey signingKey() {
        try {
            byte[] key = MessageDigest.getInstance("SHA-256")
                    .digest(secret.getBytes(StandardCharsets.UTF_8));
            return Keys.hmacShaKeyFor(key);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("No se pudo crear la llave JWT", exception);
        }
    }
}
