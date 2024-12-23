package com.example.heartflowapp.model;

import java.util.List;

public class Event {
    private String id;
    private String name;
    private String date;
    private String status;
    private String siteId;
    private List<String> donors;
    private List<String> volunteers;


    public Event() {
    }

    public Event(String name, String date, String status, String siteId) {
        this.name = name;
        this.date = date;
        this.status = status;
        this.siteId = siteId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSiteId() {
        return siteId;
    }

    public void setSiteId(String siteId) {
        this.siteId = siteId;
    }

    public List<String> getDonors() {
        return donors;
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

    public void addVolunteer(String manager) {
        this.volunteers.add(manager);
    }
}
