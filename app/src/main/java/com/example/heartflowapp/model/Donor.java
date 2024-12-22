package com.example.heartflowapp.model;

import java.util.Date;

public class Donor extends User {

    private Gender gender;
    private String dob;
    private BloodType type;

    // Use to retrieve nearby sites
    private String address;
    private String city;

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Donor() {
        super();

    }

    public Donor(String userId, String email, String password) {
        super(userId, email, password, UserRole.DONOR);
    }


    public BloodType getType() {
        return type;
    }

    public String getDob() {
        return dob;
    }

    public Gender getGender() {
        return gender;
    }

    public String getAddress() {
        return address;
    }

    public String getCity() {
        return city;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setType(BloodType type) {
        this.type = type;
    }
}
