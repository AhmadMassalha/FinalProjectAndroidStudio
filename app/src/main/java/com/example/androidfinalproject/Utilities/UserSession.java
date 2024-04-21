package com.example.androidfinalproject.Utilities;

import java.util.ArrayList;
import java.util.List;

public class UserSession {

    private static UserSession instance;
    private String email;
    private List<User> userList = new ArrayList<>();
    private boolean userPermission = false;

    // Private constructor prevents instantiation from other classes
    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            // Ensure thread-safe instantiation
            synchronized (UserSession.class) {
                if (instance == null) {
                    instance = new UserSession();
                }
            }
        }
        return instance;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setUserPermission(Boolean permission){
        this.userPermission = permission;
    }
    public boolean getUserPermission(){
        return this.userPermission;
    }

    // Gets the list of users marked as workers
    public List<User> getUserList() {
        return new ArrayList<>(userList); // Returns a copy of the list to prevent external modifications
    }

    // Sets or updates the list of users who are workers
    public void setWorkers(List<User> workers) {
        this.userList.clear(); // Clears the current list
        this.userList.addAll(workers); // Adds all new workers to the list
        // Print the list of workers to the console
        System.out.println("Updated list of workers:");
        for (User user : userList) {
            System.out.println("User: " + user.getName() + ", Email: " + user.getEmail() + ", Status: " + user.getStatus());
        }
    }
}