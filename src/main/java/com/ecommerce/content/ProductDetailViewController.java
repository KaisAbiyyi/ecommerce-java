package com.ecommerce.content;

import com.ecommerce.layouts.MainLayoutController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import com.ecommerce.utils.DatabaseUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Controller untuk menampilkan detail produk.
 */
public class ProductDetailViewController {

    @FXML
    private ImageView productImageView;

    @FXML
    private Text productNameLabel;

    @FXML
    private Text productPriceLabel;

    @FXML
    private Text productDescriptionLabel;

    @FXML
    private TextField quantityField;

    @FXML
    private Text productStockLabel;

    @FXML
    private Button buyNowButton;

    @FXML
    private Button addToCartButton;

    private MainLayoutController mainLayoutController;

    private int productId;

    /**
     * Inisialisasi controller.
     */
    @FXML
    public void initialize() {
        System.out.println("[INFO] ProductDetailViewController diinisialisasi.");
    }

    /**
     * Tetapkan referensi ke MainLayoutController.
     */
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        System.out.println("[INFO] MainLayoutController berhasil diatur di ProductDetailViewController.");
    }

    /**
     * Tetapkan ID produk yang akan ditampilkan.
     */
    public void setProductId(int productId) {
        this.productId = productId;
        System.out.println("[DEBUG] setProductId dipanggil dengan ID: " + productId);
        loadProductDetails();
        System.out.println("[DEBUG] loadProductDetails selesai untuk ID: " + productId);
    }

    /**
     * Muat detail produk dari basis data lalu perbarui tampilan.
     */
    private void loadProductDetails() {
        System.out.println("[INFO] Memuat detail produk dengan ID: " + productId);
        Product product = fetchProductDetails(productId);

        if (product != null) {
            refreshUI(product);
            System.out.println("[DEBUG] refreshUI dipanggil dan selesai.");
        } else {
            showProductNotFound();
        }
    }

    /**
     * Perbarui elemen UI dengan data produk.
     */
    private void refreshUI(Product product) {
        System.out.println("[DEBUG] Memperbarui UI dengan produk:");
        System.out.println("Name: " + product.getName());
        System.out.println("Price: " + product.getPrice());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Stock: " + product.getStock());
        System.out.println("Image URL: " + product.getImageUrl());

        productNameLabel.setText(product.getName());
        productPriceLabel.setText("Rp" + String.format("%,.0f", product.getPrice()));
        productDescriptionLabel.setText(product.getDescription());
        productStockLabel.setText(String.valueOf(product.getStock()));

        try {
            Image productImage = new Image(getClass().getResource(product.getImageUrl()).toExternalForm());
            productImageView.setImage(productImage);
            System.out.println("[INFO] Gambar produk berhasil dimuat.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat gambar produk, memakai gambar default.");
            productImageView.setImage(
                    new Image(getClass().getResource("/images/products/default.jpg").toExternalForm())
            );
        }
    }

    /**
     * Tampilkan pesan Product Not Found di UI.
     */
    private void showProductNotFound() {
        System.out.println("[WARN] Produk tidak ditemukan, menampilkan default.");
        productNameLabel.setText("Product Not Found");
        productPriceLabel.setText("N/A");
        productDescriptionLabel.setText("No details available.");
        productStockLabel.setText("0");
        productImageView.setImage(
                new Image(getClass().getResource("/images/products/default.jpg").toExternalForm())
        );
    }

    /**
     * Ambil detail produk dari basis data.
     */
    private Product fetchProductDetails(int productId) {
        System.out.println("[INFO] Mengambil detail produk ID: " + productId);

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
                SELECT id, name, price, image_url, description, stock
                FROM products
                WHERE id = ?
                """;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, productId);
                ResultSet rs = statement.executeQuery();

                if (rs.next()) {
                    System.out.println("[DEBUG] Produk ditemukan di database:");
                    System.out.println("ID: " + rs.getInt("id"));
                    System.out.println("Name: " + rs.getString("name"));
                    System.out.println("Price: " + rs.getDouble("price"));
                    System.out.println("Image URL: " + rs.getString("image_url"));
                    System.out.println("Description: " + rs.getString("description"));
                    System.out.println("Stock: " + rs.getInt("stock"));

                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("price"),
                            rs.getString("image_url"),
                            rs.getString("description"),
                            rs.getInt("stock")
                    );
                } else {
                    System.err.println("[WARN] Produk dengan ID " + productId + " tidak ditemukan di database.");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kesalahan saat mengambil detail produk:");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Tangani klik tombol Buy Now.
     */
    @FXML
    private void handleBuyNow() {
        System.out.println("[INFO] Buy Now ditekan untuk produk ID: " + productId);
    }

    /**
     * Tangani klik tombol Add to Cart.
     */
    @FXML
    private void handleAddToCart() {
        System.out.println("[INFO] Add to Cart ditekan untuk produk ID: " + productId);
        String quantity = quantityField.getText();
        System.out.println("[INFO] Jumlah yang ditambahkan: " + quantity);
    }

    /**
     * Kelas representasi produk.
     */
    static class Product {
        private final int id;
        private final String name;
        private final double price;
        private final String imageUrl;
        private final String description;
        private final int stock;

        public Product(int id, String name, double price, String imageUrl, String description, int stock) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.imageUrl = imageUrl;
            this.description = description;
            this.stock = stock;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getDescription() {
            return description;
        }

        public int getStock() {
            return stock;
        }
    }
}
