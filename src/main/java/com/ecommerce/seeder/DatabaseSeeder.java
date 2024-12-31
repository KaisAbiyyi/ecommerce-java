package com.ecommerce.seeder;

import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.utils.PasswordUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseSeeder {

    public static void main(String[] args) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            // Nonaktifkan auto-commit untuk transaksi
            connection.setAutoCommit(false);

            System.out.println("Starting seeding process...");

            seedUsers(connection);
            seedCategories(connection);
            seedProducts(connection);

            // Commit jika semua berhasil
            connection.commit();

            System.out.println("Seeding process completed successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Seeding process failed: " + e.getMessage());
        }
    }

    private static void seedUsers(Connection connection) throws SQLException {
        System.out.println("Seeding users...");

        String sql = """
                INSERT INTO users 
                (username, email, password, role, created_at, updated_at) 
                VALUES (?, ?, ?, ?, NOW(), NOW())
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "admin");
            statement.setString(2, "admin@example.com");
            statement.setString(3, PasswordUtils.encrypt("adminpass"));
            statement.setString(4, "ADMIN");
            statement.executeUpdate();

            statement.setString(1, "customer");
            statement.setString(2, "customer@example.com");
            statement.setString(3, PasswordUtils.encrypt("customerpass"));
            statement.setString(4, "CUSTOMER");
            statement.executeUpdate();

            statement.setString(1, "seller");
            statement.setString(2, "seller@example.com");
            statement.setString(3, PasswordUtils.encrypt("sellerpass"));
            statement.setString(4, "SELLER");
            statement.executeUpdate();
        }

        System.out.println("User seeding completed.");
    }

    private static void seedCategories(Connection connection) throws SQLException {
        System.out.println("Seeding categories...");

        String sql = """
                INSERT INTO categories (name, created_at, updated_at) 
                VALUES (?, NOW(), NOW())
                """;

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, "Electronics");
            statement.executeUpdate();

            statement.setString(1, "Books");
            statement.executeUpdate();

            statement.setString(1, "Clothing");
            statement.executeUpdate();
        }

        System.out.println("Category seeding completed.");
    }

    private static void seedProducts(Connection connection) throws Exception {
        System.out.println("Seeding products...");

        String sellerEmail = "seller@example.com";
        String findSellerIdSql = "SELECT id FROM users WHERE email = ?";
        String insertProductSql = """
                INSERT INTO products 
                (name, description, price, stock, seller_id, category_id, image_url, created_at, updated_at) 
                VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
                """;

        // Cari seller berdasarkan email
        int sellerId;
        try (PreparedStatement findSellerIdStmt = connection.prepareStatement(findSellerIdSql)) {
            findSellerIdStmt.setString(1, sellerEmail);
            var rs = findSellerIdStmt.executeQuery();
            if (!rs.next()) {
                throw new Exception("Seller with email " + sellerEmail + " not found.");
            }
            sellerId = rs.getInt("id");
        }

        // Tambahkan produk
        try (PreparedStatement insertProductStmt = connection.prepareStatement(insertProductSql)) {
            addProduct(insertProductStmt, "Smartphone Samsung Galaxy S21",
                    "High-end smartphone with 128GB storage.",
                    new java.math.BigDecimal("799.99"), 50, sellerId, 1,
                    "/images/products/galaxy-s21.jpg");

            addProduct(insertProductStmt, "Apple MacBook Pro 14-inch",
                    "Powerful laptop with M1 Pro chip.",
                    new java.math.BigDecimal("1999.99"), 20, sellerId, 1,
                    "/images/products/macbook-pro.jpg");

            addProduct(insertProductStmt, "Sony WH-1000XM4 Headphones",
                    "Noise-canceling wireless headphones.",
                    new java.math.BigDecimal("349.99"), 100, sellerId, 1,
                    "/images/products/sony-headphones.jpg");

            addProduct(insertProductStmt, "Harry Potter Box Set",
                    "Complete 7-book series by J.K. Rowling.",
                    new java.math.BigDecimal("59.99"), 200, sellerId, 2,
                    "/images/products/harry-potter.jpg");

            addProduct(insertProductStmt, "Levi's 501 Original Jeans",
                    "Classic straight-fit jeans for men.",
                    new java.math.BigDecimal("89.99"), 150, sellerId, 3,
                    "/images/products/levis-jeans.jpg");

            addProduct(insertProductStmt, "Nike Air Max 270 Sneakers",
                    "Comfortable and stylish sneakers.",
                    new java.math.BigDecimal("129.99"), 75, sellerId, 3,
                    "/images/products/nike-air-max.jpg");

            addProduct(insertProductStmt, "Echo Dot (4th Gen)",
                    "Smart speaker with Alexa.",
                    new java.math.BigDecimal("49.99"), 120, sellerId, 1,
                    "/images/products/echo-dot.jpg");

            addProduct(insertProductStmt, "The Great Gatsby",
                    "Classic novel by F. Scott Fitzgerald.",
                    new java.math.BigDecimal("10.99"), 300, sellerId, 2,
                    "/images/products/great-gatsby.jpg");

            addProduct(insertProductStmt, "Adidas Ultraboost Running Shoes",
                    "High-performance running shoes.",
                    new java.math.BigDecimal("180.00"), 60, sellerId, 3,
                    "/images/products/adidas-ultraboost.jpg");

            addProduct(insertProductStmt, "Kindle Paperwhite",
                    "E-reader with adjustable light.",
                    new java.math.BigDecimal("129.99"), 40, sellerId, 1,
                    "/images/products/kindle-paperwhite.jpg");
        }

        System.out.println("Product seeding completed.");
    }


    private static void addProduct(PreparedStatement statement,
                                   String name,
                                   String description,
                                   java.math.BigDecimal price,
                                   int stock,
                                   int sellerId,
                                   int categoryId,
                                   String imageUrl) throws SQLException {
        statement.setString(1, name);
        statement.setString(2, description);
        statement.setBigDecimal(3, price);
        statement.setInt(4, stock);
        statement.setInt(5, sellerId);
        statement.setInt(6, categoryId);
        statement.setString(7, imageUrl);
        statement.executeUpdate();
    }
}
