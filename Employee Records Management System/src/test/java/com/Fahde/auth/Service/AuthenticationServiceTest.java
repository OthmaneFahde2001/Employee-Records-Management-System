package com.Fahde.auth.Service;

import com.Fahde.auth.DTO.AuthenticationRequest;
import com.Fahde.auth.DTO.AuthenticationResponse;
import com.Fahde.auth.DTO.RegisterRequest;
import com.Fahde.auth.Entity.Role;
import com.Fahde.auth.Entity.Token;
import com.Fahde.auth.Entity.TokenType;
import com.Fahde.auth.Entity.User;
import com.Fahde.auth.Repository.TokenRepository;
import com.Fahde.auth.Repository.UserRepository;
import com.Fahde.config.JwtService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private TokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private AuthenticationService authenticationService;

    private RegisterRequest registerRequest;
    private User user;
    private static final String TEST_TOKEN = "test.jwt.token";
    private static final String TEST_REFRESH_TOKEN = "test.refresh.token";

    @BeforeEach
    void setUp() {
        registerRequest = RegisterRequest.builder()
                .userName("test@example.com")
                .password("password123")
                .role("ADMINISTRATOR")
                .build();

        user = User.builder()
                .username("test@example.com")
                .password("encodedPassword")
                .role(Role.ADMINISTRATOR)
                .build();
    }

    @Test
    void register_SuccessfulRegistration() {
        // Given
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(any(User.class))).thenReturn(TEST_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(TEST_REFRESH_TOKEN);

        // When
        AuthenticationResponse response = authenticationService.register(registerRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(TEST_TOKEN);
        assertThat(response.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);

        verify(userRepository).save(any(User.class));
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void register_ManagerWithoutDepartment_ThrowsException() {
        // Given
        registerRequest.setRole("MANAGER");

        // When/Then
        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Department is required for Managers.");
    }

    @Test
    void register_AdministratorWithDepartment_ThrowsException() {
        // Given
        registerRequest.setDepartment("IT");

        // When/Then
        assertThatThrownBy(() -> authenticationService.register(registerRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Administrators and HR Personnel should not have a department.");
    }

    @Test
    void authenticate_SuccessfulAuthentication() {
        // Given
        AuthenticationRequest authRequest = new AuthenticationRequest("test@example.com", "password123");
        when(userRepository.findByUsername(authRequest.getUserName())).thenReturn(Optional.of(user));
        when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(new ArrayList<>());
        when(jwtService.generateToken(any(User.class))).thenReturn(TEST_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(TEST_REFRESH_TOKEN);

        // When
        AuthenticationResponse response = authenticationService.authenticate(authRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo(TEST_TOKEN);
        assertThat(response.getRefreshToken()).isEqualTo(TEST_REFRESH_TOKEN);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
        );
        verify(tokenRepository).save(any(Token.class));
    }

    @Test
    void refreshToken_SuccessfulRefresh() throws IOException {
        // Given
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn("Bearer " + TEST_REFRESH_TOKEN);
        when(jwtService.extractUsername(TEST_REFRESH_TOKEN)).thenReturn("test@example.com");
        when(userRepository.findByUsername("test@example.com")).thenReturn(Optional.of(user));
        when(jwtService.isTokenValid(TEST_REFRESH_TOKEN, user)).thenReturn(true);
        when(jwtService.generateToken(user)).thenReturn(TEST_TOKEN);
        when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(new ArrayList<>());
        when(response.getOutputStream()).thenReturn(outputStream);

        // When
        authenticationService.refreshToken(request, response);

        // Then
        verify(tokenRepository).save(any(Token.class));
        verify(response).getOutputStream();
    }

    @Test
    void refreshToken_InvalidAuthHeader_NoAction() throws IOException {
        // Given
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);

        // When
        authenticationService.refreshToken(request, response);

        // Then
        verify(jwtService, never()).extractUsername(any());
        verify(tokenRepository, never()).save(any());
    }

    @Test
    void revokeAllUserTokens_SuccessfulRevocation() {
        // Given
        AuthenticationRequest authRequest = new AuthenticationRequest("test@example.com", "password123");
        when(userRepository.findByUsername(authRequest.getUserName())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(any(User.class))).thenReturn(TEST_TOKEN);
        when(jwtService.generateRefreshToken(any(User.class))).thenReturn(TEST_REFRESH_TOKEN);

        List<Token> tokens = new ArrayList<>();
        tokens.add(Token.builder()
                .token("token1")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build());
        tokens.add(Token.builder()
                .token("token2")
                .tokenType(TokenType.BEARER)
                .expired(false)
                .revoked(false)
                .user(user)
                .build());

        when(tokenRepository.findAllValidTokenByUser(any())).thenReturn(tokens);

        // When
        authenticationService.authenticate(authRequest);

        // Then
        ArgumentCaptor<List<Token>> tokenCaptor = ArgumentCaptor.forClass(List.class);
        verify(tokenRepository).saveAll(tokenCaptor.capture());
        List<Token> savedTokens = tokenCaptor.getValue();
        assertThat(savedTokens).hasSize(2);
        assertThat(savedTokens).allMatch(Token::isExpired);
        assertThat(savedTokens).allMatch(Token::isRevoked);
    }
}