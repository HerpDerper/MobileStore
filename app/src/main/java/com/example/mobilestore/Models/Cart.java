package com.example.mobilestore.Models;

public class Cart {

    private String userName, productName;
    private int productCount;

    public Cart() {
    }

    public Cart(String userName, String productName, int productCount) {
        this.userName = userName;
        this.productName = productName;
        this.productCount = productCount;
    }

    public String getUserName() {
        return userName;
    }

    public String getProductName() {
        return productName;
    }

    public int getProductCount() {
        return productCount;
    }
}