package com.example.textilemarketplacebackend.auth.services;

import com.example.textilemarketplacebackend.auth.models.TokenType;
import com.example.textilemarketplacebackend.global.services.EnvService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final EnvService envService;
    private String SECRET_KEY;

    @PostConstruct
    private void initializeSecretKey() {
        SECRET_KEY = envService.getJWT_SECRET();
    }

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

    public String generateAccountActivationToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("token_type", TokenType.ACCOUNT_ACTIVATION);

        return generateToken(extraClaims, userDetails, new Date(System.currentTimeMillis() + 60000*60*48));
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

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    public boolean isTokenValidForType(String token, UserDetails userDetails, TokenType expectedType) {
        try {
            final TokenType tokenType = TokenType.valueOf(extractTokenType(token));
            return isTokenValid(token, userDetails) && tokenType.equals(expectedType);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean isUserTokenValid(String token, UserDetails userDetails) {
        return isTokenValidForType(token, userDetails, TokenType.AUTH);
    }

    public boolean isPasswordTokenValid(String token, UserDetails userDetails) {
        return isTokenValidForType(token, userDetails, TokenType.PASSWORD_RESET);
    }

    public boolean isAccountActivationTokenValid(String token, UserDetails userDetails) {
        return isTokenValidForType(token, userDetails, TokenType.ACCOUNT_ACTIVATION);
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

