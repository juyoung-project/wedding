package kr.or.wds.project.common;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import jakarta.annotation.PostConstruct;
import kr.or.wds.project.dto.response.TokenResponseDto;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.expiration.access-token}")
    private long expirationAccessToken;

    @Value("${jwt.expiration.refresh-token}")
    private long expirationRefreshToken;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    private String generateAccessToken(String username, String roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (expirationAccessToken * 1000L)))
                .signWith(key)
                .compact();
    }

    private String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + (expirationRefreshToken * 1000L)))
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // "Bearer " 접두사 있으면 잘라냄
            if (token.toLowerCase().startsWith("bearer ")) {
                token = token.substring(7).trim();
            }

            Jws<Claims> claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);

            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public Authentication getAuthentication(String accessToken) {
        var claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(accessToken)
                .getBody();

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                claims.get("roles").toString().split(",")).map(role -> (GrantedAuthority) () -> role).toList();
        UserDetails userDetails = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    public TokenResponseDto generateToken(String username, String roles) {
        TokenResponseDto tokenResponse = new TokenResponseDto();
        tokenResponse.setAccessToken(generateAccessToken(username, roles));
        tokenResponse.setRefreshToken(generateRefreshToken(username));
        return tokenResponse;
    }
}
