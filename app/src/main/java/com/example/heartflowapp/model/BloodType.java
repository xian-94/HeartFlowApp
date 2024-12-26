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

    public static BloodType parseBloodTpe(String type) {
        switch (type) {
            case "A_PLUS":
                return A_PLUS;
            case "A_MINUS":
                return A_MINUS;
            case "B_PLUS":
                return B_PLUS;
            case "B_MINUS":
                return B_MINUS;
            case "AB_PLUS":
                return AB_PLUS;
            case "AB_MINUS":
                return AB_MINUS;
            case "O_PLUS":
                return O_PLUS;
            case "O_MINUS":
                return O_MINUS;
            default:
                return null;
        }
    }

    public static String toString(BloodType type) {
        switch (type) {
            case A_PLUS:
                return "A+";
            case A_MINUS:
                return "A-";
            case B_PLUS:
                return "B+";
            case B_MINUS:
                return "B-";
            case AB_PLUS:
                return "AB+";
            case AB_MINUS:
                return "AB-";
            case O_PLUS:
                return "O+";
            case O_MINUS:
                return "O-";
            default:
                return "";
        }
    }

}
