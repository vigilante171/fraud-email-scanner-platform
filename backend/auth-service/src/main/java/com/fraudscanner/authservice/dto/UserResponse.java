package com.fraudscanner.authservice.dto;

import com.fraudscanner.authservice.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;

    private String fullName;

    private String email;

    private Role role;

    private boolean active;

    private LocalDateTime createdAt;
}