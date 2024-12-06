package com.ecommerce.models;

import java.time.LocalDateTime;

public class OrderSeller {

    private int id; // ID unik untuk sub-pesanan (per seller)
    private int orderId; // ID pesanan utama (referensi ke tabel orders)
    private int sellerId; // ID penjual (referensi ke tabel users dengan role SELLER)
    private Status status; // Status sub-pesanan (enum: PENDING, SHIPPED, COMPLETED)
    private LocalDateTime createdAt; // Waktu sub-pesanan dibuat
    private LocalDateTime updatedAt; // Waktu sub-pesanan terakhir diperbarui

    // Enum untuk status sub-pesanan
    public enum Status {
        PENDING, SHIPPED, COMPLETED;

        public static Status fromString(String statusString) {
            for (Status status : Status.values()) {
                if (status.name().equalsIgnoreCase(statusString)) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status: " + statusString);
        }
    }

    // Constructor kosong
    public OrderSeller() {}

    // Constructor penuh
    public OrderSeller(int id, int orderId, int sellerId, Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.orderId = orderId;
        this.sellerId = sellerId;
        this.status = status;
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

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getSellerId() {
        return sellerId;
    }

    public void setSellerId(int sellerId) {
        this.sellerId = sellerId;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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
        return "OrderSeller{" +
                "id=" + id +
                ", orderId=" + orderId +
                ", sellerId=" + sellerId +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
