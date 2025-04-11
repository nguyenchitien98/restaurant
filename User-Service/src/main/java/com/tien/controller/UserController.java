package com.tien.controller;

import com.tien.dto.response.UserResponse;
import com.tien.model.User;
import com.tien.model.elasticsearch.UserDocument;
import com.tien.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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

    @GetMapping("/page")
    public Page<User> getUsersPage(@RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "50") int size) {
        return userService.getUsers(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public List<UserDocument> searchUser(@RequestParam String userName) {
        return userService.searchByUsername(userName);
    }

    @PutMapping
    public ResponseEntity<User> updateUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cache/all")
    public ResponseEntity<Void> clearCache() {
        userService.clearAllUserCache();
        return ResponseEntity.noContent().build();
    }

//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
//        // Logic login (sẽ làm tiếp trong phần xác thực)
//        UserResponse user = userService.getUserById(id);
//        return ResponseEntity.ok(user);
//    }
}
