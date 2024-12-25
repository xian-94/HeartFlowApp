package com.example.heartflowapp.model;


import java.io.Serializable;

public class Donor extends User implements Serializable {

    private String gender;
    private String dob;
    private BloodType type;


    public void setGender(String gender) {
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

    public String getGender() {
        return gender;
    }


    public void setDob(String dob) {
        this.dob = dob;
    }

    public void setType(BloodType type) {
        this.type = type;
    }
}
