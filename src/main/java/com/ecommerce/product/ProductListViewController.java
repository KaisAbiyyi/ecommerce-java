package com.ecommerce.product;

import com.ecommerce.components.ProductCard;
import com.ecommerce.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.layout.GridPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ProductListViewController {

    @FXML
    private GridPane gridPane;

    public void initialize() {
        // Load product data when the view is initialized
        loadProducts();
    }

    private void loadProducts() {
        System.out.println("Loading products...");
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
                    SELECT id, name, price, image_url 
                    FROM products
                    LIMIT 6
                    """;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                ResultSet rs = statement.executeQuery();
                int row = 0;
                int column = 0;

                while (rs.next()) {
                    int productId = rs.getInt("id");
                    String productName = rs.getString("name");
                    double productPrice = rs.getDouble("price");
                    String imageUrl = rs.getString("image_url");

                    System.out.println("Product found: " + productName + " - Rp" + productPrice);

                    // Pass the correct parameters to ProductCard constructor
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
