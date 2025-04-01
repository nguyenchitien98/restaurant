package com.tien.controller;

import com.tien.dto.request.LoginRequest;
import com.tien.dto.request.UserRequest;
import com.tien.dto.response.UserResponse;
import com.tien.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUser() {
        // Logic login (sẽ làm tiếp trong phần xác thực)
        List<UserResponse> user = userService.getAllUser();
        return ResponseEntity.ok(user);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody UserRequest user) {
        return new ResponseEntity<>(userService.createUser(user), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponse> loginUser(@RequestBody LoginRequest request) {
        // Logic login (sẽ làm tiếp trong phần xác thực)
        UserResponse user = userService.getUserByUsername(request.getUsername());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        // Logic login (sẽ làm tiếp trong phần xác thực)
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }
}
