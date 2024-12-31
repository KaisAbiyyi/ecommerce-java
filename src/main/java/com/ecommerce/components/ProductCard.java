package com.ecommerce.components;

import com.ecommerce.App;
import com.ecommerce.content.ProductDetailViewController;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Map;

public class ProductCard extends CardBase {
    private final int productId;

    public ProductCard(int productId, String productName, double productPrice, String imageUrl) {
        super(380, 400, "#f9f9f9", "dropshadow(gaussian, rgba(0, 0, 0, 0.15), 8, 0, 2, 2)");
        this.productId = productId;

        // Add ImageView with object-cover behavior
        ImageView imageView = createImageView(imageUrl);
        getChildren().add(imageView);

        // Add product name
        addTitle(productName);

        // Add product price
        Text priceText = new Text("Rp" + String.format("%,.0f", productPrice));
        priceText.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #4d5358;");
        getChildren().add(priceText);

        // Add click event
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
        imageView.setFitWidth(350); // Set fixed width
        imageView.setFitHeight(300); // Set fixed height
        imageView.setPreserveRatio(false); // Disable ratio preservation

        // Apply object-cover behavior using viewport
        double imageWidth = image.getWidth();
        double imageHeight = image.getHeight();
        double aspectRatioImage = imageWidth / imageHeight;
        double aspectRatioContainer = 350.0 / 250.0;

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
            // Pastikan MainLayoutController tersedia
            if (App.mainLayoutController != null) {
                // Tambahkan halaman ke stack navigasi
                NavbarController navbarController = App.mainLayoutController.getNavbarController();
                if (navbarController != null) {
                    // Tambahkan halaman dengan data tambahan (ID produk)
                    navbarController.addPageToStack(
                            "/com/ecommerce/content/ProductDetailView.fxml",
                            Map.of("productId", productId) // Data tambahan: ID produk
                    );
                }

                // Muat konten menggunakan MainLayoutController
                App.mainLayoutController.loadContent("/com/ecommerce/content/ProductDetailView.fxml");

                // Dapatkan controller dari konten yang dimuat
                ProductDetailViewController controller =
                        (ProductDetailViewController) App.mainLayoutController.getCurrentController();

                // Pastikan controller berhasil diambil dan set ID produk
                if (controller != null) {
                    controller.setProductId(productId); // Tetapkan ID produk
                    System.out.println("[INFO] Berhasil navigasi ke detail produk dengan ID: " + productId);
                } else {
                    System.err.println("[ERROR] Gagal mendapatkan controller ProductDetailViewController.");
                }
            } else {
                System.err.println("[ERROR] MainLayoutController tidak ditemukan. Pastikan layout utama telah diatur dengan benar.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kesalahan saat navigasi ke detail produk.");
            e.printStackTrace();
        }
    }



}
