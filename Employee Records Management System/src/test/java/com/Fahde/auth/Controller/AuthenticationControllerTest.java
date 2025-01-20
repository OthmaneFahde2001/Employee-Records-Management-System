package com.Fahde.auth.Controller;

import com.Fahde.auth.DTO.AuthenticationRequest;
import com.Fahde.auth.DTO.AuthenticationResponse;
import com.Fahde.auth.DTO.RegisterRequest;
import com.Fahde.auth.Entity.Role;
import com.Fahde.auth.Service.AuthenticationService;
import com.Fahde.config.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@ExtendWith(SpringExtension.class)
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authenticationService;

    @MockBean
    private JwtService jwtService;

    @Test
    void shouldRegisterNewUser() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest("user", "password", Role.ADMINISTRATOR.name(), null);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("access-token", "refresh-token");

        Mockito.when(authenticationService.register(Mockito.any(RegisterRequest.class)))
                .thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\", \"password\":\"password\", \"role\":\"ADMINISTRATOR\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.id").value("refresh-token"));
    }

    @Test
    void shouldAuthenticateUser() throws Exception {
        AuthenticationRequest authRequest = new AuthenticationRequest("user", "password");
        AuthenticationResponse authenticationResponse = new AuthenticationResponse("access-token", "refresh-token");

        Mockito.when(authenticationService.authenticate(Mockito.any(AuthenticationRequest.class)))
                .thenReturn(authenticationResponse);

        mockMvc.perform(post("/api/v1/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"user\", \"password\":\"password\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("access-token"))
                .andExpect(jsonPath("$.id").value("refresh-token"));
    }

    @Test
    void shouldRefreshToken() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .header("Authorization", "Bearer fahde-refresh-token"))
                .andExpect(status().isOk());
        Mockito.verify(authenticationService).refreshToken(any(), any());
    }

    @Test
    void shouldExtractIdFromToken() throws Exception {
        String token = "fahde-token";
        Integer expectedId = 1;

        Mockito.when(jwtService.extractId(anyString()))
                .thenReturn(expectedId);

        mockMvc.perform(get("/api/v1/auth/extract")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(expectedId));
    }
}
