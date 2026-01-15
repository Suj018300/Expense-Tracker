package org.example.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    public static final String SECRET = "356388792F423F4428472B4B6250655368566D597133743677397A2443264625";

    private Key getSignKey() {
        byte[] byteKey = Decoders.BASE64.decode(SECRET);
        return Keys.hmacShaKeyFor(byteKey);
    }

    private Claims extractAllClaims (String token) {
        return Jwts
                .parser()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJwt(token)
                .getBody();

    }

    public <T> T extractAllClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String extractUsername (String token) {
        return extractAllClaims(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token, Claims::getExpiration);
    }

    public boolean isTokenExpired (String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken (String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private String createToken (Map<String, Object> claims, String username) {
        return Jwts
                .builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() +1000*60*1))
                .signWith(getSignKey(), SignatureAlgorithm.ES256).compact();
    }

    public String GenerateToken (String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }
}
