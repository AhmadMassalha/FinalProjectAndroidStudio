package com.example.androidfinalproject.Utilities;

public class User {
    private String email;
    private String status;
    private String name; // Added field for name

    // Default constructor is required for Firebase Realtime Database
    public User() {
        // Default constructor necessary for data retrieval from Firebase
    }

    // Updated constructor to include name
    public User(String name, String email, String status) {
        this.name = name;
        this.email = email;
        this.status = status;
    }

    // Getter and setter for name
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // Updated toString method to include name
    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
