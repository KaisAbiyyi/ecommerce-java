package com.ecommerce.components;

import com.ecommerce.App;
import com.ecommerce.content.ProductDetailViewController;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Map;

public class ProductCard extends VBox {
    private final int productId;

    public ProductCard(int productId, String productName, double productPrice, String imageUrl) {
        super(10); // Set spacing between child elements
        this.productId = productId;

        // Set fixed size for the card
        this.setPrefWidth(200);
        this.setPrefHeight(300);
        this.setPadding(new Insets(10));
        this.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; " +
                "-fx-background-color: #FFFFFF; -fx-background-radius: 10; -fx-border-radius: 10;");
        this.setAlignment(Pos.CENTER);

        // Add product image
        ImageView productImageView = createImageView(imageUrl);
        this.getChildren().add(productImageView);

        // Add product name
        Text productNameText = new Text(productName);
        productNameText.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        productNameText.setWrappingWidth(180); // Allow wrapping within the card width
        this.getChildren().add(productNameText);

        // Add product price
        Text productPriceText = new Text("Rp" + String.format("%,.0f", productPrice));
        productPriceText.setStyle("-fx-font-size: 14px; -fx-text-fill: #666;");
        this.getChildren().add(productPriceText);

        // Add click event for navigation
        this.setOnMouseClicked(this::handleClick);
    }

    private ImageView createImageView(String imageUrl) {
        Image image;
        try {
            image = new Image(getClass().getResource(imageUrl).toExternalForm());
        } catch (NullPointerException | IllegalArgumentException e) {
            System.err.println("Image not found: " + imageUrl + ". Using default image.");
            image = new Image(getClass().getResource("/images/products/default.jpg").toExternalForm());
        }

        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(180); // Fixed width for the image
        imageView.setFitHeight(180); // Fixed height for the image
        imageView.setPreserveRatio(false); // Disable preserve ratio for object-cover behavior

        // Apply object-cover behavior
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double aspectRatioImage = imageWidth / imageHeight;
        double aspectRatioContainer = 180.0 / 180.0;

        if (aspectRatioImage > aspectRatioContainer) {
            double newWidth = imageHeight * aspectRatioContainer;
            double xOffset = (imageWidth - newWidth) / 2;
            imageView.setViewport(new Rectangle2D(xOffset, 0, newWidth, imageHeight));
        } else {
            double newHeight = imageWidth / aspectRatioContainer;
            double yOffset = (imageHeight - newHeight) / 2;
            imageView.setViewport(new Rectangle2D(0, yOffset, imageWidth, newHeight));
        }

        return imageView;
    }

    private void handleClick(MouseEvent event) {
        System.out.println("Product clicked: " + productId);
        navigateToProductDetail();
    }

    private void navigateToProductDetail() {
        try {
            if (App.mainLayoutController != null) {
                // Add page to the navigation stack
                NavbarController navbarController = App.mainLayoutController.getNavbarController();
                if (navbarController != null) {
                    navbarController.addPageToStack(
                            "/com/ecommerce/content/ProductDetailView.fxml",
                            Map.of("productId", productId)
                    );
                }

                // Load product detail view
                App.mainLayoutController.loadContent("/com/ecommerce/content/ProductDetailView.fxml");

                // Pass product ID to the ProductDetailViewController
                ProductDetailViewController controller =
                        (ProductDetailViewController) App.mainLayoutController.getCurrentController();
                if (controller != null) {
                    controller.setProductId(productId);
                    System.out.println("[INFO] Navigated to product detail view for product ID: " + productId);
                } else {
                    System.err.println("[ERROR] Failed to get ProductDetailViewController.");
                }
            } else {
                System.err.println("[ERROR] MainLayoutController is not set.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] An error occurred while navigating to product detail:");
            e.printStackTrace();
        }
    }
}
