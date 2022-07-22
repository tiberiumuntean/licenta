package com.licenta.models;

public class Review {
    private int id;
    private Float rating;
    private Trainer trainer;
    private Client client;
    private String creationDate;
    private String review;

    public Review() {
    }

    public Review(int id, Float rating, Trainer trainer, Client client, String review) {
        this.id = id;
        this.rating = rating;
        this.trainer = trainer;
        this.client = client;
        this.review = review;
    }

    public Review(Float rating, Trainer trainer, Client client, String review) {
        this.rating = rating;
        this.trainer = trainer;
        this.client = client;
        this.review = review;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
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

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
