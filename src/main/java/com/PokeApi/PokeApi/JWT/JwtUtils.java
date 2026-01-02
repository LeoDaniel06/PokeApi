
package com.PokeApi.PokeApi.JWT;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JwtUtils {
 
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private long expiration;
    
    public String extractUsername(String Token){
        return extractClaim(Token, Claims::getSubject);
    }
    
    
    private <T> T extractClaim(String Token , Function<Claims, T> claimsResolver){
        final Claims claims= extractAllClaims(Token);
        return claimsResolver.apply(claims);
    }
    
    public String generatedToken(UserDetails userDetails){
        return generatedToken(new HashMap<>(), userDetails);
    }
    
        public String generatedToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return buildToken(extraClaims, userDetails, expiration);        
    }
        
        public long getExpirationTime(){
            return expiration;
    }
        
        private String buildToken(Map<String, Object>extraClaims,
        UserDetails userDetails,
        long expiration){
            return Jwts.builder()
                    .setClaims(extraClaims)
                    .setSubject(userDetails.getUsername())
                    .setId(java.util.UUID.randomUUID().toString())
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + expiration * 1000))
                    .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                    .compact();
        }
        
        public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public Date extractCreatedAt(String token) {
        return extractClaim(token, Claims::getIssuedAt);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String Token) {
        return Jwts
                .parser()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(Token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public LoginResponse generateLoginResponse(UserDetails userDetails, String  rol) {
        String token = generatedToken(userDetails);
        Date createdAt = extractCreatedAt(token);
        Date expiresAt = extractExpiration(token);
        return new LoginResponse(token, createdAt, expiresAt,rol);
    }
}
            
