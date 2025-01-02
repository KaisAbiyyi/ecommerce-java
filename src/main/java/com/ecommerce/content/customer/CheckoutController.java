package com.ecommerce.content.customer;

import com.ecommerce.App;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.utils.DatabaseUtils;
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

public class CheckoutController implements MainLayoutController.MainLayoutAware {

    @FXML
    private VBox orderDetailsContainer;

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

        // Handle confirm payment button
        confirmPaymentButton.setOnAction(event -> handleConfirmPayment());
    }

    private void loadOrderDetails() {
        orderDetailsContainer.getChildren().clear();
        int userId = App.loggedInUser.getId();
        double grandTotalPrice = 0.0; // Variabel untuk menyimpan total semua pesanan

        try (Connection connection = DatabaseUtils.getConnection()) {
            // Query untuk mendapatkan semua pesanan pengguna
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

                        // Jika pesanan baru dimulai, buat kontainer baru untuk pesanan ini
                        if (orderId != currentOrderId) {
                            currentOrderId = orderId;

                            if (currentOrderContainer != null) {
                                // Tambahkan total harga untuk pesanan sebelumnya
                                Label totalLabel = new Label("Total Order Price: Rp" + String.format("%,.2f", orderTotalPrice));
                                totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                                currentOrderContainer.getChildren().add(totalLabel);

                                // Tambahkan total order sebelumnya ke grand total
                                grandTotalPrice += orderTotalPrice;
                            }

                            orderTotalPrice = resultSet.getDouble("total_order_price");

                            // Tambahkan judul untuk pesanan baru
                            Label orderHeader = new Label("Order ID: " + orderId + " - Status: " + resultSet.getString("order_status"));
                            orderHeader.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
                            orderDetailsContainer.getChildren().add(orderHeader);

                            currentOrderContainer = new VBox();
                            currentOrderContainer.setSpacing(5);
                            currentOrderContainer.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1;");
                            orderDetailsContainer.getChildren().add(currentOrderContainer);
                        }

                        // Tambahkan detail item pesanan
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
                        // Tambahkan total harga untuk pesanan terakhir
                        Label totalLabel = new Label("Total Order Price: Rp" + String.format("%,.2f", orderTotalPrice));
                        totalLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
                        currentOrderContainer.getChildren().add(totalLabel);

                        // Tambahkan total order terakhir ke grand total
                        grandTotalPrice += orderTotalPrice;
                    }
                }
            }

            // Update total harga di label utama
            totalPriceLabel.setText("Total: Rp" + String.format("%,.2f", grandTotalPrice));
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


    private void saveProofOfPayment(Connection connection, int orderId) throws Exception {
        if (uploadedFilePath == null) {
            throw new IllegalStateException("Proof of payment not uploaded.");
        }

        String insertPaymentQuery = """
            INSERT INTO payments (order_id, proof_of_payment)
            VALUES (?, ?)
            """;

        try (PreparedStatement statement = connection.prepareStatement(insertPaymentQuery)) {
            statement.setInt(1, orderId);
            statement.setString(2, uploadedFilePath);
            statement.executeUpdate();
            System.out.println("[INFO] Bukti pembayaran berhasil disimpan.");
        }
    }



    private void handleConfirmPayment() {
        if (App.loggedInUser == null || App.loggedInUser.getId() == 0) {
            System.err.println("[ERROR] Tidak ada pengguna yang sedang login.");
            return;
        }

        if (uploadedFilePath == null || uploadedFilePath.isEmpty()) {
            System.err.println("[ERROR] Bukti pembayaran belum diunggah.");
            return;
        }

        int userId = App.loggedInUser.getId();

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
            String paymentSuccessPath = "/com/ecommerce/content/ProfileView.fxml";

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




    private void insertOrderSellersAndItems(Connection connection, int userId, int orderId) throws Exception {
        // Insert into order_sellers
        String insertOrderSellersQuery = """
                INSERT INTO order_sellers (order_id, seller_id, status)
                SELECT ?, cs.seller_id, 'PENDING'
                FROM cart_sellers cs
                WHERE cs.user_id = ?
                """;

        try (PreparedStatement sellerStatement = connection.prepareStatement(insertOrderSellersQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
            sellerStatement.setInt(1, orderId);
            sellerStatement.setInt(2, userId);
            sellerStatement.executeUpdate();

            try (ResultSet sellerKeys = sellerStatement.getGeneratedKeys()) {
                while (sellerKeys.next()) {
                    int orderSellerId = sellerKeys.getInt(1);

                    // Insert into order_items
                    String insertOrderItemsQuery = """
                            INSERT INTO order_items (order_seller_id, product_id, quantity, price)
                            SELECT ?, ci.product_id, ci.quantity, p.price
                            FROM cart_items ci
                            JOIN products p ON ci.product_id = p.id
                            WHERE ci.cart_seller_id = ?
                            """;

                    try (PreparedStatement itemStatement = connection.prepareStatement(insertOrderItemsQuery)) {
                        itemStatement.setInt(1, orderSellerId);
                        itemStatement.setInt(2, orderSellerId);
                        itemStatement.executeUpdate();
                    }
                }
            }
        }
    }

    private void clearCart(Connection connection, int userId) throws Exception {
        String deleteCartQuery = """
                DELETE cs, ci
                FROM cart_sellers cs
                LEFT JOIN cart_items ci ON cs.id = ci.cart_seller_id
                WHERE cs.user_id = ?
                """;

        try (PreparedStatement statement = connection.prepareStatement(deleteCartQuery)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
            System.out.println("[INFO] Keranjang berhasil dikosongkan.");
        }
    }

    private void navigateToPayment() {
        try {
            String paymentViewPath = "/com/ecommerce/content/customer/PaymentView.fxml";

            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(paymentViewPath, null);
            }

            mainLayoutController.loadContent(paymentViewPath);
            System.out.println("[INFO] Navigasi ke halaman PaymentView berhasil.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal navigasi ke halaman PaymentView:");
            e.printStackTrace();
        }
    }
}
