package com.example.qrstaff;


public class User {
    public String name, designation, employeeCode, phoneNumber;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String designation, String employeeCode, String phoneNumber) {
        this.name = name;
        this.designation = designation;
        this.employeeCode = employeeCode;
        this.phoneNumber = phoneNumber;
    }
}
