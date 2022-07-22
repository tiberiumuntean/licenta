package com.licenta.models;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.util.Set;

public class FitnessClass {
    private int id;
    private String name;
    private String date;
    private String description;
    private String location;
    private Integer freeSpots;
    private Trainer trainer;
    private int club_id;
    private Set<Client> clients;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getFreeSpots() {
        return freeSpots;
    }

    public void setFreeSpots(Integer freeSpots) {
        this.freeSpots = freeSpots;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Set<Client> getClients() {
        return clients;
    }

    public void setClients(Set<Client> clients) {
        this.clients = clients;
    }

    public void addClient(Client client){
        this.clients.add(client);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void removeClient(int id){
        this.clients.removeIf(client -> client.getId() == id);
    }

    public int getClub_id() {
        return club_id;
    }

    public void setClub_id(int club_id) {
        this.club_id = club_id;
    }
}
