package com.example.heartflowapp.model;

public class SiteManager extends User {
    private String gender;

    public SiteManager() {
        super();
    }

    public SiteManager(String userId, String email, String password) {
        super(userId, email, password, UserRole.MANAGER);
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
