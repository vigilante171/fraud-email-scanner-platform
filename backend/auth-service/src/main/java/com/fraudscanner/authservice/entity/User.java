package com.fraudscanner.authservice.entity;

import jakarta.persistence.*;
import lombok.*;   // ✅ Lombok annotations
import java.time.LocalDateTime;

@Data                   // Generates getters, setters, toString, equals, hashCode
@NoArgsConstructor      // Default constructor
@AllArgsConstructor     // All-args constructor
@Builder                // Optional: fluent builder pattern
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private boolean active = true;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        active = true;
    }
}
