package com.ecommerce.shared;

import java.sql.Connection;

import com.ecommerce.App;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.dao.impl.UserDAOImpl;
import com.ecommerce.models.User;
import com.ecommerce.utils.DatabaseUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
}
