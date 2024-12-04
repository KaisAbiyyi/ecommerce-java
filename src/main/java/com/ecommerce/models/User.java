package com.ecommerce.models;

import java.time.LocalDateTime;

import com.ecommerce.utils.PasswordUtils;

public class User {

    private int id;
    private String username;
    private String email;
    private String password; // Disimpan dalam bentuk hashed
    private Role role; // Menggunakan enum untuk role
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum Role {
        ADMIN, CUSTOMER, SELLER;

        public static Role fromString(String roleString) {
            for (Role role : Role.values()) {
                if (role.name().equalsIgnoreCase(roleString)) {
                    return role;
                }
            }
            throw new IllegalArgumentException("Invalid role: " + roleString);
        }
    }

    // Constructor kosong
    public User() {
    }

    // Constructor penuh
    public User(int id, String username, String email, String password, Role role, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        throw new UnsupportedOperationException("Direct access to password is not allowed.");
    }

    public void setPassword(String password) {
        if (!PasswordUtils.isHashed(password)) { // Tambahkan logika untuk memeriksa apakah password sudah di-hash
            this.password = PasswordUtils.encrypt(password);
        } else {
            this.password = password;
        }
    }

    public boolean checkPassword(String rawPassword) {
        return PasswordUtils.verify(rawPassword, this.password);
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    @Override
    public String toString() {
        return "User{"
                + "id=" + id
                + ", username='" + username + '\''
                + ", email='" + email + '\''
                + ", role=" + role
                + ", createdAt=" + createdAt
                + ", updatedAt=" + updatedAt
                + '}';
    }
}
