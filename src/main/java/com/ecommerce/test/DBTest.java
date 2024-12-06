package com.ecommerce.test;

import com.ecommerce.dao.impl.*;
import com.ecommerce.models.*;
import com.ecommerce.models.User.Role;
import com.ecommerce.utils.DatabaseUtils;

import java.math.BigDecimal;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class DBTest {

    public static void main(String[] args) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            // DAO Instances
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);
            UserDAOImpl userDAO = new UserDAOImpl(connection);
            ProductDAOImpl productDAO = new ProductDAOImpl(connection);
            OrderDAOImpl orderDAO = new OrderDAOImpl(connection);
            OrderSellerDAOImpl orderSellerDAO = new OrderSellerDAOImpl(connection);
            OrderItemDAOImpl orderItemDAO = new OrderItemDAOImpl(connection);

            // 1. Insert Categories
            System.out.println("=== INSERT CATEGORIES ===");
            categoryDAO.addCategory(new Category(0, "Electronics", LocalDateTime.now(), LocalDateTime.now()));
            categoryDAO.addCategory(new Category(0, "Books", LocalDateTime.now(), LocalDateTime.now()));

            // Retrieve Categories
            Category electronics = categoryDAO.searchCategoryByName("Electronics").stream().findFirst().orElseThrow();
            Category books = categoryDAO.searchCategoryByName("Books").stream().findFirst().orElseThrow();
            System.out.println("Categories inserted successfully.");

            // 2. Insert Users
            System.out.println("\n=== INSERT USERS ===");
            userDAO.addUser(new User(0, "seller1", "seller1@example.com", "password123", Role.SELLER, LocalDateTime.now(), LocalDateTime.now()));
            userDAO.addUser(new User(0, "customer1", "customer1@example.com", "password123", Role.CUSTOMER, LocalDateTime.now(), LocalDateTime.now()));

            // Retrieve Users
            User seller = userDAO.getAllUsers().stream().filter(u -> u.getRole() == Role.SELLER).findFirst().orElseThrow();
            User customer = userDAO.getAllUsers().stream().filter(u -> u.getRole() == Role.CUSTOMER).findFirst().orElseThrow();
            System.out.println("Users inserted successfully.");

            // 3. Insert Products
            System.out.println("\n=== INSERT PRODUCTS ===");
            productDAO.addProduct(new Product(
                    0, "Smartphone", "High-end smartphone", 999.99, 50,
                    seller.getId(), electronics.getId(), "phone.jpg",
                    LocalDateTime.now(), LocalDateTime.now()
            ));

            productDAO.addProduct(new Product(
                    0, "Laptop", "Gaming laptop", 1599.99, 30,
                    seller.getId(), electronics.getId(), "laptop.jpg",
                    LocalDateTime.now(), LocalDateTime.now()
            ));

            productDAO.addProduct(new Product(
                    0, "Novel", "Bestselling novel", 19.99, 200,
                    seller.getId(), books.getId(), "novel.jpg",
                    LocalDateTime.now(), LocalDateTime.now()
            ));
            System.out.println("Products inserted successfully.");

            // Retrieve Products
            List<Product> products = productDAO.getAllProducts();
            products.forEach(System.out::println);

            // 4. Create Order
            System.out.println("\n=== CREATE ORDER ===");
            Order order = new Order(
                    0, // ID
                    customer.getId(), // ID pengguna
                    new BigDecimal("2599.97"), // Total harga
                    Order.Status.PENDING, // Status pesanan
                    LocalDateTime.now(), // Waktu pembuatan
                    LocalDateTime.now() // Waktu pembaruan
            );

            orderDAO.addOrder(order);

            // Retrieve Order
            order = orderDAO.getAllOrders().stream().findFirst().orElseThrow();
            System.out.println("Order created successfully.");

            // 5. Create Order Sellers
            System.out.println("\n=== CREATE ORDER SELLERS ===");
            OrderSeller orderSeller = new OrderSeller(
                    0, order.getId(), seller.getId(), OrderSeller.Status.PENDING,
                    LocalDateTime.now(), LocalDateTime.now()
            );
            orderSellerDAO.addOrderSeller(orderSeller);

            // Retrieve Order Sellers
            List<OrderSeller> orderSellers = orderSellerDAO.getOrderSellersByOrderId(order.getId());
            orderSellers.forEach(System.out::println);

            // 6. Add Order Items
            System.out.println("\n=== ADD ORDER ITEMS ===");
            Product phone = products.stream().filter(p -> p.getName().equals("Smartphone")).findFirst().orElseThrow();
            Product laptop = products.stream().filter(p -> p.getName().equals("Laptop")).findFirst().orElseThrow();

            orderItemDAO.addOrderItem(new OrderItem(
                    0, orderSeller.getId(), phone.getId(), 2, phone.getPrice() * 2,
                    LocalDateTime.now(), LocalDateTime.now()
            ));

            orderItemDAO.addOrderItem(new OrderItem(
                    0, orderSeller.getId(), laptop.getId(), 1, laptop.getPrice(),
                    LocalDateTime.now(), LocalDateTime.now()
            ));
            System.out.println("Order items added successfully.");

            // Retrieve Order Items
            List<OrderItem> orderItems = orderItemDAO.getOrderItemsByOrderSellerId(orderSeller.getId());
            orderItems.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during database operation: " + e.getMessage());
        }
    }
}
