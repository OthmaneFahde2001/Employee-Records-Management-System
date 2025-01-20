package com.Fahde.auth.Repository;

import com.Fahde.auth.Entity.Token;
import com.Fahde.auth.Entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

class TokenRepositoryTest {

    @Mock
    private TokenRepository tokenRepository;

    private User user;
    private Token validToken1;
    private Token validToken2;
    private Token expiredToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = User.builder()
                .id(1)
                .username("testuser")
                .build();

        validToken1 = Token.builder()
                .id(1)
                .token("valid_token_1")
                .expired(false)
                .revoked(false)
                .user(user)
                .build();

        validToken2 = Token.builder()
                .id(2)
                .token("valid_token_2")
                .expired(false)
                .revoked(false)
                .user(user)
                .build();

        expiredToken = Token.builder()
                .id(3)
                .token("expired_token")
                .expired(true)
                .revoked(false)
                .user(user)
                .build();
    }

    @Test
    void testFindAllValidTokenByUser() {
        when(tokenRepository.findAllValidTokenByUser(1))
                .thenReturn(Arrays.asList(validToken1, validToken2));

        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(1);

        assertEquals(2, validTokens.size());
        assertEquals("valid_token_1", validTokens.get(0).getToken());
        assertEquals("valid_token_2", validTokens.get(1).getToken());
        assertTrue(validTokens.stream().allMatch(token -> !token.isExpired() && !token.isRevoked()));
    }

    @Test
    void testFindAllValidTokenByUser_NoValidTokens() {
        when(tokenRepository.findAllValidTokenByUser(1)).thenReturn(Arrays.asList());
        List<Token> validTokens = tokenRepository.findAllValidTokenByUser(1);
        assertTrue(validTokens.isEmpty());
    }

    @Test
    void testFindByToken_TokenExists() {
        when(tokenRepository.findByToken("valid_token_1")).thenReturn(Optional.of(validToken1));
        Optional<Token> foundToken = tokenRepository.findByToken("valid_token_1");

        assertTrue(foundToken.isPresent());
        assertEquals("valid_token_1", foundToken.get().getToken());
        assertEquals(user, foundToken.get().getUser());
    }

    @Test
    void testFindByToken_TokenDoesNotExist() {
        when(tokenRepository.findByToken("nonexistent_token")).thenReturn(Optional.empty());
        Optional<Token> foundToken = tokenRepository.findByToken("nonexistent_token");
        assertTrue(foundToken.isEmpty());
    }
}
