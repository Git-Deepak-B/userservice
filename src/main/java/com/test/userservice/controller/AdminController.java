package com.test.userservice.controller;

import com.test.userservice.dto.RegisterUserRequestDTO;
import com.test.userservice.service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/userList")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> userList() {
        return adminService.getUserList();
    }

    @PostMapping("/createUser")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@Valid @RequestBody RegisterUserRequestDTO registerUserRequestDTO) {
        return adminService.registerUser(registerUserRequestDTO);
    }

    @GetMapping("/deleteUser/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable String username) {
        return adminService.deleteUser(username);
    }
}
