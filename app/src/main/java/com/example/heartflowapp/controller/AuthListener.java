package com.example.heartflowapp.controller;

public interface AuthListener {
    public void onSignUp(String role, String email, String password);

    public void onLogin(String email, String password);
}
