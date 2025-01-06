package com.ecommerce.content;

import com.ecommerce.App; // Pastikan App memiliki loggedInUser
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.db.models.User;
import com.ecommerce.db.DatabaseUtils;
import com.ecommerce.utils.PasswordUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.util.Map;

public class ProfileController implements MainLayoutController.MainLayoutAware {

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
    private TextField usernameField;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private Label roleLabel;

    @FXML
    private VBox sellerRequestContainer;

    @FXML
    private HBox customerStatus;

    @FXML
    private HBox noneStatus;

    @FXML
    private Label sellerRequestStatusLabel;

    @FXML
    private Button requestSellerButton;

    @FXML
    private Button saveButton;

    private User loggedInUser;
    private String sellerRequest;
    private MainLayoutController mainLayoutController;

    @FXML
    public void initialize() {
        // Ambil data pengguna yang sedang login
        loggedInUser = App.loggedInUser; // Asumsikan ini sudah disiapkan saat login

        if (loggedInUser == null) {
            System.err.println("[ERROR] No logged-in user found.");
            return;
        }

        // Isi data awal pengguna
        fetchLoggedInUser();

        // Setup UI components
        setupSellerRequestSection();
        setupPasswordFieldListener();
        setupNavigationButtons();

        if(!("SELLER".equalsIgnoreCase(loggedInUser.getRole().name()))){
            productsButton.setVisible(false);
            ordersButton.setVisible(false);
        }

        // Hide navigation buttons if the user is not a seller

        // Event handlers
        requestSellerButton.setOnAction(event -> handleRequestSeller());
        saveButton.setOnAction(event -> onSaveButtonClicked());
    }

