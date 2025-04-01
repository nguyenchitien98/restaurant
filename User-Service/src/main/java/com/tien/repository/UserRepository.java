package com.tien.repository;

import com.tien.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
    User findByUsername(String username);
    User findByUserId(Long id);
    List<User> findAll();
}
