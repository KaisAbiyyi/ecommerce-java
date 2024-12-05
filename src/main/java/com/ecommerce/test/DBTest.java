package com.ecommerce.test;

import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.dao.impl.CategoryDAOImpl;
import com.ecommerce.dao.impl.ProductDAOImpl;
import com.ecommerce.dao.impl.UserDAOImpl;
import com.ecommerce.models.Category;
import com.ecommerce.models.Product;
import com.ecommerce.models.User;
import com.ecommerce.models.User.Role;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class DBTest {

    public static void main(String[] args) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            // DAO instances
            CategoryDAOImpl categoryDAO = new CategoryDAOImpl(connection);
            ProductDAOImpl productDAO = new ProductDAOImpl(connection);
            UserDAOImpl userDAO = new UserDAOImpl(connection);

            // 1. Insert Categories
            System.out.println("=== INSERT CATEGORIES ===");
            Category category1 = new Category(0, "Electronics", LocalDateTime.now(), LocalDateTime.now());
            Category category2 = new Category(0, "Books", LocalDateTime.now(), LocalDateTime.now());
            Category category3 = new Category(0, "Clothing", LocalDateTime.now(), LocalDateTime.now());

            categoryDAO.addCategory(category1);
            categoryDAO.addCategory(category2);
            categoryDAO.addCategory(category3);
            System.out.println("Categories inserted successfully.");

            // 2. Retrieve All Categories
            System.out.println("\n=== RETRIEVE ALL CATEGORIES ===");
            List<Category> categories = categoryDAO.getAllCategories();
            categories.forEach(System.out::println);

            // 3. Create a User as a Seller
            System.out.println("\n=== CREATE SELLER USER ===");
            User seller = new User(0, "selleruser", "seller@example.com", "sellerpass123", Role.SELLER, LocalDateTime.now(), LocalDateTime.now());
            userDAO.addUser(seller);
            System.out.println("Seller user created successfully.");

            // Retrieve the newly created seller to get the ID
            seller = userDAO.getAllUsers().stream()
                    .filter(user -> user.getUsername().equals("selleruser"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Seller user not found"));

            // 4. Insert Products
            System.out.println("\n=== INSERT PRODUCTS ===");
            Product product1 = new Product(0, "Smartphone", "Latest model smartphone", 699.99, 100, seller.getId(), categories.get(0).getId(), "smartphone.jpg", LocalDateTime.now(), LocalDateTime.now());
            Product product2 = new Product(0, "Laptop", "High performance laptop", 1299.99, 50, seller.getId(), categories.get(0).getId(), "laptop.jpg", LocalDateTime.now(), LocalDateTime.now());
            Product product3 = new Product(0, "Novel", "Bestselling novel", 19.99, 200, seller.getId(), categories.get(1).getId(), "novel.jpg", LocalDateTime.now(), LocalDateTime.now());
            Product product4 = new Product(0, "T-Shirt", "Comfortable cotton t-shirt", 9.99, 300, seller.getId(), categories.get(2).getId(), "tshirt.jpg", LocalDateTime.now(), LocalDateTime.now());
            Product product5 = new Product(0, "Jeans", "Stylish denim jeans", 49.99, 150, seller.getId(), categories.get(2).getId(), "jeans.jpg", LocalDateTime.now(), LocalDateTime.now());

            productDAO.addProduct(product1);
            productDAO.addProduct(product2);
            productDAO.addProduct(product3);
            productDAO.addProduct(product4);
            productDAO.addProduct(product5);
            System.out.println("Products inserted successfully.");

            // 5. Retrieve All Products
            System.out.println("\n=== RETRIEVE ALL PRODUCTS ===");
            List<Product> products = productDAO.getAllProducts();
            products.forEach(System.out::println);

            // 6. Update a Product
            System.out.println("\n=== UPDATE PRODUCT ===");
            Product existingProduct = products.get(0); // Assuming we take the first product
            existingProduct.setPrice(749.99); // Update price
            productDAO.updateProduct(existingProduct);
            System.out.println("Product updated successfully.");

            // 7. Delete a Product
            System.out.println("\n=== DELETE PRODUCT ===");
            productDAO.deleteProduct(products.get(4).getId()); // Delete the fifth product
            System.out.println("Product deleted successfully.");

            // 8. Retrieve Products by Category
            System.out.println("\n=== RETRIEVE PRODUCTS BY CATEGORY ===");
            List<Product> electronicsProducts = productDAO.getProductsByCategory(categories.get(0).getId());
            electronicsProducts.forEach(System.out::println);

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during database operation: " + e.getMessage());
        }
    }
}
