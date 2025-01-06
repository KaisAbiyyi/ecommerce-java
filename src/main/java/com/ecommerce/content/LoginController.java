package com.ecommerce.content;

import java.sql.Connection;

import com.ecommerce.App;
import com.ecommerce.db.dao.UserDAO;
import com.ecommerce.db.dao.impl.UserDAOImpl;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.db.models.User;
import com.ecommerce.db.DatabaseUtils;
import com.ecommerce.utils.LocalStorageUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

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
            if (connection != null) {
                this.userDAO = new UserDAOImpl(connection);
                System.out.println("[INFO] Database connection berhasil diinisialisasi.");
            } else {
                System.err.println("[ERROR] Database connection null.");
                showAlert("Error", "Gagal menghubungkan ke database.");
            }
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
    /**
     * Proses login pengguna.
     */
    @FXML
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert("Login Gagal", "Email atau password tidak boleh kosong.");
            return;
        }

        if (userDAO == null) {
            showAlert("Error", "Database connection belum tersedia.");
            return;
        }

        try {
            User user = userDAO.getUserByEmailAndPassword(email, password);
            if (user != null) {
                processSuccessfulLogin(user);
            } else {
                showAlert("Login Gagal", "Email atau password salah.");
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
            System.out.println("[DEBUG] Token: " + token + ", Role: " + user.getRole().name());

            // Perbarui navbar setelah login
            updateNavbarAfterLogin();

            // Navigasi ke dashboard
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
            System.out.println("[INFO] Navigasi ke dashboard untuk role: " + role);

            // Pastikan MainLayoutController tersedia
            if (App.mainLayoutController == null) {
                throw new IllegalStateException("[ERROR] MainLayoutController tidak ditemukan.");
            }

            // Dapatkan path dashboard berdasarkan role
            String dashboardPath = App.getDashboardPath(role);

            // Muat konten dashboard
            App.mainLayoutController.loadContent(dashboardPath);

            // Tambahkan ke stack navigasi melalui NavbarController
            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(dashboardPath, null);
                System.out.println("[INFO] Halaman dashboard ditambahkan ke stack navigasi.");
            } else {
                System.err.println("[WARN] NavbarController belum diinisialisasi.");
            }

            System.out.println("[INFO] Dashboard berhasil dimuat untuk role: " + role);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Tidak dapat membuka dashboard.");
        }
    }


    private void updateNavbarAfterLogin() {
        if (App.mainLayoutController != null && App.mainLayoutController.getNavbarController() != null) {
            App.mainLayoutController.getNavbarController().updateAuthButtonState(true); // Set tombol Logout
            System.out.println("[INFO] Navbar diperbarui setelah login.");
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
            if (App.mainLayoutController != null) {
                App.mainLayoutController.loadContent("/com/ecommerce/content/RegisterView.fxml");
                System.out.println("[INFO] Berhasil membuka halaman register.");
            } else {
                System.err.println("[ERROR] MainLayoutController tidak ditemukan.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Tidak dapat membuka halaman register.");
        }
    }


}
