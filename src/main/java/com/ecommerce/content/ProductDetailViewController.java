package com.ecommerce.content;

import com.ecommerce.App;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import com.ecommerce.utils.DatabaseUtils;
import javafx.scene.control.Alert;

import java.sql.*;
import java.util.Map;

/**
 * Controller untuk menampilkan detail produk.
 */
public class ProductDetailViewController implements MainLayoutController.MainLayoutAware {

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
    private double productPrice;

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

        // Perbarui variabel productPrice
        productPrice = product.getPrice();

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
     * Tangani klik tombol Add to Cart.
     */
    @FXML
    private void handleAddToCart() {
        System.out.println("[INFO] Add to Cart ditekan untuk produk ID: " + productId);

        if (App.loggedInUser == null) {
            System.err.println("[ERROR] Tidak ada pengguna yang sedang login. Tidak dapat melanjutkan.");
            return;
        }

        int userId = App.loggedInUser.getId();

        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false); // Mulai transaksi

            int sellerId = getSellerIdByProductId(connection, productId);

            // Ambil jumlah stok yang tersedia
            int availableStock = getAvailableStock(connection, productId);
            int quantity = Integer.parseInt(quantityField.getText());

            if (quantity > availableStock) {
                System.err.println("[ERROR] Jumlah stok yang diminta melebihi stok yang tersedia. Stok tersedia: " + availableStock);

                // Tampilkan alert jika jumlah stok melebihi
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Peringatan Stok");
                alert.setHeaderText("Stok Tidak Mencukupi");
                alert.setContentText("Jumlah stok yang diminta melebihi stok yang tersedia. Stok tersedia: " + availableStock);
                alert.showAndWait();
                return;
            }

            // Periksa apakah cart_seller untuk kombinasi user dan seller sudah ada
            int cartSellerId = getOrCreateCartSeller(connection, userId, sellerId);

            // Tambahkan atau perbarui item ke cart_items
            addOrUpdateCartItem(connection, cartSellerId, productId, quantity);

            connection.commit(); // Selesaikan transaksi

            // Navigasi ke halaman CartView
            String cartViewPath = "/com/ecommerce/content/customer/CartView.fxml";
            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(cartViewPath, Map.of(
                        "productName", productNameLabel.getText(),
                        "productPrice", String.valueOf(productPrice),
                        "quantity", String.valueOf(quantity)
                ));
            }

            App.mainLayoutController.loadContent(cartViewPath);
            System.out.println("[INFO] Navigasi ke halaman CartView berhasil.");
        } catch (NumberFormatException e) {
            System.err.println("[ERROR] Input jumlah tidak valid.");

            // Tampilkan alert jika input tidak valid
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Kesalahan Input");
            alert.setHeaderText("Jumlah Tidak Valid");
            alert.setContentText("Harap masukkan jumlah barang yang valid.");
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal melakukan proses input ke tabel atau navigasi ke CartView:");
            e.printStackTrace();
        }
    }



    private int getSellerIdByProductId(Connection connection, int productId) throws SQLException {
        String query = "SELECT seller_id FROM products WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("seller_id");
                } else {
                    throw new SQLException("Produk dengan ID " + productId + " tidak ditemukan.");
                }
            }
        }
    }


    private int getAvailableStock(Connection connection, int productId) throws Exception {
        String query = "SELECT stock FROM products WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, productId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("stock");
                } else {
                    throw new Exception("[ERROR] Produk dengan ID " + productId + " tidak ditemukan.");
                }
            }
        }
    }


    private int getOrCreateCartSeller(Connection connection, int userId, int sellerId) throws SQLException {
        String selectQuery = "SELECT id FROM cart_sellers WHERE user_id = ? AND seller_id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, userId);
            selectStmt.setInt(2, sellerId);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("id");
                }
            }
        }

        String insertQuery = "INSERT INTO cart_sellers (user_id, seller_id) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            insertStmt.setInt(1, userId);
            insertStmt.setInt(2, sellerId);
            insertStmt.executeUpdate();
            try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                } else {
                    throw new SQLException("Gagal membuat entri baru di cart_sellers.");
                }
            }
        }
    }


    private void addOrUpdateCartItem(Connection connection, int cartSellerId, int productId, int quantity) throws SQLException {
        String selectQuery = "SELECT id, quantity FROM cart_items WHERE cart_seller_id = ? AND product_id = ?";
        try (PreparedStatement selectStmt = connection.prepareStatement(selectQuery)) {
            selectStmt.setInt(1, cartSellerId);
            selectStmt.setInt(2, productId);
            try (ResultSet resultSet = selectStmt.executeQuery()) {
                if (resultSet.next()) {
                    // Update quantity jika item sudah ada
                    int existingQuantity = resultSet.getInt("quantity");
                    int newQuantity = existingQuantity + quantity;

                    String updateQuery = "UPDATE cart_items SET quantity = ?, updated_at = CURRENT_TIMESTAMP WHERE id = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, newQuantity);
                        updateStmt.setInt(2, resultSet.getInt("id"));
                        updateStmt.executeUpdate();
                    }
                } else {
                    // Tambahkan item baru jika belum ada
                    String insertQuery = "INSERT INTO cart_items (cart_seller_id, product_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setInt(1, cartSellerId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, quantity);
                        insertStmt.executeUpdate();
                    }
                }
            }
        }
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
