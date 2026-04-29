package com.fraudscanner.authservice.dto;

import lombok.*;   // ✅ Lombok annotations

@Data                   // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Default constructor
@AllArgsConstructor     // All-args constructor
@Builder                // Optional: fluent builder pattern
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";  // Default value
    private Long userId;
    private String fullName;
    private String email;
    private String role;
}
