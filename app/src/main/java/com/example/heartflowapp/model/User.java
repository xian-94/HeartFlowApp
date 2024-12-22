package com.example.heartflowapp.model;

public class User {
    private String userId;
private String fullName;
    private String password;
    private String email; // Need validation
    private String phone; // Need validation
    private UserRole role;

    public User() {

    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public User(String userId, String email, String password, UserRole role) {
        this.userId = userId;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public String getFullName() {
        return fullName;
    }


    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public String getUserId() {
        return userId;
    }

    public UserRole getRole() {
        return role;
    }

    public void setFullName(String name) {
        this.fullName = name;
    }


    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
