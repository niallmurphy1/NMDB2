package com.niall.nmdb.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class User {

    private String email;
    private String username;

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("username", username);
        return result;
    }

    public User(String email, String username) {
        this.email = email;
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
