package com.fraudscanner.authservice.controller;

import com.fraudscanner.authservice.dto.AuthResponse;
import com.fraudscanner.authservice.dto.LoginRequest;
import com.fraudscanner.authservice.dto.RegisterRequest;
import com.fraudscanner.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
private final AuthService authService ;
public AuthController (AuthService authService){
    this.authService=authService ;
}
@PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request){
AuthResponse response = authService.register(request);
return ResponseEntity.status(HttpStatus.CREATED).body(response);

}
@PostMapping("/login")
public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
}
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Auth Service is running");
    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        // In stateless JWT, logout just means the client deletes the token.
        return ResponseEntity.ok("Logged out successfully");
    }

}

