package com.fraudscanner.authservice.service;

import com.fraudscanner.authservice.dto.UpdateUserRequest;
import com.fraudscanner.authservice.dto.UpdateUserRoleRequest;
import com.fraudscanner.authservice.dto.UserResponse;
import com.fraudscanner.authservice.entity.Role;
import com.fraudscanner.authservice.entity.User;
import com.fraudscanner.authservice.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminUserService {

    private final UserRepository userRepository;

    public AdminUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public UserResponse getUserById(Long userId) {
        User user = findUserOrThrow(userId);
        return mapToResponse(user);
    }

    public UserResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = findUserOrThrow(userId);

        if (request.getFullName() != null && !request.getFullName().isBlank()) {
            user.setFullName(request.getFullName().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().trim().toLowerCase();

            if (!newEmail.equals(user.getEmail()) && userRepository.existsByEmail(newEmail)) {
                throw new RuntimeException("Email is already used by another user");
            }

            user.setEmail(newEmail);
        }

        return mapToResponse(userRepository.save(user));
    }

    public UserResponse updateUserRole(Long userId, UpdateUserRoleRequest request) {
        User user = findUserOrThrow(userId);

        Role newRole = parseRole(request.getRole());
        user.setRole(newRole);

        return mapToResponse(userRepository.save(user));
    }

    public UserResponse enableUser(Long userId) {
        User user = findUserOrThrow(userId);
        user.setActive(true);

        return mapToResponse(userRepository.save(user));
    }

    public UserResponse disableUser(Long userId) {
        User user = findUserOrThrow(userId);
        user.setActive(false);

        return mapToResponse(userRepository.save(user));
    }

    public void deleteUser(Long userId) {
        User user = findUserOrThrow(userId);
        userRepository.delete(user);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
    }

    private Role parseRole(String roleValue) {
        if (roleValue == null || roleValue.isBlank()) {
            throw new RuntimeException("Role is required");
        }

        try {
            return Role.valueOf(roleValue.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new RuntimeException("Invalid role. Allowed roles: ADMIN, ANALYST, USER");
        }
    }

    private UserResponse mapToResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole())
                .active(user.isActive())
                .createdAt(user.getCreatedAt())
                .build();
    }
}