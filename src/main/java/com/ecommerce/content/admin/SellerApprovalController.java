package com.ecommerce.content.admin;

import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SellerApprovalController implements MainLayoutController.MainLayoutAware {

    @FXML
    private VBox requestContainer;

    private MainLayoutController mainLayoutController;

    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController tidak diatur. Navigasi mungkin gagal.");
        } else {
            System.out.println("[INFO] MainLayoutController berhasil diatur ke SellerApprovalController.");
        }
    }

    @FXML
    private void initialize() {
        System.out.println("[INFO] SellerApprovalController diinisialisasi.");

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController belum diatur. Navigasi mungkin gagal.");
        }

        // Muat daftar permintaan approval
        loadPendingRequests();
    }

    private void loadPendingRequests() {
        requestContainer.getChildren().clear();

        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "SELECT id, username, email FROM users WHERE seller_request = 'PENDING'";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                ResultSet resultSet = preparedStatement.executeQuery();

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");

                    addRequestItem(id, username, email);
                }

                System.out.println("[INFO] Permintaan approval berhasil dimuat.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat permintaan approval:");
            e.printStackTrace();
        }
    }

    private void addRequestItem(int userId, String username, String email) {
        VBox requestCard = new VBox();
        requestCard.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
        requestCard.setSpacing(10);

        Button approveButton = new Button("Approve");
        approveButton.setStyle("-fx-background-color: #31D0AA; -fx-text-fill: white;");
        approveButton.setOnAction(event -> handleApprove(userId));

        Button rejectButton = new Button("Reject");
        rejectButton.setStyle("-fx-background-color: #FF4D4F; -fx-text-fill: white;");
        rejectButton.setOnAction(event -> handleReject(userId));

        VBox actionButtons = new VBox(5, approveButton, rejectButton);

        // Isi informasi pengguna dan tombol
        requestCard.getChildren().addAll(
                createLabel("User ID: " + userId),
                createLabel("Username: " + username),
                createLabel("Email: " + email),
                actionButtons
        );

        // Tambahkan kartu ke container
        requestContainer.getChildren().add(requestCard);
    }

    private javafx.scene.control.Label createLabel(String text) {
        javafx.scene.control.Label label = new javafx.scene.control.Label(text);
        label.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        return label;
    }

    private void handleApprove(int userId) {
        updateSellerRequest(userId, "APPROVED");
    }

    private void handleReject(int userId) {
        updateSellerRequest(userId, "REJECTED");
    }

    private void updateSellerRequest(int userId, String status) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            // Mulai transaksi
            connection.setAutoCommit(false);

            // Query untuk memperbarui status permintaan seller
            String updateRequestQuery = "UPDATE users SET seller_request = ? WHERE id = ?";
            try (PreparedStatement updateRequestStmt = connection.prepareStatement(updateRequestQuery)) {
                updateRequestStmt.setString(1, status);
                updateRequestStmt.setInt(2, userId);

                int rowsUpdated = updateRequestStmt.executeUpdate();

                if (rowsUpdated > 0) {
                    System.out.println("[INFO] Permintaan seller berhasil diperbarui menjadi " + status + " untuk user ID: " + userId);

                    // Jika status adalah "APPROVED", ubah role menjadi "seller"
                    if ("APPROVED".equals(status)) {
                        String updateRoleQuery = "UPDATE users SET role = 'seller' WHERE id = ?";
                        try (PreparedStatement updateRoleStmt = connection.prepareStatement(updateRoleQuery)) {
                            updateRoleStmt.setInt(1, userId);

                            int roleUpdated = updateRoleStmt.executeUpdate();
                            if (roleUpdated > 0) {
                                System.out.println("[INFO] Peran user ID: " + userId + " berhasil diubah menjadi 'seller'.");
                            } else {
                                System.err.println("[WARN] Gagal mengubah peran user ID: " + userId);
                                connection.rollback(); // Batalkan transaksi jika gagal
                                return;
                            }
                        }
                    }

                    connection.commit(); // Commit transaksi jika berhasil
                    loadPendingRequests();
                } else {
                    System.err.println("[WARN] Tidak ada baris yang diperbarui untuk user ID: " + userId);
                    connection.rollback(); // Batalkan transaksi jika tidak ada perubahan
                }
            } catch (Exception e) {
                connection.rollback(); // Batalkan transaksi jika terjadi kesalahan
                System.err.println("[ERROR] Gagal memperbarui permintaan seller:");
                e.printStackTrace();
            } finally {
                connection.setAutoCommit(true); // Kembalikan auto-commit ke default
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memperbarui role dan status user:");
            e.printStackTrace();
        }
    }

}
