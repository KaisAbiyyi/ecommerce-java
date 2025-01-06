package com.ecommerce.db.seeder;

import com.ecommerce.db.DatabaseUtils;
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
            seedOrders(connection);
            seedCartData(connection);

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
            // Electronics
            statement.setString(1, "Electronics");
            statement.executeUpdate();

            // Fashion
            statement.setString(1, "Clothing");
            statement.executeUpdate();
            statement.setString(1, "Footwear");
            statement.executeUpdate();
            statement.setString(1, "Accessories");
            statement.executeUpdate();

            // Books & Stationery
            statement.setString(1, "Books");
            statement.executeUpdate();
            statement.setString(1, "Stationery");
            statement.executeUpdate();

            // Home & Furniture
            statement.setString(1, "Furniture");
            statement.executeUpdate();
            statement.setString(1, "Home Decor");
            statement.executeUpdate();
            statement.setString(1, "Kitchenware");
            statement.executeUpdate();

            // Health & Beauty
            statement.setString(1, "Health & Wellness");
            statement.executeUpdate();
            statement.setString(1, "Beauty Products");
            statement.executeUpdate();

            // Sports & Outdoors
            statement.setString(1, "Sports Equipment");
            statement.executeUpdate();
            statement.setString(1, "Outdoor Gear");
            statement.executeUpdate();

            // Groceries
            statement.setString(1, "Food & Beverages");
            statement.executeUpdate();
            statement.setString(1, "Snacks");
            statement.executeUpdate();

            // Toys & Hobbies
            statement.setString(1, "Toys");
            statement.executeUpdate();
            statement.setString(1, "Hobbies");
            statement.executeUpdate();

            // Automotive
            statement.setString(1, "Automotive Accessories");
            statement.executeUpdate();

            // Others
            statement.setString(1, "Jewelry");
            statement.executeUpdate();
            statement.setString(1, "Pet Supplies");
            statement.executeUpdate();
            statement.setString(1, "Musical Instruments");
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
                    new java.math.BigDecimal("11000000"), 50, sellerId, 1, // Electronics
                    "/images/products/galaxy-s21.jpg");

            addProduct(insertProductStmt, "Apple MacBook Pro 14-inch",
                    "Powerful laptop with M1 Pro chip.",
                    new java.math.BigDecimal("30000000"), 20, sellerId, 1, // Electronics
                    "/images/products/macbook-pro.jpg");

            addProduct(insertProductStmt, "Sony WH-1000XM4 Headphones",
                    "Noise-canceling wireless headphones.",
                    new java.math.BigDecimal("4500000"), 100, sellerId, 1, // Electronics
                    "/images/products/sony-headphones.jpg");

            addProduct(insertProductStmt, "Harry Potter Box Set",
                    "Complete 7-book series by J.K. Rowling.",
                    new java.math.BigDecimal("700000"), 200, sellerId, 5, // Books
                    "/images/products/harry-potter.jpg");

            addProduct(insertProductStmt, "Levi's 501 Original Jeans",
                    "Classic straight-fit jeans for men.",
                    new java.math.BigDecimal("800000"), 150, sellerId, 2, // Clothing
                    "/images/products/levis-jeans.jpg");

            addProduct(insertProductStmt, "Nike Air Max 270 Sneakers",
                    "Comfortable and stylish sneakers.",
                    new java.math.BigDecimal("1500000"), 75, sellerId, 3, // Footwear
                    "/images/products/nike-air-max.jpg");

            addProduct(insertProductStmt, "Echo Dot (4th Gen)",
                    "Smart speaker with Alexa.",
                    new java.math.BigDecimal("700000"), 120, sellerId, 1, // Electronics
                    "/images/products/echo-dot.jpg");

            addProduct(insertProductStmt, "The Great Gatsby",
                    "Classic novel by F. Scott Fitzgerald.",
                    new java.math.BigDecimal("120000"), 300, sellerId, 5, // Books
                    "/images/products/great-gatsby.jpg");

            addProduct(insertProductStmt, "Adidas Ultraboost Running Shoes",
                    "High-performance running shoes.",
                    new java.math.BigDecimal("2200000"), 60, sellerId, 3, // Footwear
                    "/images/products/adidas-ultraboost.jpg");

            addProduct(insertProductStmt, "Kindle Paperwhite",
                    "E-reader with adjustable light.",
                    new java.math.BigDecimal("1800000"), 40, sellerId, 1, // Electronics
                    "/images/products/kindle-paperwhite.jpg");
        }

        System.out.println("Product seeding completed.");
    }


    private static void seedOrders(Connection connection) throws SQLException {
        System.out.println("Seeding orders...");

        String insertOrderSql = """
                INSERT INTO orders (user_id, total_price, status, created_at, updated_at)
                VALUES (?, ?, ?, NOW(), NOW())
                """;

        String insertOrderSellerSql = """
                INSERT INTO order_sellers (order_id, seller_id, status, created_at, updated_at)
                VALUES (?, ?, ?, NOW(), NOW())
                """;

        String insertOrderItemSql = """
                INSERT INTO order_items (order_seller_id, product_id, quantity, price, created_at, updated_at)
                VALUES (?, ?, ?, ?, NOW(), NOW())
                """;

        try (
                PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderSql, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement insertOrderSellerStmt = connection.prepareStatement(insertOrderSellerSql, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement insertOrderItemStmt = connection.prepareStatement(insertOrderItemSql)
        ) {
            // Create order
            insertOrderStmt.setInt(1, 2); // User ID (Assumes customer with ID 2 exists)
            insertOrderStmt.setBigDecimal(2, new java.math.BigDecimal("259.98")); // Total price
            insertOrderStmt.setString(3, "PENDING"); // Order status
            insertOrderStmt.executeUpdate();

            // Get generated order ID
            int orderId;
            try (var rs = insertOrderStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated order ID.");
                }
            }

            // Create order_seller
            insertOrderSellerStmt.setInt(1, orderId); // Order ID
            insertOrderSellerStmt.setInt(2, 3); // Seller ID (Assumes seller with ID 3 exists)
            insertOrderSellerStmt.setString(3, "PENDING"); // Order seller status
            insertOrderSellerStmt.executeUpdate();

            // Get generated order_seller ID
            int orderSellerId;
            try (var rs = insertOrderSellerStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    orderSellerId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated order_seller ID.");
                }
            }

            // Create order_items
            insertOrderItemStmt.setInt(1, orderSellerId); // Order seller ID
            insertOrderItemStmt.setInt(2, 1); // Product ID (Assumes product with ID 1 exists)
            insertOrderItemStmt.setInt(3, 2); // Quantity
            insertOrderItemStmt.setBigDecimal(4, new java.math.BigDecimal("129.99")); // Price per item
            insertOrderItemStmt.executeUpdate();

            insertOrderItemStmt.setInt(1, orderSellerId); // Order seller ID
            insertOrderItemStmt.setInt(2, 2); // Product ID (Assumes product with ID 2 exists)
            insertOrderItemStmt.setInt(3, 1); // Quantity
            insertOrderItemStmt.setBigDecimal(4, new java.math.BigDecimal("199.99")); // Price per item
            insertOrderItemStmt.executeUpdate();
        }

        System.out.println("Order seeding completed.");
    }

    private static void seedCartData(Connection connection) throws SQLException {
        System.out.println("Seeding cart data...");

        String insertCartSellerSql = """
            INSERT INTO cart_sellers (user_id, seller_id, created_at, updated_at)
            VALUES (?, ?, NOW(), NOW())
            """;

        String insertCartItemSql = """
            INSERT INTO cart_items (cart_seller_id, product_id, quantity, created_at, updated_at)
            VALUES (?, ?, ?, NOW(), NOW())
            """;

        try (
                PreparedStatement insertCartSellerStmt = connection.prepareStatement(insertCartSellerSql, PreparedStatement.RETURN_GENERATED_KEYS);
                PreparedStatement insertCartItemStmt = connection.prepareStatement(insertCartItemSql)
        ) {
            // Create cart_seller
            insertCartSellerStmt.setInt(1, 2); // User ID (Assumes customer with ID 2 exists)
            insertCartSellerStmt.setInt(2, 3); // Seller ID (Assumes seller with ID 3 exists)
            insertCartSellerStmt.executeUpdate();

            // Get generated cart_seller ID
            int cartSellerId;
            try (var rs = insertCartSellerStmt.getGeneratedKeys()) {
                if (rs.next()) {
                    cartSellerId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve generated cart_seller ID.");
                }
            }

            // Create cart_items
            insertCartItemStmt.setInt(1, cartSellerId); // Cart seller ID
            insertCartItemStmt.setInt(2, 1); // Product ID (Assumes product with ID 1 exists)
            insertCartItemStmt.setInt(3, 2); // Quantity
            insertCartItemStmt.executeUpdate();

            insertCartItemStmt.setInt(1, cartSellerId); // Cart seller ID
            insertCartItemStmt.setInt(2, 2); // Product ID (Assumes product with ID 2 exists)
            insertCartItemStmt.setInt(3, 1); // Quantity
            insertCartItemStmt.executeUpdate();
        }

        System.out.println("Cart data seeding completed.");
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
