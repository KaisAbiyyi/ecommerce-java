package com.ecommerce.content.seller;

import com.ecommerce.App;
import com.ecommerce.dao.CategoryDAO;
import com.ecommerce.dao.impl.CategoryDAOImpl;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.models.Category;
import com.ecommerce.utils.DatabaseUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public class AddProductController implements MainLayoutController.MainLayoutAware {

    @FXML
    private Button profileButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button ordersButton;

    @FXML
    private Button uploadButton;

    @FXML
    private Button submitButton;

    @FXML
    private TextField productNameInput;

    @FXML
    private TextField productDescriptionInput;

    @FXML
    private TextField productPriceInput;

    @FXML
    private TextField productStockInput;

    @FXML
    private ComboBox<String> categorySelect;

    @FXML
    private ImageView productImageView;

    private File selectedImageFile;

    private MainLayoutController mainLayoutController;

    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        System.out.println("[INFO] MainLayoutController berhasil diatur ke AddProductController.");
    }

    @FXML
    private void initialize() {
        System.out.println("[INFO] AddProductController initialized.");

        // Setup navigation buttons
        setupNavigationButtons();

        // Setup upload button
        uploadButton.setOnAction(event -> handleImageUpload());

        // Setup submit button
        submitButton.setOnAction(event -> handleSubmit());

        // Load categories into ComboBox
        loadCategories();
    }

    private void setupNavigationButtons() {
        profileButton.setOnAction(event -> navigateTo("/com/ecommerce/content/ProfileView.fxml"));
        productsButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/ManageProductsView.fxml"));
        ordersButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/OrdersView.fxml"));
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


    private void handleImageUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(App.primaryStage);
        if (file != null) {
            selectedImageFile = file;
            Image image = new Image(file.toURI().toString());
            productImageView.setImage(image);
            System.out.println("[INFO] Image selected: " + file.getAbsolutePath());
        } else {
            System.out.println("[INFO] Image upload canceled.");
        }
    }

    private void loadCategories() {
        try (Connection connection = DatabaseUtils.getConnection()) {
            CategoryDAO categoryDAO = new CategoryDAOImpl(connection);
            List<Category> categories = categoryDAO.getAllCategories();

            // Konversi ke ObservableList dengan hanya menampilkan nama kategori
            ObservableList<String> categoryList = FXCollections.observableArrayList();
            for (Category category : categories) {
                categoryList.add(category.getName()); // Pastikan `Category` memiliki getter untuk nama
            }

            categorySelect.setItems(categoryList);

            System.out.println("[INFO] Categories loaded into ComboBox.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to load categories:");
            e.printStackTrace();
        }
    }


    private void handleSubmit() {
        String name = productNameInput.getText();
        String description = productDescriptionInput.getText();
        String price = productPriceInput.getText();
        String stock = productStockInput.getText();
        String category = categorySelect.getValue();

        if (name.isEmpty() || description.isEmpty() || price.isEmpty() || stock.isEmpty() || category == null) {
            System.err.println("[ERROR] All fields, including category, must be filled.");
            return;
        }

        if (selectedImageFile == null) {
            System.err.println("[ERROR] Product image is required.");
            return;
        }

        try (Connection connection = DatabaseUtils.getConnection()) {
            // Simpan gambar di folder resources/images/products
            String imageUrl = saveImage(selectedImageFile);

            // Ambil ID kategori dari nama kategori
            int categoryId = getCategoryID(connection, category);

            // Simpan produk ke database
            saveProductToDatabase(connection, name, description, price, stock, categoryId, imageUrl);

            System.out.println("[INFO] Product submitted successfully.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to submit product:");
            e.printStackTrace();
        }

        // Navigate back to the Manage Products view
        navigateTo("/com/ecommerce/content/seller/ManageProductsView.fxml");
    }
    private String saveImage(File imageFile) throws Exception {
        String targetDirectory = "src/main/resources/images/products/";
        String targetFileName = imageFile.getName();
        Path targetPath = Path.of(targetDirectory, targetFileName);

        // Buat folder jika belum ada
        Files.createDirectories(Path.of(targetDirectory));

        // Salin file gambar
        Files.copy(imageFile.toPath(), targetPath);

        System.out.println("[INFO] Image saved to: " + targetPath.toAbsolutePath());
        return "/images/products/" + targetFileName; // Return relative path
    }

    private int getCategoryID(Connection connection, String categoryName) throws Exception {
        String sql = "SELECT id FROM categories WHERE name = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            } else {
                throw new Exception("Category not found: " + categoryName);
            }
        }
    }
    private void saveProductToDatabase(Connection connection, String name, String description,
                                       String price, String stock, int categoryId, String imageUrl) throws Exception {
        String sql = """
        INSERT INTO products (name, description, price, stock, seller_id, category_id, image_url, created_at, updated_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, NOW(), NOW())
        """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setBigDecimal(3, new java.math.BigDecimal(price));
            stmt.setInt(4, Integer.parseInt(stock));
            stmt.setInt(5, App.loggedInUser.getId());
            stmt.setInt(6, categoryId);
            stmt.setString(7, imageUrl);

            stmt.executeUpdate();
            System.out.println("[INFO] Product saved to database.");
        }
    }

}
