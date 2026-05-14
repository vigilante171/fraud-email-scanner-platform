package com.fraudscanner.authservice.controller;

import com.fraudscanner.authservice.dto.UpdateUserRequest;
import com.fraudscanner.authservice.dto.UpdateUserRoleRequest;
import com.fraudscanner.authservice.dto.UserResponse;
import com.fraudscanner.authservice.service.AdminUserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "*")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(adminUserService.getAllUsers());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.getUserById(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Long userId,
            @RequestBody UpdateUserRequest request
    ) {
        return ResponseEntity.ok(adminUserService.updateUser(userId, request));
    }

    @PutMapping("/{userId}/role")
    public ResponseEntity<UserResponse> updateUserRole(
            @PathVariable Long userId,
            @RequestBody UpdateUserRoleRequest request
    ) {
        return ResponseEntity.ok(adminUserService.updateUserRole(userId, request));
    }

    @PutMapping("/{userId}/enable")
    public ResponseEntity<UserResponse> enableUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.enableUser(userId));
    }

    @PutMapping("/{userId}/disable")
    public ResponseEntity<UserResponse> disableUser(@PathVariable Long userId) {
        return ResponseEntity.ok(adminUserService.disableUser(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long userId) {
        adminUserService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}