package com.example.heartflowapp.model;

public enum Gender {
    MALE,
    FEMALE;

    public static Gender getGender(String gender) {
        if (gender.equalsIgnoreCase("female")) {
            return FEMALE;
        }
        return MALE;
    }
}
