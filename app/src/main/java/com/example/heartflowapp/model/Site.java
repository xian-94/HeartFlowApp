package com.example.heartflowapp.model;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Site {
    private String siteId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String createdBy;
    private List<String> managerIds;
    private int totalDonors;
    private Timestamp createdAt;

    public Site(String name, String address, double latitude, double longitude, String createdBy) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdBy = createdBy;
        this.createdAt = Timestamp.now();
        this.managerIds = new ArrayList<>();
        this.totalDonors = 0;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getTotalDonors() {
        return totalDonors;
    }

    public List<String> getManagerIds() {
        return managerIds;
    }

    public String getAddress() {
        return address;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getName() {
        return name;
    }

    public String getSiteId() {
        return siteId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setManagerIds(List<String> managerIds) {
        this.managerIds = managerIds;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public void setTotalDonors(int totalDonors) {
        this.totalDonors = totalDonors;
    }
}
