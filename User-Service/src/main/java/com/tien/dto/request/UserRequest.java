package com.tien.dto.request;

import lombok.Data;

@Data
public class UserRequest {

    private Long userId;
    private String username;
    private String password;
    private String email;
    private String role;
}
