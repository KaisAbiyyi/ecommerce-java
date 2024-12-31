package com.ecommerce.shared;

import java.sql.Connection;
import java.time.LocalDateTime;

import com.ecommerce.App;
import com.ecommerce.dao.UserDAO;
import com.ecommerce.dao.impl.UserDAOImpl;
import com.ecommerce.models.User;
import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.utils.PasswordUtils;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML
    private TextField username;

    @FXML
    private TextField email;

    @FXML
    private PasswordField password;

    @FXML
    private PasswordField confirm_password;

    @FXML
    private Button sign_up;

    @FXML
    private Button backToLoginButton;

    private UserDAO userDAO;

    @FXML
    private void initialize() {
        try {
            Connection connection = DatabaseUtils.getConnection();
            this.userDAO = new UserDAOImpl(connection);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Koneksi database gagal.");
        }
    }

    @FXML
    private void handleRegister() {
        String usernameValue = username.getText().trim();
        String emailValue = email.getText().trim();
        String passwordValue = password.getText().trim();
        String confirmPasswordValue = confirm_password.getText().trim();

        // Validasi input
        if (usernameValue.isEmpty() || emailValue.isEmpty() || passwordValue.isEmpty() || confirmPasswordValue.isEmpty()) {
            showAlert("Register Gagal", "Semua field wajib diisi.");
            return;
        }

        if (!passwordValue.equals(confirmPasswordValue)) {
            showAlert("Register Gagal", "Password dan Confirm Password tidak cocok.");
            return;
        }

        if (!emailValue.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showAlert("Register Gagal", "Format email tidak valid.");
            return;
        }

        try {
            // Cek apakah email sudah terdaftar
            User existingUser = userDAO.getUserByEmail(emailValue);
            if (existingUser != null) {
                showAlert("Register Gagal", "Email sudah digunakan.");
                return;
            }

            // Enkripsi password
            String hashedPassword = PasswordUtils.encrypt(passwordValue);

            // Buat user baru
            User newUser = new User();
            newUser.setUsername(usernameValue);
            newUser.setEmail(emailValue);
            newUser.setPassword(hashedPassword);
            newUser.setRole(User.Role.CUSTOMER);
            newUser.setCreatedAt(LocalDateTime.now());
            newUser.setUpdatedAt(LocalDateTime.now());

            // Simpan ke database
            userDAO.addUser(newUser);
            showAlert("Register Berhasil", "Akun berhasil dibuat. Silakan login.");

            // Navigasi ke halaman login tanpa mengganti scene
            App.mainLayoutController.loadContent("/com/ecommerce/shared/LoginView.fxml");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan pada proses registrasi.");
        }
    }

    @FXML
    private void handleBackToLogin() {
        try {
            // Navigasi ke halaman login dalam MainLayout
            App.mainLayoutController.loadContent("/com/ecommerce/shared/LoginView.fxml");
            System.out.println("[INFO] Berhasil kembali ke halaman login.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Tidak dapat membuka halaman login.");
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
