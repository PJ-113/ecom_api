package com.ecom2.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expMillis;

    public JwtService(@Value("${jwt.secret}") String base64Secret,
                      @Value("${jwt.exp.minutes:120}") long expMinutes) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        this.expMillis = expMinutes * 60_000L;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> fn) {
        return fn.apply(parseAllClaims(token));
    }

    private Claims parseAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody();
    }

    public boolean isTokenValid(String token, UserDetails user) {
        try {
            return user.getUsername().equals(extractUsername(token)) && !isExpired(token);
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private boolean isExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    public String generateToken(UserDetails user) {
        Date now = new Date();
        return Jwts.builder()
                .setClaims(Map.of("role", firstAuthority(user))) // ใส่ role ไว้ดูเล่น
                .setSubject(user.getUsername())                 // ใช้ email เป็น subject
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + expMillis))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    private String firstAuthority(UserDetails u) {
        return u.getAuthorities().stream().findFirst().map(Object::toString).orElse("USER");
    }
}
