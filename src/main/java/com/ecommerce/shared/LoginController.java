package com.ecommerce.shared;

import java.io.IOException;
import java.sql.Connection;

import com.ecommerce.App;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.dao.impl.UserDAOImpl;
import com.ecommerce.models.User;
import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.utils.LocalStorageUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField emailField; // Ganti dari usernameField ke emailField
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        try {
            Connection connection = DatabaseUtils.getConnection();
            this.userDAO = new UserDAOImpl(connection);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database connection failed.");
        }

        loginButton.setOnAction(event -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Login Gagal", "Email atau password tidak boleh kosong");
            return;
        }

        try {
            // Cari dan verifikasi user berdasarkan email dan password
            User user = userDAO.getUserByEmailAndPassword(email, password);
            if (user != null) {
                // Simpan token user ke local storage
                String token = generateToken(user); // Simulasi token
                LocalStorageUtils.set("authToken", token);
                LocalStorageUtils.set("userRole", user.getRole().name());
                LocalStorageUtils.set("userId", String.valueOf(user.getId()));

                showAlert("Login Berhasil", "Selamat datang, " + user.getUsername());
                navigateToDashboard(user.getRole().name());
            } else {
                showAlert("Login Gagal", "Email atau password salah");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan saat login.");
        }
    }

    private String generateToken(User user) {
        // Simulasi token (gunakan algoritma lebih aman jika diperlukan)
        return "TOKEN_" + user.getId() + "_" + System.currentTimeMillis();
    }

    private void navigateToDashboard(String role) {
        try {
            App.showMainView(role);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void handleSignUpRedirect() {
        try {
            // Load RegisterView.fxml
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/shared/RegisterView.fxml"));
            Parent root = loader.load();

            // Ambil stage dari komponen saat ini
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            stage.setFullScreen(true); // Tetap fullscreen
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Tidak dapat membuka halaman register.");
        }
    }
}
