package com.fraudscanner.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;   // ✅ Lombok annotations

@Data                   // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Default constructor (needed for frameworks like Spring)
@AllArgsConstructor     // All-args constructor
@Builder                // Optional: fluent builder pattern
public class LoginRequest {

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;
}
