package com.bluetooth.le.model;

/**
 * Created by stadiko on 1/20/14.
 */
public class StoreItem {
    private String itemId;
    private Category category;
    private String name;
    private double price;

    public StoreItem(String itemId, Category category, String name, double price) {
        this.itemId = itemId;
        this.category = category;
        this.name = name;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public Category getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }
}
