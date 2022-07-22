package com.licenta.models;

import android.annotation.SuppressLint;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Client {
    private int id;
    private String birthday;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private Trainer trainer;
    private User user;
    private String registrationDate;
    private Integer trainer_id;
    private Integer user_id;
    private Set<ClientMembership> memberships;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Trainer getTrainer() {
        return trainer;
    }

    public void setTrainer(Trainer trainer) {
        this.trainer = trainer;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @SuppressLint("NewApi")
    public Membership getMembershipByClub(Integer clubId) {
        List<ClientMembership> clientMemberships = this.memberships.stream().filter(clientMembership -> clientMembership.getMembership().getClub_id() == clubId).collect(Collectors.toList());

        return clientMemberships.size() > 0 ? clientMemberships.get(clientMemberships.size() - 1).getMembership() : null;
    }

    @SuppressLint("NewApi")
    public ClientMembership getClientMembershipByClub(Integer clubId) {
        List<ClientMembership> clientMemberships = this.memberships.stream().filter(clientMembership -> clientMembership.getMembership().getClub_id() == clubId).collect(Collectors.toList());

        return clientMemberships.size() > 0 ? clientMemberships.get(clientMemberships.size() - 1) : null;
    }

    public String getMembershipStartDate(Integer clubId) {
        ClientMembership clientMembership = getClientMembershipByClub(clubId);
        return clientMembership.getMembershipStartDate();
    }

    public String getMembershipEndDate(Integer clubId) {
        ClientMembership clientMembership = getClientMembershipByClub(clubId);
        return clientMembership.getMembershipEndDate();
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<ClientMembership> getMemberships() {
        return memberships;
    }

    public void setMemberships(Set<ClientMembership> memberships) {
        this.memberships = memberships;
    }

    public void addMembership(ClientMembership clientMembership){
        this.memberships.add(clientMembership);
    }

    public Integer getTrainerId() {
        return trainer_id;
    }

    public void setTrainerId(Integer trainerId) {
        this.trainer_id = trainerId;
    }

    public Integer getTrainer_id() {
        return trainer_id;
    }

    public void setTrainer_id(Integer trainer_id) {
        this.trainer_id = trainer_id;
    }

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }
}
