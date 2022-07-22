package com.licenta.user;

import com.licenta.models.Club;

import java.util.Set;

public class UserLogin {
    private String id;
    private String token;
    private String type;
    private String email;
    private Set<Club> clubs;
    private int role;

    public UserLogin(String id, String token, String type, String email, Set<Club> clubs, int role) {
        this.id = id;
        this.token = token;
        this.type = type;
        this.email = email;
        this.clubs = clubs;
        this.role = role;
    }

    public Set<Club> getClubs() {
        return clubs;
    }

    public void setClubs(Set<Club> clubs) {
        this.clubs = clubs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
