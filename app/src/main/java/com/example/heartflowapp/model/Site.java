package com.example.heartflowapp.model;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Site implements Serializable {
    private String siteId;
    private String name;
    private String address;
    private double latitude;
    private double longitude;
    private String createdBy;
    private String date;
    private List<String> donors;
    private List<String> volunteers;
    private Map<String, Double> requiredBloodTypes;
    private Timestamp createdAt;
    private String status;

    public Site(String name, String address, double latitude, double longitude, String date, List<String> requiredBloodTypes) {
        this.name = name;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.createdAt = Timestamp.now();
        this.donors = new ArrayList<>();
        this.volunteers = new ArrayList<>();
        this.status = "ACTIVE";
        this.date = date;
        this.requiredBloodTypes = new HashMap<>();
        for (String type : requiredBloodTypes) {
            this.requiredBloodTypes.put(type, 0.0);
        }
    }

    public Site() {

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
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


    public void setName(String name) {
        this.name = name;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public List<String> getDonors() {
        return this.donors;
    }

    public List<String> getVolunteers() {
        return volunteers;
    }

    public void setDonors(List<String> donors) {
        this.donors = donors;
    }

    public void setVolunteers(List<String> volunteers) {
        this.volunteers = volunteers;
    }

    public void addDonor(String donor) {
        this.donors.add(donor);
    }

    public void addVolunteer(String volunteer) {
        this.donors.add(volunteer);
    }

    public Map<String, Double> getRequiredBloodTypes() {
        return requiredBloodTypes;
    }

    public void setRequiredBloodTypes(Map<String, Double> requiredBloodTypes) {
        this.requiredBloodTypes = requiredBloodTypes;
    }

    public void setBloodAmount(String type, double amount) {
        this.requiredBloodTypes.put(type, amount);
    }


}
