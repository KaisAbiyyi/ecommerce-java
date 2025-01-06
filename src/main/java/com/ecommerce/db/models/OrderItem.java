package com.ecommerce.db.models;

import java.time.LocalDateTime;

public class OrderItem {

    private int id; // ID unik untuk item dalam pesanan
    private int orderSellerId; // ID sub-pesanan (referensi ke tabel order_sellers)
    private int productId; // ID produk yang dipesan (referensi ke tabel products)
    private int quantity; // Jumlah produk yang dipesan
    private double price; // Harga total untuk item (quantity x harga produk)
    private LocalDateTime createdAt; // Waktu item dipesan
    private LocalDateTime updatedAt; // Waktu item terakhir diperbarui

    // Constructor kosong
    public OrderItem() {}

    // Constructor penuh
    public OrderItem(int id, int orderSellerId, int productId, int quantity, double price, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderSellerId = orderSellerId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters dan Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderSellerId() {
        return orderSellerId;
    }

    public void setOrderSellerId(int orderSellerId) {
        this.orderSellerId = orderSellerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // toString() untuk debugging dan logging
    @Override
    public String toString() {
        return "OrderItem{" +
                "id=" + id +
                ", orderSellerId=" + orderSellerId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
