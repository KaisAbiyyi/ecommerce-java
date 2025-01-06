package com.ecommerce.db.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int id; // ID unik untuk pesanan
    private int userId; // ID pengguna yang membuat pesanan
    private BigDecimal totalPrice; // Total harga pesanan
    private Status status; // Status pesanan (enum: PENDING, PAID, COMPLETED)
    private LocalDateTime createdAt; // Waktu pesanan dibuat
    private LocalDateTime updatedAt; // Waktu pesanan terakhir diperbarui

    // Enum untuk status pesanan
    public enum Status {
        PENDING, PAID, COMPLETED;

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
    public Order() {}

    // Constructor penuh
    public Order(int id, int userId, BigDecimal totalPrice, Status status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.totalPrice = totalPrice;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
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
        return "Order{" +
                "id=" + id +
                ", userId=" + userId +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
