package com.tang.demo_db.dto;

import lombok.Data;

@Data
public class UserProfileDTO {
    private String username;
    private String email;
    private int age;
    // Constructor
    public UserProfileDTO(String username, String email, int age) {
        this.username = username;
        this.email = email;
        this.age = age;
    }
    // Getters and setters
}
