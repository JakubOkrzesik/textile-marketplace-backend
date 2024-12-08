package com.example.textilemarketplacebackend.auth.services;

import com.example.textilemarketplacebackend.auth.models.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "34b8f23b1e65c2cffd3d50c75050a09c3434e52242837700f2fc7f2bac635e59b41b2460b19e23670448dfaf8ff0ae8b96d762db5b76d0e2c78663d0b61890b3";

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("token_type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateAuthToken(UserDetails userDetails){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("token_type", TokenType.AUTH);

        return generateToken(extraClaims, userDetails, new Date(System.currentTimeMillis() + 60000*60*24));
    }

    public String generatePasswordResetToken(UserDetails userDetails){
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("token_type", TokenType.PASSWORD_RESET);

        return generateToken(extraClaims, userDetails, new Date(System.currentTimeMillis() + 60000*15));
    }

    public String generateToken(Map<String,Object> ExtraClaims, UserDetails userDetails, Date expiration){
        return Jwts
                .builder()
                .claims()
                .add(ExtraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(expiration)
                .and()
                .signWith(getSinginKey())
                .compact();
    }

    public boolean isUserTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);
        final TokenType token_type = TokenType.valueOf(extractTokenType(token));
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token) && token_type.equals(TokenType.AUTH);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSinginKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSinginKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}

