package com.medsync.cadastroagendamento.infrastructure.config;

import com.medsync.cadastroagendamento.infrastructure.config.properties.AppProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component
public class JwtConfig {
    
    private final AppProperties appProperties;
    
    public JwtConfig(AppProperties appProperties) {
        this.appProperties = appProperties;
    }
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(appProperties.getSecurity().getJwt().getSecret().getBytes());
    }
    
    public String generateToken(UUID userId, String email, String role, List<String> permissions) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId.toString());
        claims.put("role", role);
        claims.put("permissions", permissions);
        return createToken(claims, email);
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + appProperties.getSecurity().getJwt().getExpiration()))
                .signWith(getSigningKey())
                .compact();
    }
    
    public Boolean validateToken(String token, String email) {
        final String username = getUsernameFromToken(token);
        return (username.equals(email) && !isTokenExpired(token));
    }
    
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public UUID getUserIdFromToken(String token) {
        String userIdStr = getClaimFromToken(token, claims -> claims.get("userId", String.class));
        return UUID.fromString(userIdStr);
    }
    
    public String getRoleFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("role", String.class));
    }
    
    @SuppressWarnings("unchecked")
    public List<String> getPermissionsFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("permissions", List.class));
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
    
    public Long getExpirationTime() {
        return appProperties.getSecurity().getJwt().getExpiration();
    }
}
