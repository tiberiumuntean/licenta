package com.licenta.models;

import java.util.Date;

public class ClientMembership {
    private int id;
    private Client client;
    private Membership membership;
    private String membershipStartDate;
    private String membershipEndDate;

    public ClientMembership() {
    }

    public ClientMembership(int id, Client client, Membership membership, String membershipStartDate, String membershipEndDate) {
        this.id = id;
        this.client = client;
        this.membership = membership;
        this.membershipStartDate = membershipStartDate;
        this.membershipEndDate = membershipEndDate;
    }

    public ClientMembership(Client client, Membership membership, String membershipStartDate, String membershipEndDate) {
        this.client = client;
        this.membership = membership;
        this.membershipStartDate = membershipStartDate;
        this.membershipEndDate = membershipEndDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Membership getMembership() {
        return membership;
    }

    public void setMembership(Membership membership) {
        this.membership = membership;
    }

    public String getMembershipStartDate() {
        return membershipStartDate;
    }

    public void setMembershipStartDate(String membershipStartDate) {
        this.membershipStartDate = membershipStartDate;
    }

    public String getMembershipEndDate() {
        return membershipEndDate;
    }

    public void setMembershipEndDate(String membershipEndDate) {
        this.membershipEndDate = membershipEndDate;
    }
}
