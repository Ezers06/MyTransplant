package com.example.mytransplant;

public class Doctor {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String hospitalName;
    private String hospitalAddress;

    public Doctor() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Doctor(String fullName, String email, String phoneNumber) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;

    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public String getHospitalAddress() {
        return hospitalAddress;
    }
}
