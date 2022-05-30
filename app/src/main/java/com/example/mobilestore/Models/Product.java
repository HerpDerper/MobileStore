package com.example.mobilestore.Models;

public class Product {

    private String productName, categoryName, description, guarantee, manufacturerName, productImage;
    private int productCount, ratingCount;
    private float rating, price;

    public Product() {
    }

    public Product(String productName, String categoryName, String description, String guarantee, String manufacturerName, String productImage, int productCount, int ratingCount, float rating, float price) {
        this.productName = productName;
        this.categoryName = categoryName;
        this.description = description;
        this.guarantee = guarantee;
        this.manufacturerName = manufacturerName;
        this.productImage = productImage;
        this.productCount = productCount;
        this.ratingCount = ratingCount;
        this.rating = rating;
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getDescription() {
        return description;
    }

    public String getGuarantee() {
        return guarantee;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getProductImage() {
        return productImage;
    }

    public int getProductCount() {
        return productCount;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public float getRating() {
        return rating;
    }

    public float getPrice() {
        return price;
    }
}