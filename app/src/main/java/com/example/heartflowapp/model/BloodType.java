package com.example.heartflowapp.model;

public enum BloodType {
    A_PLUS,
    A_MINUS,
    B_PLUS,
    B_MINUS,
    O_PLUS,
    O_MINUS,
    AB_PLUS,
    AB_MINUS;

    public static BloodType getType(String type) {
        switch (type) {
            case "A+":
                return A_PLUS;
            case "A-":
                return A_MINUS;
            case "B+":
                return B_PLUS;
            case "B-":
                return B_MINUS;
            case "AB+":
                return AB_PLUS;
            case "AB-":
                return AB_MINUS;
            case "O+":
                return O_PLUS;
            case "O-":
                return O_MINUS;
            default:
                return null;
        }
    }

}
