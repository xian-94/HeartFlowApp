package com.example.heartflowapp.model;

public class SiteManager extends User {
    private String siteId;
    private String position;

    public SiteManager() {
        super();
    }
    public SiteManager(String userId, String email, String password) {
        super(userId, email, password, UserRole.MANAGER);
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public String getSiteId() {
        return siteId;
    }

    public String getPosition() {
        return position;
    }
}
