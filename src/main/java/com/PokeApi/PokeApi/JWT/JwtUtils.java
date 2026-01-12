package com.PokeApi.PokeApi.JWT;

import com.PokeApi.PokeApi.JPA.UsuarioDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import static io.jsonwebtoken.Jwts.claims;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import static javax.crypto.Cipher.SECRET_KEY;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtils {
    
    private static final String SECRET_KEY = "password123456789012345678901234";
    private static final long EXPIRATION_TIME = 86400000;
    private static final long VERIFICATION_EXPIRATION_TIME = 172800000;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;
    
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String generateToken(UsuarioDetails userDetails) {

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("idUsuario", userDetails.getId());
        extraClaims.put("rol", userDetails.getAuthorities()
                .iterator().next().getAuthority());

        return buildToken(extraClaims, userDetails);
    }
    
    public String generateVerificationToken(String correo, int idUsuario) {
        return Jwts.builder()
                .claim("idUsuario", idUsuario)
                .claim("correo", correo)
                .claim("type", "verification")
                .setSubject(correo)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + VERIFICATION_EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    public boolean validateVerificationToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            String tokenType = claims.get("type", String.class);
            return "verification".equals(tokenType) && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    public String extractEmail(String token) {
        return extractAllClaims(token).get("correo", String.class);
    }

    public long getExpiration() {
        return expiration;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setId(java.util.UUID.randomUUID().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public LoginResponse generateLoginResponse(UsuarioDetails userDetails) {
        String token = generateToken(userDetails);
        Date createdAt = extractIssuedAt(token);
        Date expiresAt = extractExpiration(token);
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();

        return new LoginResponse(token, createdAt, expiresAt, rol);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Long extractUserId(String token) {
        return extractAllClaims(token).get("idUsuario", Long.class);
    }

    public String extractRol(String token) {
        return extractAllClaims(token).get("rol", String.class);
    }

    public Date extractIssuedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
            
