package io.github.luicit.luisprojectscore.security;

import io.github.luicit.luisprojectscore.config.CoreProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    private final CoreProperties coreProperties;

    public JwtUtil(CoreProperties coreProperties) {
        this.coreProperties = coreProperties;
    }

    public String generateToken(UserDetails userDetails) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + coreProperties.getJwt().getExpiration());

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(exp)
                .signWith(getSignKey())
                .compact();
    }

    public boolean validateToken(String token, String username) {
        try {
            final String tokenUsername = extractUsername(token);
            return tokenUsername.equals(username) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = Jwts.parser()
                .verifyWith(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = extractExpiration(token);
        return expiration.before(new Date());
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(coreProperties.getJwt().getSecret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
