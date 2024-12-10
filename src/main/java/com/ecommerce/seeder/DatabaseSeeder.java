package com.ecommerce.seeder;

import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class DatabaseSeeder {

    public static void main(String[] args) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            System.out.println("Starting seeding process...");

            // Jalankan seed data
            seedUsers(connection);
            seedCategories(connection);

            System.out.println("Seeding process completed successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Seeding process failed: " + e.getMessage());
        }
    }

    private static void seedUsers(Connection connection) throws Exception {
        System.out.println("Seeding users...");

        String sql = "INSERT INTO users (username, email, password, role, created_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Tambahkan data pengguna
            statement.setString(1, "admin");
            statement.setString(2, "admin@example.com");
            statement.setString(3, PasswordUtils.encrypt("adminpass")); // Hash password
            statement.setString(4, "ADMIN");
            statement.executeUpdate();

            statement.setString(1, "customer");
            statement.setString(2, "customer@example.com");
            statement.setString(3, PasswordUtils.encrypt("customerpass")); // Hash password
            statement.setString(4, "CUSTOMER");
            statement.executeUpdate();

            statement.setString(1, "seller");
            statement.setString(2, "seller@example.com");
            statement.setString(3, PasswordUtils.encrypt("sellerpass")); // Hash password
            statement.setString(4, "SELLER");
            statement.executeUpdate();
        }

        System.out.println("User seeding completed.");
    }

    private static void seedCategories(Connection connection) throws Exception {
        System.out.println("Seeding categories...");

        String sql = "INSERT INTO categories (name, created_at, updated_at) VALUES (?, NOW(), NOW())";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            // Tambahkan data kategori
            statement.setString(1, "Electronics");
            statement.executeUpdate();

            statement.setString(1, "Books");
            statement.executeUpdate();

            statement.setString(1, "Clothing");
            statement.executeUpdate();
        }

        System.out.println("Category seeding completed.");
    }
}