    private void setupSellerRequestSection() {
        // Jika pengguna adalah SELLER, sembunyikan container permintaan
        if ("SELLER".equals(loggedInUser.getRole().name())) {
            sellerRequestContainer.setVisible(false);
        }
        // Jika pengguna adalah CUSTOMER dan belum mengajukan permintaan (NONE)
        else if ("CUSTOMER".equals(loggedInUser.getRole().name()) && "NONE".equals(sellerRequest)) {
            sellerRequestContainer.setVisible(true);
            customerStatus.setVisible(false);
            noneStatus.setVisible(true); // Tampilkan tombol "Request Seller"
            requestSellerButton.setVisible(true); // Pastikan tombol terlihat
        }
        // Jika pengguna adalah CUSTOMER dan sudah ada permintaan (PENDING, APPROVED, REJECTED)
        else if ("CUSTOMER".equals(loggedInUser.getRole().name())) {
            sellerRequestContainer.setVisible(true);
            customerStatus.setVisible(true);
            noneStatus.setVisible(false); // Sembunyikan tombol "Request Seller"
        }
        // Untuk role lainnya, sembunyikan container permintaan
        else {
            sellerRequestContainer.setVisible(false);
        }
    }


    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        System.out.println("[INFO] MainLayoutController berhasil diatur ke ProfileController.");
    }

    private void setupNavigationButtons() {
        profileButton.setOnAction(event -> navigateTo("/com/ecommerce/content/ProfileView.fxml"));
        productsButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/ManageProductsView.fxml"));
        ordersButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/OrdersView.fxml"));
        cartButton.setOnAction(event->navigateTo("/com/ecommerce/content/customer/CartView.fxml"));
        checkoutButton.setOnAction(event->navigateTo("/com/ecommerce/content/customer/CheckoutView.fxml"));
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

    private void fetchLoggedInUser() {
        try (var connection = DatabaseUtils.getConnection()) {
            String query = "SELECT username, email, seller_request, role FROM users WHERE id = ?";
            try (var preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, App.loggedInUser.getId()); // Gunakan ID dari App.loggedInUser

                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        // Perbarui data pengguna dari hasil query
                        String username = resultSet.getString("username");
                        String email = resultSet.getString("email");
                        String sellerRequestFromDb = resultSet.getString("seller_request");
                        String roleFromDb = resultSet.getString("role");

                        // Set data ke UI
                        usernameField.setText(username);
                        emailField.setText(email);
                        roleLabel.setText(roleFromDb);

                        // Perbarui sellerRequest untuk digunakan dalam logika lainnya
                        if (sellerRequestFromDb != null) {
                            sellerRequest = sellerRequestFromDb;
                            sellerRequestStatusLabel.setText(sellerRequest);
                        } else {
                            sellerRequest = "NONE";
                            sellerRequestStatusLabel.setText(sellerRequest);
                        }

                        // Update role di loggedInUser (jika diperlukan)
                        App.loggedInUser.setRole(User.Role.fromString(roleFromDb));
                    } else {
                        System.err.println("[ERROR] No user data found for ID: " + App.loggedInUser.getId());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to fetch logged-in user data from the database:");
            e.printStackTrace();
        }
    }



    private void updateSellerRequestToPending() {
        try (var connection = DatabaseUtils.getConnection()) {
            String updateQuery = """
                        UPDATE users
                        SET seller_request = ?
                        WHERE id = ?
                    """;

            try (var preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, User.SellerRequest.PENDING.name());
                preparedStatement.setInt(2, loggedInUser.getId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("[INFO] Seller request updated to PENDING.");
                    loggedInUser.setSellerRequest(User.SellerRequest.PENDING); // Update model in memory
                    navigateTo("/com/ecommerce/content/ProfileView.fxml");
                } else {
                    System.err.println("[WARN] No changes to seller_request.");
                }
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update seller_request:");
            e.printStackTrace();
        }
    }

    private void setupPasswordFieldListener() {
        confirmPasswordField.setVisible(false);
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            confirmPasswordField.setVisible(!newValue.isEmpty());
        });
    }

    private void handleRequestSeller() {
        System.out.println("[INFO] Seller request submitted.");
        updateSellerRequestToPending();
        sellerRequest = "PENDING";
        setupSellerRequestSection();
    }


    @FXML
    private void onSaveButtonClicked() {
        String username = usernameField.getText();
        String email = emailField.getText();
        String newPassword = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (!newPassword.isBlank() || !confirmPassword.isBlank()) {
            if (!newPassword.equals(confirmPassword)) {
                System.err.println("[ERROR] Password and Confirm Password do not match.");
                return;
            }
        }

        try {
            saveUserChanges(username, email, newPassword);
            System.out.println("[INFO] User data updated.");
        } catch (Exception e) {
            System.err.println("[ERROR] Failed to update user data:");
            e.printStackTrace();
        }
    }

    private void saveUserChanges(String username, String email, String plainPassword) throws Exception {
        try (var connection = DatabaseUtils.getConnection()) {
            String updateQuery = """
                        UPDATE users
                        SET 
                            username = COALESCE(?, username),
                            email = COALESCE(?, email),
                            password = ?,
                            seller_request = ?
                        WHERE id = ?
                    """;

            String encryptedPassword = plainPassword.isBlank()
                    ? getExistingPassword(connection)
                    : PasswordUtils.encrypt(plainPassword);

            try (var preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, email);
                preparedStatement.setString(3, encryptedPassword);
                preparedStatement.setString(4, sellerRequest);
                preparedStatement.setInt(5, loggedInUser.getId());

                int rowsUpdated = preparedStatement.executeUpdate();
                if (rowsUpdated > 0) {
                    System.out.println("[INFO] User data successfully updated in the database.");
                    navigateTo("/com/ecommerce/content/ProfileView.fxml");
                } else {
                    System.err.println("[WARN] No changes were made to the user data.");
                }
            }
        }
    }

    private String getExistingPassword(Connection connection) throws Exception {
        String query = "SELECT password FROM users WHERE id = ?";
        try (var passwordStmt = connection.prepareStatement(query)) {
            passwordStmt.setInt(1, loggedInUser.getId());
            try (var resultSet = passwordStmt.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getString("password");
                }
            }
        }
        throw new IllegalStateException("[ERROR] Failed to fetch existing password.");
    }
}


