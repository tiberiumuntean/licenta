package com.licenta.models;

public class Appointment {
    private int id;
    private String name;
    private String date;
    private Trainer trainer;
    private Client client;

    public Appointment() {
    }

    public Appointment(int id, String name, String date, Trainer trainer, Client client) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.trainer = trainer;
        this.client = client;
    }

    public Appointment(String name, String date, Trainer trainer, Client client) {
        this.name = name;
        this.date = date;
        this.trainer = trainer;
        this.client = client;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
