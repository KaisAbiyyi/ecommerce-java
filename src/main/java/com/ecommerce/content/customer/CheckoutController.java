package com.ecommerce.content.customer;

import com.ecommerce.App;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.db.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import static com.ecommerce.App.loggedInUser;

public class CheckoutController implements MainLayoutController.MainLayoutAware {

    @FXML
    private Button profileButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button cartButton; // Tambahkan ini untuk menghubungkan tombol dengan controller

    @FXML
    private Button checkoutButton; // Tambahkan ini untuk menghubungkan tombol dengan controller

    @FXML
    private Button ordersButton;

    @FXML
    private VBox orderDetailsContainer;
    @FXML
    private VBox fileUploadContainer;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button confirmPaymentButton;

    @FXML
    private Button uploadProofButton;

    @FXML
    private Label fileNameLabel;

    private String uploadedFilePath = null;

    private MainLayoutController mainLayoutController;

    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        if (mainLayoutController == null) {
            System.err.println("[ERROR] MainLayoutController belum diatur.");
        } else {
            System.out.println("[INFO] MainLayoutController berhasil diatur ke CheckoutController.");
        }
    }

    @FXML
    public void initialize() {
        System.out.println("[INFO] CheckoutController diinisialisasi.");

        // Load order details
        loadOrderDetails();


        setupNavigationButtons();
        if (!("SELLER".equalsIgnoreCase(loggedInUser.getRole().name()))) {
            productsButton.setVisible(false);
            ordersButton.setVisible(false);
        }

        // Handle confirm payment button
        confirmPaymentButton.setOnAction(event -> handleConfirmPayment());
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
            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(path, Map.of());
            }

            mainLayoutController.loadContent(path);
            System.out.println("[INFO] Navigasi ke " + path + " berhasil.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal navigasi ke " + path + ":");
            e.printStackTrace();
        }
    }

    private void loadOrderDetails() {
        orderDetailsContainer.getChildren().clear();
        int userId = loggedInUser.getId();
        double grandTotalUnpaidPrice = 0.0; // Variabel untuk menyimpan total harga pesanan yang belum dibayar
        boolean hasPendingOrders = false; // Variabel untuk mengecek apakah ada pesanan yang pending

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = """
            SELECT 
                o.id AS order_id,
                o.status AS order_status,
                os.id AS order_seller_id,
                os.seller_id AS seller_id,
                os.status AS seller_status,
                oi.product_id AS product_id,
                p.name AS product_name,
                oi.quantity AS quantity,
                oi.price AS price,
                (oi.quantity * oi.price) AS total_item_price,
                o.total_price AS total_order_price
            FROM orders o
            JOIN order_sellers os ON o.id = os.order_id
            JOIN order_items oi ON os.id = oi.order_seller_id
            JOIN products p ON oi.product_id = p.id
            WHERE o.user_id = ?
            ORDER BY o.created_at DESC
            """;

            try (PreparedStatement statement = connection.prepareStatement(query)) {
                statement.setInt(1, userId);
                try (ResultSet resultSet = statement.executeQuery()) {
                    int currentOrderId = -1;
                    double orderTotalPrice = 0.0;
                    VBox currentOrderContainer = null;

                    while (resultSet.next()) {
                        int orderId = resultSet.getInt("order_id");
                        String orderStatus = resultSet.getString("order_status"); // Ambil status order di dalam iterasi

                        // Jika pesanan baru dimulai, buat kontainer baru untuk pesanan ini
                        if (orderId != currentOrderId) {
                            if (currentOrderContainer != null) {
                                Label totalLabel = new Label("Total Order Price: Rp" + String.format("%,.2f", orderTotalPrice));
                                totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                                currentOrderContainer.getChildren().add(totalLabel);
                            }

                            currentOrderId = orderId;
                            orderTotalPrice = resultSet.getDouble("total_order_price");

                            Label orderHeader = new Label("Order ID: " + orderId + " - Status: " + orderStatus);
                            orderHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                            orderDetailsContainer.getChildren().add(orderHeader);

                            currentOrderContainer = new VBox();
                            currentOrderContainer.setSpacing(5);
                            currentOrderContainer.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1;");
                            orderDetailsContainer.getChildren().add(currentOrderContainer);
                        }

                        if ("PENDING".equals(orderStatus)) {
                            grandTotalUnpaidPrice += resultSet.getDouble("total_order_price");
                            hasPendingOrders = true;
                        }

                        String productName = resultSet.getString("product_name");
                        int quantity = resultSet.getInt("quantity");
                        double price = resultSet.getDouble("price");
                        double totalItemPrice = resultSet.getDouble("total_item_price");

                        Label itemLabel = new Label(
                                productName + " - " + quantity + " x Rp" + String.format("%,.2f", price) +
                                        " = Rp" + String.format("%,.2f", totalItemPrice)
                        );
                        itemLabel.setStyle("-fx-font-size: 14px;");
                        currentOrderContainer.getChildren().add(itemLabel);
                    }

                    if (currentOrderContainer != null) {
                        Label totalLabel = new Label("Total Order Price: Rp" + String.format("%,.2f", orderTotalPrice));
                        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                        currentOrderContainer.getChildren().add(totalLabel);
                    }
                }
            }

            if (hasPendingOrders) {
                totalPriceLabel.setText("Total (Unpaid): Rp" + String.format("%,.2f", grandTotalUnpaidPrice));
                totalPriceLabel.setVisible(true);
                confirmPaymentButton.setVisible(true);
                fileUploadContainer.setVisible(true);
            } else {
                totalPriceLabel.setVisible(false);
                confirmPaymentButton.setVisible(false);
                fileUploadContainer.setVisible(false);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat rincian pesanan:");
            e.printStackTrace();
        }
    }




    @FXML
    private void handleFileUpload() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Proof of Payment");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg"));
        File selectedFile = fileChooser.showOpenDialog(App.primaryStage);

        if (selectedFile != null) {
            uploadedFilePath = "/images/proofs/" + selectedFile.getName();
            fileNameLabel.setText(selectedFile.getName());
            try {
                // Pastikan direktori tujuan ada
                Path targetDir = Paths.get("src/main/resources/images/proofs");
                if (!Files.exists(targetDir)) {
                    Files.createDirectories(targetDir);
                }

                // Salin file ke direktori tujuan
                Path targetPath = targetDir.resolve(selectedFile.getName());
                Files.copy(selectedFile.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                System.out.println("[INFO] File berhasil diunggah: " + uploadedFilePath);
            } catch (IOException e) {
                System.err.println("[ERROR] Gagal menyimpan file:");
                e.printStackTrace();
            }
        } else {
            fileNameLabel.setText("No file selected");
        }
    }



    private void handleConfirmPayment() {
        if (loggedInUser == null || loggedInUser.getId() == 0) {
            System.err.println("[ERROR] Tidak ada pengguna yang sedang login.");
            return;
        }

        if (uploadedFilePath == null || uploadedFilePath.isEmpty()) {
            System.err.println("[ERROR] Bukti pembayaran belum diunggah.");
            return;
        }

        int userId = loggedInUser.getId();

        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false); // Mulai transaksi

            // Query untuk mendapatkan ID pesanan terakhir pengguna
            String getOrderQuery = """
                        SELECT id FROM orders
                        WHERE user_id = ? AND status = 'PENDING'
                        ORDER BY created_at DESC
                        LIMIT 1
                    """;

            int orderId = -1;

            try (PreparedStatement getOrderStmt = connection.prepareStatement(getOrderQuery)) {
                getOrderStmt.setInt(1, userId);
                try (ResultSet resultSet = getOrderStmt.executeQuery()) {
                    if (resultSet.next()) {
                        orderId = resultSet.getInt("id");
                    } else {
                        System.err.println("[ERROR] Tidak ada pesanan PENDING yang ditemukan.");
                        connection.rollback();
                        return;
                    }
                }
            }

            // Perbarui status pesanan menjadi PAID
            String updateOrderStatusQuery = """
                        UPDATE orders
                        SET status = 'PAID'
                        WHERE id = ?
                    """;

            try (PreparedStatement updateOrderStmt = connection.prepareStatement(updateOrderStatusQuery)) {
                updateOrderStmt.setInt(1, orderId);
                int rowsUpdated = updateOrderStmt.executeUpdate();

                if (rowsUpdated == 0) {
                    System.err.println("[ERROR] Gagal memperbarui status pesanan.");
                    connection.rollback();
                    return;
                }
            }

            // Tambahkan entri ke tabel payments
            String insertPaymentQuery = """
                        INSERT INTO payments (order_id, proof_of_payment)
                        VALUES (?, ?)
                    """;

            try (PreparedStatement insertPaymentStmt = connection.prepareStatement(insertPaymentQuery)) {
                insertPaymentStmt.setInt(1, orderId);
                insertPaymentStmt.setString(2, uploadedFilePath);
                insertPaymentStmt.executeUpdate();
            }

            connection.commit(); // Commit transaksi
            System.out.println("[INFO] Pembayaran berhasil dikonfirmasi untuk Pesanan ID: " + orderId);

            // Navigasi ke halaman sukses pembayaran
            navigateToProfileView();
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal mengonfirmasi pembayaran. Transaksi dibatalkan:");
            e.printStackTrace();
        }
    }

    private void navigateToProfileView() {
        try {
            String paymentSuccessPath = "/com/ecommerce/customer/CheckoutView.fxml";

            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(paymentSuccessPath, null);
            }

            mainLayoutController.loadContent(paymentSuccessPath);
            System.out.println("[INFO] Navigasi ke halaman sukses pembayaran berhasil.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal navigasi ke halaman sukses pembayaran:");
            e.printStackTrace();
        }
    }



}
