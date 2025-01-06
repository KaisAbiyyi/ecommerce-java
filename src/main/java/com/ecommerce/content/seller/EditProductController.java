package com.ecommerce.content.seller;

import com.ecommerce.App;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.db.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller untuk mengedit detail produk.
 */
public class EditProductController implements MainLayoutController.MainLayoutAware {

    @FXML
    private Button profileButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button ordersButton;
    @FXML
    private Button cartButton;
    @FXML
    private Button checkoutButton;

    @FXML
    private ImageView productImageView;

    @FXML
    private TextField productNameInput;

    @FXML
    private TextField productDescriptionInput;

    @FXML
    private TextField productPriceInput;

    @FXML
    private TextField productStockInput;

    @FXML
    private ComboBox<String> categorySelect; // Combo box untuk kategori

    @FXML
    private Button updateButton;

    @FXML
    private Button uploadButton;

    private int productId;
    private File selectedImageFile;
    private MainLayoutController mainLayoutController;

    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        System.out.println("[INFO] MainLayoutController berhasil diatur di EditProductController.");
    }

    public void setProductId(int productId) {
        this.productId = productId;
        System.out.println("[DEBUG] setProductId dipanggil dengan ID: " + productId);
        loadProductData();
    }

    @FXML
    private void initialize() {
        System.out.println("[INFO] EditProductController diinisialisasi.");

        setupNavigationButtons();
        updateButton.setOnAction(event -> handleUpdate());
        uploadButton.setOnAction(event -> handleImageUpload());
        loadCategories(); // Muat kategori saat inisialisasi
    }

    private void setupNavigationButtons() {
        profileButton.setOnAction(event -> navigateTo("/com/ecommerce/content/ProfileView.fxml"));
        productsButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/ManageProductsView.fxml"));
        ordersButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/OrdersView.fxml"));
        cartButton.setOnAction(event -> navigateTo("/com/ecommerce/content/customer/CartView.fxml"));
        checkoutButton.setOnAction(event -> navigateTo("/com/ecommerce/content/customer/CheckoutView.fxml"));
    }

    private void navigateTo(String path) {
        if (mainLayoutController == null) {
            System.err.println("[ERROR] MainLayoutController is not set.");
            return;
        }

        try {
            // Tambahkan halaman ke stack navigasi jika NavbarController tersedia
            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(path, Map.of()); // Tidak ada data tambahan
            }

            // Muat konten menggunakan MainLayoutController
            mainLayoutController.loadContent(path);
            System.out.println("[INFO] Navigasi ke " + path + " berhasil.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal navigasi ke " + path + ":");
            e.printStackTrace();
        }
    }

    private void loadProductData() {
        System.out.println("[INFO] Memuat detail produk dengan ID: " + productId);
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
                    SELECT name, description, price, stock, image_url, category_id
                    FROM products
                    WHERE id = ?
                    """;

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, productId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    productNameInput.setText(rs.getString("name"));
                    productDescriptionInput.setText(rs.getString("description"));
                    productPriceInput.setText(rs.getBigDecimal("price").toPlainString());
                    productStockInput.setText(String.valueOf(rs.getInt("stock")));

                    int categoryId = rs.getInt("category_id");
                    categorySelect.getSelectionModel().select(getCategoryNameById(categoryId));

                    String imageUrl = rs.getString("image_url");
                    try {
                        Image productImage = new Image(getClass().getResource(imageUrl).toExternalForm());
                        productImageView.setImage(productImage);
                        System.out.println("[INFO] Gambar produk berhasil dimuat.");
                    } catch (Exception e) {
                        System.err.println("[ERROR] Gagal memuat gambar produk, memakai gambar default.");
                        productImageView.setImage(
                                new Image(getClass().getResource("/images/products/default.jpg").toExternalForm())
                        );
                    }
                } else {
                    System.err.println("[WARN] Produk dengan ID " + productId + " tidak ditemukan di database.");
                    showProductNotFound();
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kesalahan saat memuat detail produk:");
            e.printStackTrace();
        }
    }

    private void loadCategories() {
        System.out.println("[INFO] Memuat daftar kategori...");
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT id, name FROM categories";

            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                ResultSet rs = stmt.executeQuery();
                List<String> categories = new ArrayList<>();

                while (rs.next()) {
                    categories.add(rs.getString("name"));
                }

                categorySelect.getItems().addAll(categories);
                System.out.println("[INFO] Kategori berhasil dimuat.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat kategori:");
            e.printStackTrace();
        }
    }

    private String getCategoryNameById(int categoryId) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT name FROM categories WHERE id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setInt(1, categoryId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return rs.getString("name");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal mendapatkan nama kategori berdasarkan ID:");
            e.printStackTrace();
        }
        return null;
    }

    private void showProductNotFound() {
        productNameInput.setText("Product Not Found");
        productDescriptionInput.setText("No description available.");
        productPriceInput.setText("0.00");
        productStockInput.setText("0");
        productImageView.setImage(
                new Image(getClass().getResource("/images/products/default.jpg").toExternalForm())
        );
    }

    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            productImageView.setImage(image);
            System.out.println("[INFO] Image selected: " + file.getAbsolutePath());
        } else {
            System.out.println("[INFO] Image upload canceled.");
        }
    }

    private void handleUpdate() {
        String name = productNameInput.getText();
        String description = productDescriptionInput.getText();
        String price = productPriceInput.getText();
        String stock = productStockInput.getText();
        String category = categorySelect.getValue();

        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || stock.isEmpty() || category == null) {
            System.err.println("[ERROR] Semua bidang harus diisi.");
            return;
        }

        try (Connection connection = DatabaseUtils.getConnection()) {
            // Ambil URL gambar lama dari database
            String oldImageUrl = null;
            String queryGetOldImage = """
                    SELECT image_url
                    FROM products
                    WHERE id = ?
                    """;
            try (PreparedStatement stmt = connection.prepareStatement(queryGetOldImage)) {
                stmt.setInt(1, productId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    oldImageUrl = rs.getString("image_url");
                }
            }

            // Jika tidak ada gambar baru diunggah, gunakan URL gambar lama
            String imageUrl = selectedImageFile != null ? saveImage(selectedImageFile) : oldImageUrl;

            String sql = """
                    UPDATE products
                    SET name = ?, description = ?, price = ?, stock = ?, image_url = ?, 
                        category_id = (SELECT id FROM categories WHERE name = ?), updated_at = NOW()
                    WHERE id = ?
                    """;

            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setString(2, description);
                stmt.setBigDecimal(3, new BigDecimal(price));
                stmt.setInt(4, Integer.parseInt(stock));
                stmt.setString(5, imageUrl != null ? imageUrl : "/images/products/default.jpg");
                stmt.setString(6, category);
                stmt.setInt(7, productId);

                int rowsUpdated = stmt.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("[INFO] Produk berhasil diperbarui.");
                } else {
                    System.err.println("[ERROR] Tidak ada produk yang diperbarui.");
                }
            }

            // Navigasi kembali ke halaman ManageProductsView
            mainLayoutController.loadContent("/com/ecommerce/content/seller/ManageProductsView.fxml");

        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memperbarui produk:");
            e.printStackTrace();
        }
    }


    private String saveImage(File imageFile) throws Exception {
        if (imageFile == null) {
            return null; // Tidak ada gambar baru yang diunggah
        }

        String targetDirectory = "src/main/resources/images/products/";
        String targetFileName = imageFile.getName();
        Path targetPath = Path.of(targetDirectory, targetFileName);

        Files.createDirectories(Path.of(targetDirectory));
        Files.copy(imageFile.toPath(), targetPath);

        System.out.println("[INFO] Image saved to: " + targetPath.toAbsolutePath());
        return "/images/products/" + targetFileName; // Return relative path
    }
}
