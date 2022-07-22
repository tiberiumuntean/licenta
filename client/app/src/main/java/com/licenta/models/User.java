package com.licenta.models;

import java.util.Set;

public class User {
    private int id;
    private int role;
    private String email;
    private String password;
    private String image;
    private Set<Club> clubs;
    private Trainer trainer;
    private Client client;
    private String registrationDate;

    public User() {
    }

    public User(int id, int role, String email, String password, String image, Set<Club> clubs, Trainer trainer, Client client, String registrationDate) {
        this.id = id;
        this.role = role;
        this.email = email;
        this.password = password;
        this.image = image;
        this.clubs = clubs;
        this.trainer = trainer;
        this.client = client;
        this.registrationDate = registrationDate;
    }

    public User(int role, String email, String password, String image, Set<Club> clubs, Trainer trainer, Client client, String registrationDate) {
        this.role = role;
        this.email = email;
        this.password = password;
        this.image = image;
        this.clubs = clubs;
        this.trainer = trainer;
        this.client = client;
        this.registrationDate = registrationDate;
    }

    public User(int id, String email, String password, Set<Club> clubs, Trainer trainer, Client client) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.clubs = clubs;
        this.trainer = trainer;
        this.client = client;
    }

    public User(String email, String password, Set<Club> clubs, Trainer trainer, Client client) {
        this.email = email;
        this.password = password;
        this.clubs = clubs;
        this.trainer = trainer;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Club> getClubs() {
        return clubs;
    }

    public void setClubs(Set<Club> clubs) {
        this.clubs = clubs;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public void addClub(Club club){
        this.clubs.add(club);
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
