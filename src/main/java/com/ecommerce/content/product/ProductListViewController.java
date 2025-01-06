package com.ecommerce.content.product;

import com.ecommerce.components.ProductCard;
import com.ecommerce.db.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductListViewController {

    @FXML
    private GridPane gridPane;

    @FXML
    private HBox categoryContainer;
    @FXML
    private HBox sellerContainer;

    @FXML
    private TextField searchField;

    @FXML
    private Button searchButton;

    public void initialize() {
        // Load categories and products when the view is initialized
        loadCategories();
        loadProducts();
        loadSellers();


        // Add search button action
        searchButton.setOnAction(event -> searchProducts());
    }

    private void loadSellers() {
        System.out.println("Loading sellers...");
        try (Connection connection = DatabaseUtils.getConnection()) {
            // Add "All Sellers" button to show all products
            Button allSellersButton = new Button("All Sellers");
            allSellersButton.setStyle("-fx-background-color: #fff; -fx-border-color: #F2F4F8; " +
                    "-fx-border-width: 2; -fx-background-radius: 100; -fx-border-radius: 100; " +
                    "-fx-text-fill: #4d5358;");
            allSellersButton.setOnAction(event -> loadProducts()); // Load all products when clicked
            sellerContainer.getChildren().add(allSellersButton);

            // Query sellers from database
            String query = "SELECT id, username FROM users WHERE role = 'SELLER'";

            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int sellerId = rs.getInt("id");
                    String sellerName = rs.getString("username");

                    System.out.println("Seller found: " + sellerName);

                    // Create a button for each seller
                    Button sellerButton = new Button(sellerName);
                    sellerButton.setStyle("-fx-background-color: #fff; -fx-border-color: #F2F4F8; " +
                            "-fx-border-width: 2; -fx-background-radius: 100; -fx-border-radius: 100; " +
                            "-fx-text-fill: #4d5358;");
                    sellerButton.setOnAction(event -> filterProductsBySeller(sellerId));

                    sellerContainer.getChildren().add(sellerButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void filterProductsBySeller(int sellerId) {
        System.out.println("Filtering products by seller ID: " + sellerId);
        gridPane.getChildren().clear(); // Clear existing products

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
                SELECT id, name, price, image_url 
                FROM products
                WHERE seller_id = ?
                """;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, sellerId);
                ResultSet rs = statement.executeQuery();
                int row = 0;
                int column = 0;

                while (rs.next()) {
                    int productId = rs.getInt("id");
                    String productName = rs.getString("name");
                    double productPrice = rs.getDouble("price");
                    String imageUrl = rs.getString("image_url");

                    ProductCard productCard = new ProductCard(productId, productName, productPrice, imageUrl);
                    gridPane.add(productCard, column++, row);

                    if (column == 3) {
                        column = 0;
                        row++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void loadCategories() {
        System.out.println("Loading categories...");
        try (Connection connection = DatabaseUtils.getConnection()) {
            // Add "All" button to show all products
            Button allButton = new Button("All");
            allButton.setStyle("-fx-background-color: #fff; -fx-border-color: #F2F4F8; " +
                    "-fx-border-width: 2; -fx-background-radius: 100; -fx-border-radius: 100; " +
                    "-fx-text-fill: #4d5358;");
            allButton.setOnAction(event -> loadProducts()); // Load all products when clicked
            categoryContainer.getChildren().add(allButton);

            // Query categories from database
            String query = "SELECT id, name FROM categories";

            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    int categoryId = rs.getInt("id");
                    String categoryName = rs.getString("name");

                    System.out.println("Category found: " + categoryName);

                    // Create a button for each category
                    Button categoryButton = new Button(categoryName);
                    categoryButton.setStyle("-fx-background-color: #fff; -fx-border-color: #F2F4F8; " +
                            "-fx-border-width: 2; -fx-background-radius: 100; -fx-border-radius: 100; " +
                            "-fx-text-fill: #4d5358;");
                    categoryButton.setOnAction(event -> filterProductsByCategory(categoryId));

                    categoryContainer.getChildren().add(categoryButton);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadProducts() {
        System.out.println("Loading all products...");
        gridPane.getChildren().clear();

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT id, name, price, image_url FROM products";

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet rs = statement.executeQuery();
                int row = 0;
                int column = 0;

                while (rs.next()) {
                    int productId = rs.getInt("id");
                    String productName = rs.getString("name");
                    double productPrice = rs.getDouble("price");
                    String imageUrl = rs.getString("image_url");

                    ProductCard productCard = new ProductCard(productId, productName, productPrice, imageUrl);
                    gridPane.add(productCard, column++, row);

                    if (column == 3) { // Move to the next row after 3 columns
                        column = 0;
                        row++;
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load products:");
            e.printStackTrace();
        }
    }

    private void searchProducts() {
        String keyword = searchField.getText().trim().toLowerCase();
        System.out.println("Searching for: " + keyword);
        gridPane.getChildren().clear(); // Clear the current product grid

        if (keyword.isEmpty()) {
            loadProducts(); // Load all products if no keyword
            return;
        }

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
                    SELECT id, name, price, image_url 
                    FROM products
                    WHERE LOWER(name) LIKE ?
                    """;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setString(1, "%" + keyword + "%");
                ResultSet rs = statement.executeQuery();

                int row = 0;
                int column = 0;
                boolean productFound = false; // Tambahkan indikator

                while (rs.next()) {
                    productFound = true; // Jika produk ditemukan, ubah ke true
                    int productId = rs.getInt("id");
                    String productName = rs.getString("name");
                    double productPrice = rs.getDouble("price");
                    String imageUrl = rs.getString("image_url");

                    ProductCard productCard = new ProductCard(productId, productName, productPrice, imageUrl);
                    gridPane.add(productCard, column++, row);

                    if (column == 3) { // Move to the next row after 3 columns
                        column = 0;
                        row++;
                    }
                }

                if (!productFound) {
                    System.out.println("[INFO] No products found for keyword: " + keyword);
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to search products:");
            e.printStackTrace();
        }
    }


    private void filterProductsByCategory(int categoryId) {
        System.out.println("Filtering products by category ID: " + categoryId);
        gridPane.getChildren().clear(); // Clear existing products

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
                    SELECT id, name, price, image_url 
                    FROM products
                    WHERE category_id = ?
                    """;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, categoryId);
                ResultSet rs = statement.executeQuery();
                int row = 0;
                int column = 0;

                while (rs.next()) {
                    int productId = rs.getInt("id");
                    String productName = rs.getString("name");
                    double productPrice = rs.getDouble("price");
                    String imageUrl = rs.getString("image_url");

                    ProductCard productCard = new ProductCard(productId, productName, productPrice, imageUrl);
                    gridPane.add(productCard, column++, row);

                    if (column == 3) {
                        column = 0;
                        row++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
