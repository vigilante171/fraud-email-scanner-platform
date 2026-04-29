package com.fraudscanner.authservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;   // ✅ Lombok annotations

@Data                   // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Default constructor (needed for frameworks like Spring)
@AllArgsConstructor     // All-args constructor
@Builder                // Optional: fluent builder pattern
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Email must be valid")
    @NotBlank(message = "Email is required")
    private String email;

    @Size(min = 6, message = "Password must contain at least 6 characters")
    @NotBlank(message = "Password is required")
    private String password;

    private String role;
}
