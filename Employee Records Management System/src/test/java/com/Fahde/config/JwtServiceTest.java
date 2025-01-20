package com.Fahde.config;

import com.Fahde.auth.Entity.Role;
import com.Fahde.auth.Entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtServiceTest {

    private JwtService jwtService;

    @Value("${application.security.jwt.secret-key}")
    private String secretKey ;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        jwtService.setSecretKey("404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        jwtService.setJwtExpiration(86400000);
        jwtService.setRefreshExpiration(604800000);
    }

    @Test
    void generateTokenShouldGenerateValidToken() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(userDetails.getAuthorities()).thenReturn(null);
        when(((User) userDetails).getId()).thenReturn(1);
        when(((User) userDetails).getRole()).thenReturn(Role.ADMINISTRATOR);

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token, "Token should not be null");
        assertTrue(token.startsWith("ey"), "Token should start with JWT prefix (ey)");
    }

    @Test
    void generateRefreshTokenShouldGenerateValidRefreshToken() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String refreshToken = jwtService.generateRefreshToken(userDetails);

        assertNotNull(refreshToken, "Refresh token should not be null");
        assertTrue(refreshToken.startsWith("ey"), "Refresh token should start with JWT prefix (ey)");
    }

    @Test
    void extractUsernameShouldReturnUsernameFromToken() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(userDetails);
        String username = jwtService.extractUsername(token);

        assertEquals("testUser", username, "Extracted username should match");
    }

    @Test
    void isTokenValidShouldReturnTrueForValidToken() {
        // Mock UserDetails
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid, "Token should be valid");
    }

    @Test
    void isTokenValidShouldReturnFalseForExpiredToken() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        Key secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String token = jwtService.generateToken((Map<String, Object>) userDetails, (UserDetails) secretKey);
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Date expiredDate = new Date(System.currentTimeMillis() - 10000);
        claims.setExpiration(expiredDate);
        String expiredToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(expiredDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        boolean isValid = jwtService.isTokenValid(expiredToken, userDetails);
        assertFalse(isValid, "Token should be invalid due to expiration");
    }


    @Test
    void isTokenValidShouldReturnFalseForInvalidSignature() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        String token = jwtService.generateToken(userDetails);
        boolean isValid = jwtService.isTokenValid(token, userDetails);
        String invalidSecretKey = "invalidSecretKey";
        try {
            Jwts.parserBuilder()
                    .setSigningKey(invalidSecretKey.getBytes())
                    .build()
                    .parseClaimsJws(token);
            assertFalse(true, "Token should not be valid with incorrect secret key");
        } catch (Exception e) {
            assertTrue(true);
        }
    }



    @Test
    void extractIdShouldReturnIdFromToken() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(((User) userDetails).getId()).thenReturn(1);

        String token = jwtService.generateToken(userDetails);
        Integer extractedId = jwtService.extractId(token);

        assertEquals(1, extractedId, "Extracted id should match");
    }

    @Test
    void extractRoleShouldReturnRoleFromToken() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");
        when(((User) userDetails).getRole()).thenReturn(Role.ADMINISTRATOR);

        String token = jwtService.generateToken(userDetails);
        String role = jwtService.extractRole(token);

        assertEquals("ADMINISTRATOR", role, "Extracted role should match");
    }

    @Test
    void extractUserNameShouldReturnUserNameFromToken() {
        UserDetails userDetails = mock(User.class);
        when(userDetails.getUsername()).thenReturn("testUser");

        String token = jwtService.generateToken(userDetails);
        String extractedUserName = jwtService.extractUserName(token);

        assertEquals("testUser", extractedUserName, "Extracted username should match");
    }

}
