package br.com.ccgl.sunharvestbackend.controller;

import br.com.ccgl.sunharvestbackend.domain.AuthResponse;
import br.com.ccgl.sunharvestbackend.domain.LoginRequest;
import br.com.ccgl.sunharvestbackend.domain.RegisterRequest;
import br.com.ccgl.sunharvestbackend.entity.User;
import br.com.ccgl.sunharvestbackend.security.JwtService;
import br.com.ccgl.sunharvestbackend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.register(request.displayName(), request.email(), request.password());
        String token = jwtService.generateToken(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(new AuthResponse(token));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails user = userService.loadUserByUsername(request.email());
        String token = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        UserDetails user = userService.loadUserByUsername(email);
        if (jwtService.isTokenValid(token, user)) {
            return ResponseEntity.ok(new AuthResponse(jwtService.generateToken(user)));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
