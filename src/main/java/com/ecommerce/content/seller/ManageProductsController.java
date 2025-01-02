package com.ecommerce.content.seller;

import com.ecommerce.App;
import com.ecommerce.components.AdminProductCard;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.impl.ProductDAOImpl;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.models.Product;
import com.ecommerce.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.util.List;
import java.util.Map;

public class ManageProductsController implements MainLayoutController.MainLayoutAware {

    @FXML
    private VBox productContainer;
    @FXML
    private Button profileButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button ordersButton;
    @FXML
    private Button addButton;

    private MainLayoutController mainLayoutController;

    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController tidak diatur. Navigasi mungkin gagal.");
        } else {
            System.out.println("[INFO] MainLayoutController berhasil diatur ke ManageProductsController.");
        }
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


    @FXML
    private void initialize() {
        System.out.println("[INFO] ManageProductsController diinisialisasi.");

        setupNavigationButtons();

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController belum diatur. Navigasi mungkin gagal.");
        }

        // Muat produk
        loadProducts();

        // Event handler tombol
        addButton.setOnAction(event -> goToAddProduct());
    }


    private void goToAddProduct() {
        if (mainLayoutController == null) {
            System.err.println("[ERROR] MainLayoutController belum diatur. Tidak dapat navigasi.");
            return;
        }

        try {
            String addProductPath = "/com/ecommerce/content/seller/AddProductView.fxml";

            // Tambahkan halaman ke stack navigasi jika NavbarController tersedia
            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(addProductPath, Map.of()); // Tidak ada data tambahan
            }

            // Muat konten menggunakan MainLayoutController
            mainLayoutController.loadContent(addProductPath);
            System.out.println("[INFO] Navigasi ke halaman Add Product berhasil.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal navigasi ke halaman Add Product:");
            e.printStackTrace();
        }
    }


    private void loadProducts() {
        try (Connection connection = DatabaseUtils.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(connection);

            // Ambil seller ID dari user yang sedang login
            int sellerId = App.loggedInUser.getId();

            // Ambil produk berdasarkan seller ID
            List<Product> products = productDAO.getProductsBySeller(sellerId);

            // Tambahkan setiap produk ke container
            for (Product product : products) {
                addProductItem(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        String.valueOf(product.getPrice()),
                        String.valueOf(product.getStock()),
                        product.getImageUrl() // Tambahkan URL gambar
                );
            }

            System.out.println("[INFO] Produk berhasil dimuat dari database.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat produk dari database:");
            e.printStackTrace();
        }
    }

    private static final String BASE_IMAGE_PATH = "file:src/main/resources";
    private static final String DEFAULT_IMAGE_PATH = BASE_IMAGE_PATH + "/images/products/default.jpg";

    private void addProductItem(int productId, String name, String description, String price, String stock, String imageUrl) {
        String fullImagePath;
        if (imageUrl != null && !imageUrl.isEmpty()) {
            fullImagePath = BASE_IMAGE_PATH + imageUrl; // Tambahkan base path ke URL dari database
        } else {
            fullImagePath = DEFAULT_IMAGE_PATH; // Gunakan default image jika URL kosong
        }

        Image productImage;
        try {
            productImage = new Image(fullImagePath); // Coba muat gambar
        } catch (Exception e) {
            System.err.println("[WARN] Gagal memuat gambar dari path: " + fullImagePath + ". Menggunakan default image.");
            productImage = new Image(DEFAULT_IMAGE_PATH); // Gunakan default jika gagal
        }

        AdminProductCard productCard = new AdminProductCard(productId, name, description, price, stock, productImage);
        productContainer.getChildren().add(productCard);
    }


}
