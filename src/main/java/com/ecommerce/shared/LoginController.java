package com.ecommerce.shared;

import com.ecommerce.App;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    @FXML
    private void initialize() {
        loginButton.setOnAction(event -> handleLogin());
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        // Validasi input
        if (ValidationUtils.isFieldEmpty(usernameField) || ValidationUtils.isFieldEmpty(passwordField)) {
            showAlert("Login Gagal", "Username atau password tidak boleh kosong");
            return;
        }

        // Contoh logika login (ganti dengan logika validasi sesungguhnya)
        if ("admin".equals(username) && "adminpass".equals(password)) {
            navigateToDashboard("admin");
        } else if ("customer".equals(username) && "customerpass".equals(password)) {
            navigateToDashboard("customer");
        } else if ("seller".equals(username) && "sellerpass".equals(password)) {
            navigateToDashboard("seller");
        } else {
            showAlert("Login Gagal", "Username atau password salah");
        }
    }

    private void navigateToDashboard(String role) {
        try {
            App.showMainView(role); // Pastikan method ini sudah didefinisikan di App
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String content) {
        AlertUtils.showErrorAlert("Login Failed", "Invalid username or password", "Please try again.");
    }
}
