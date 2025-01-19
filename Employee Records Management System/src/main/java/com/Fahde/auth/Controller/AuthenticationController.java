package com.Fahde.auth.Controller;

import com.Fahde.auth.DTO.AuthenticationRequest;
import com.Fahde.auth.DTO.AuthenticationResponse;
import com.Fahde.auth.Service.AuthenticationService;
import com.Fahde.auth.DTO.RegisterRequest;
import com.Fahde.config.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor

public class AuthenticationController {

  private final AuthenticationService service;
  private final JwtService jwtService;

  @PostMapping("/register")
  public ResponseEntity<AuthenticationResponse> register(
      @RequestBody RegisterRequest request
  ) {
    return ResponseEntity.ok(service.register(request));
  }
  @PostMapping("/authenticate")
  public ResponseEntity<AuthenticationResponse> authenticate(
      @RequestBody AuthenticationRequest request
  ) {
    return ResponseEntity.ok(service.authenticate(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(
      HttpServletRequest request,
      HttpServletResponse response
  ) throws IOException {
    service.refreshToken(request, response);
  }


  @GetMapping("/extract")
  public Integer extractIdFromToken(@RequestHeader("Authorization") String authorizationHeader) {
    // Extract the token from the "Bearer <token>" format
    String token = authorizationHeader.replace("Bearer ", "");

    // Use JwtService to extract the ID
    Integer id = jwtService.extractId(token);

    return id;
  }


}
