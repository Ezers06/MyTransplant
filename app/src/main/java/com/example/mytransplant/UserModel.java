package com.example.mytransplant;

import com.google.firebase.firestore.DocumentSnapshot;

public class UserModel {
    private String name;
    private String identityCard;
    private String address;
    private String phoneNumber;
    private String bloodType;
    private String organType;
    private String documentId;
    private String hospitalName;
    private String hospitalAddress;

    public UserModel() {
        // Required empty constructor for Firestore
    }

    public UserModel(DocumentSnapshot document) {
        this.name = document.getString("fullName");
        this.identityCard = document.getString("icNumber");
        this.address = document.getString("address");
        this.documentId = document.getId();
        this.phoneNumber = document.getString("phoneNumber");
        this.hospitalName = document.getString("hospitalName");
        this.hospitalAddress = document.getString("hospitalAddress");
        this.bloodType = document.getString("bloodType");
        if (document.contains("organDonation")) {
            this.organType = document.getString("organDonation");
        } else if (document.contains("organRequest")) {
            this.organType = document.getString("organRequest");
        } else {
            this.organType = "DefaultOrganType";
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getIdentityCard() {
        return identityCard;
    }

    public void setIdentityCard(String identityCard) {
        this.identityCard = identityCard;
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

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public String getOrganType() {
        return organType;
    }

    public void setOrganType(String organType) {
        this.organType = organType;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        this.hospitalName = hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }

    public void setHospitalAddress(String hospitalAddress) {
        this.hospitalAddress = hospitalAddress;
    }
}
