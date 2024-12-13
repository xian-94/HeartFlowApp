package com.example.heartflowapp.model;
import java.util.Date;

public class Donor extends User {
    enum Gender {
        MALE,
        FEMALE
    };

    private Gender gender;
    private Date dob;
    private BloodType type;

    // Use to retrieve nearby sites
    private String address;
    private String city;

}
