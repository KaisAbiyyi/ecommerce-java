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
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Button loginButton;

    private UserDAO userDAO;

    /**
     * Inisialisasi controller dan setup dependensi.
     */
    @FXML
    private void initialize() {
        setupDatabaseConnection();
        setupEventHandlers();
    }

    /**
     * Setup koneksi database.
     */
    private void setupDatabaseConnection() {
        try {
            Connection connection = DatabaseUtils.getConnection();
            this.userDAO = new UserDAOImpl(connection);
            System.out.println("[INFO] Database connection berhasil diinisialisasi.");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Database connection failed.");
        }
    }

    /**
     * Setup event handler untuk tombol login.
     */
    private void setupEventHandlers() {
        loginButton.setOnAction(event -> handleLogin());
    }

    /**
     * Proses login pengguna.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Login Gagal", "Email atau password tidak boleh kosong");
            return;
        }

        try {
            User user = userDAO.getUserByEmailAndPassword(email, password);
            if (user != null) {
                processSuccessfulLogin(user);
            } else {
                showAlert("Login Gagal", "Email atau password salah");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan saat login.");
        }
    }

    /**
     * Proses setelah login berhasil.
     *
     * @param user Pengguna yang berhasil login.
     */
    private void processSuccessfulLogin(User user) {
        try {
            String token = generateToken(user);
            App.setLoggedInUser(user);

            LocalStorageUtils.set("authToken", token);
            LocalStorageUtils.set("userRole", user.getRole().name());
            LocalStorageUtils.set("userId", String.valueOf(user.getId()));

            System.out.println("[INFO] Login berhasil. User: " + user.getUsername());

            showAlert("Login Berhasil", "Selamat datang, " + user.getUsername());
            navigateToDashboard(user.getRole().name());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Terjadi kesalahan saat memproses login.");
        }
    }

    /**
     * Generate token untuk sesi login.
     *
     * @param user Pengguna yang login.
     * @return Token yang dihasilkan.
     */
    private String generateToken(User user) {
        return "TOKEN_" + user.getId() + "_" + System.currentTimeMillis();
    }

    /**
     * Navigasi ke dashboard berdasarkan peran pengguna.
     *
     * @param role Peran pengguna.
     */
    private void navigateToDashboard(String role) {
        try {
            App.openMainPage(role);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Tidak dapat membuka dashboard.");
        }
    }

    /**
     * Menampilkan pesan alert.
     *
     * @param title   Judul alert.
     * @param content Isi pesan alert.
     */
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Aksi untuk membuka halaman register.
     */
    @FXML
    private void handleSignUpRedirect() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/shared/RegisterView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Register");
            System.out.println("[INFO] Berhasil membuka halaman register.");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Tidak dapat membuka halaman register.");
        }
    }
}
