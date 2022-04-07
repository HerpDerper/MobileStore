package com.example.mobilestore.Models;

public class Manufacturer {
    private String manufacturerName, address;

    public Manufacturer() {
    }

    public Manufacturer(String manufacturerName, String address) {
        this.manufacturerName = manufacturerName;
        this.address = address;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getAddress() {
        return address;
    }
}