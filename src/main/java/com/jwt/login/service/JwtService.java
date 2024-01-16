package com.jwt.login.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

@Component
public class JwtService {
    private static final String SECRET = "nQoiAXyIPOFfEUYMZuQJdoIXwrkbzR3OnQoiAXyIPOFfEUYMZuQJdoIXwrkb";

    public String generateToken(String userName, Date expiration){
      Map<String, Objects> claims = new HashMap<>();
      return Jwts.builder()
        .setClaims(claims)
        .setSubject(userName)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(expiration)
        .signWith(getSignKey(), SignatureAlgorithm.HS256)
        .compact();
    }

    public String generateAccessToken(String userName){
      Date accessExpiration = new Date(
        System.currentTimeMillis() + 1000 * 60 * 30
      );
      return generateToken(userName, accessExpiration);
    }
    
    public String generateRefreshToken(String userName) {
      Calendar currentDateTime = Calendar.getInstance();
      Calendar refreshExpiration = (Calendar) currentDateTime.clone();
      refreshExpiration.add(Calendar.MONTH, 3);
      Date refreshExpirationDate = refreshExpiration.getTime();
      return generateToken(userName, refreshExpirationDate);
    }

    private Key getSignKey() {
      byte[] keyBytes = Decoders.BASE64.decode(SECRET);
      return Keys.hmacShaKeyFor(keyBytes);
    }
    public String extractUserName(String token){
      return extractClaim(token,Claims::getSubject);
    }

    public Date extractExpiration(String token){
      return extractClaim(token,Claims::getExpiration);
    }
    private <T> T extractClaim(String token, Function<Claims,T> claimResolver) {
      final Claims claims = extractAllClaims(token);
      return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
      return Jwts.parserBuilder()
        .setSigningKey(getSignKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    private Boolean isTokenExpired(String token){
      return extractExpiration(token).before(new Date());
    }

    public Boolean validateToken(String token, UserDetails userDetails){
      final String userName = extractUserName(token);
      return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
