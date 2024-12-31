package com.ecommerce;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.dao.impl.UserDAOImpl;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.models.User;
import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.utils.LocalStorageUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;

public class App extends Application {

    private static Stage primaryStage;
    private static User loggedInUser;
    public static MainLayoutController mainLayoutController;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;
        primaryStage.setWidth(1440);
        primaryStage.setHeight(1024);

        initializeMainLayout();

        String token = LocalStorageUtils.get("authToken");
        String role = LocalStorageUtils.get("userRole");
        String userId = LocalStorageUtils.get("userId");

        System.out.println("[DEBUG] authToken: " + token);
        System.out.println("[DEBUG] userRole: " + role);
        System.out.println("[DEBUG] userId: " + userId);

        boolean isLoggedIn = isValidSession(token, role, userId);

        // Cek status login dan tentukan halaman yang akan dimuat
        if (isLoggedIn) {
            loggedInUser = getUserById(Integer.parseInt(userId));
            if (loggedInUser != null) {
                mainLayoutController.loadContent(getDashboardPath(role)); // Tampilkan dashboard sesuai role
            } else {
                isLoggedIn = false; // User tidak ditemukan, anggap belum login
                mainLayoutController.loadContent("/com/ecommerce/content/DashboardView.fxml"); // Tampilkan dashboard default
            }
        } else {
            mainLayoutController.loadContent("/com/ecommerce/content/DashboardView.fxml"); // Tampilkan dashboard default
        }

        // Update tombol auth di NavbarController
        if (mainLayoutController != null && mainLayoutController.getNavbarController() != null) {
            NavbarController navbarController = mainLayoutController.getNavbarController();
            navbarController.updateAuthButtonState(isLoggedIn); // Kirim status login ke tombol auth
        } else {
            System.err.println("[WARN] NavbarController belum diinisialisasi.");
        }
    }



    private static String getDashboardPath(String role) {
        switch (role.toUpperCase()) {
            case "ADMIN":
                return "/com/ecommerce/admin/ManageUsersView.fxml";
            case "CUSTOMER":
                return "/com/ecommerce/content/DashboardView.fxml";
            case "SELLER":
                return "/com/ecommerce/seller/ManageProductsView.fxml";
            default:
                System.err.println("[ERROR] Role tidak dikenali: " + role);
                return "/com/ecommerce/shared/LoginView.fxml";
        }
    }

    private void initializeMainLayout() {
        try {
            // Cek apakah mainLayoutController sudah diinisialisasi
            if (mainLayoutController != null) {
                System.out.println("[INFO] MainLayoutController sudah diinisialisasi. Menggunakan instance yang ada.");
                return;
            }

            System.out.println("[DEBUG] Memulai inisialisasi MainLayout...");

            // Muat file FXML MainLayout
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/layouts/MainLayout.fxml"));
            AnchorPane root = loader.load();

            // Ambil controller dari FXML
            mainLayoutController = loader.getController();
            if (mainLayoutController == null) {
                throw new IllegalStateException("[ERROR] MainLayoutController tidak dapat diinisialisasi. Pastikan FXML memiliki controller yang benar.");
            }
            System.out.println("[DEBUG] MainLayoutController berhasil diambil.");

            // Buat Scene baru dan tambahkan stylesheet jika tersedia
            Scene scene = new Scene(root);
            String stylesheetPath = "/application.css";
            if (getClass().getResource(stylesheetPath) != null) {
                scene.getStylesheets().add(getClass().getResource(stylesheetPath).toExternalForm());
                System.out.println("[DEBUG] Stylesheet berhasil dimuat: " + stylesheetPath);
            } else {
                System.err.println("[WARN] Stylesheet tidak ditemukan di path: " + stylesheetPath);
            }

            // Set scene ke primaryStage
            primaryStage.setScene(scene);
            primaryStage.setTitle("E-Commerce App");
            primaryStage.show();
            System.out.println("[INFO] MainLayout berhasil diinisialisasi dan ditampilkan.");
        } catch (IllegalStateException e) {
            System.err.println("[ERROR] Terjadi masalah dengan struktur atau controller MainLayout.");
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("[ERROR] File FXML MainLayout.fxml tidak ditemukan atau tidak dapat dimuat.");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal menginisialisasi MainLayout karena kesalahan tidak terduga.");
            e.printStackTrace();
        }
    }


    private boolean isValidSession(String token, String role, String userId) {
        if (token == null || token.isEmpty()) {
            System.out.println("[ERROR] Token tidak valid.");
            return false;
        }
        if (role == null || role.isEmpty()) {
            System.out.println("[ERROR] Role tidak valid.");
            return false;
        }
        if (userId == null || !userId.matches("\\d+")) {
            System.out.println("[ERROR] User ID tidak valid.");
            return false;
        }
        return true;
    }

    public void openLoginPage() {
        try {
            System.out.println("[INFO] Membuka halaman login.");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/shared/LoginView.fxml"));
            AnchorPane loginRoot = loader.load();

            Scene loginScene = new Scene(loginRoot);
            primaryStage.setScene(loginScene);
            primaryStage.setTitle("Login - E-Commerce App");
            primaryStage.show();

            System.out.println("[INFO] Halaman login berhasil dimuat.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat halaman login.");
            e.printStackTrace();
        }
    }

    public static void openMainPage(String role) {
        try {
            if (mainLayoutController != null) {
                String contentPath = getDashboardPath(role);
                mainLayoutController.loadContent(contentPath);
                System.out.println("[INFO] Main page berhasil dimuat untuk role: " + role);
            } else {
                throw new IllegalStateException("[ERROR] MainLayoutController tidak ditemukan.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kesalahan saat membuka Main Page: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private User getUserById(int userId) {
        System.out.println("[DEBUG] Mencoba mendapatkan user dengan ID: " + userId);
        try (Connection connection = DatabaseUtils.getConnection()) {
            UserDAO userDAO = new UserDAOImpl(connection);
            User user = userDAO.getUserById(userId);
            if (user != null) {
                System.out.println("[INFO] User ditemukan: " + user.getUsername());
                return user;
            } else {
                System.out.println("[WARN] User dengan ID " + userId + " tidak ditemukan di database.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error mengambil data user dengan ID: " + userId);
            e.printStackTrace();
        }
        return null;
    }

    public static void logout() {
        try {
            LocalStorageUtils.remove("authToken");
            LocalStorageUtils.remove("userRole");
            LocalStorageUtils.remove("userId");

            loggedInUser = null;
            System.out.println("[INFO] Logout berhasil. Menampilkan halaman login.");
            if (mainLayoutController != null) {
                mainLayoutController.loadContent("/com/ecommerce/shared/LoginView.fxml");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Error logout: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void setLoggedInUser(User user) {
        try {
            if (user != null) {
                loggedInUser = user;
                System.out.println("[INFO] User login berhasil diatur: " + user.getUsername() + " (" + user.getEmail() + ")");

                LocalStorageUtils.set("authToken", "TOKEN_" + user.getId() + "_" + System.currentTimeMillis());
                LocalStorageUtils.set("userRole", user.getRole().name());
                LocalStorageUtils.set("userId", String.valueOf(user.getId()));
                System.out.println("[INFO] Detail user disimpan ke LocalStorage.");

                if (mainLayoutController != null) {
                    mainLayoutController.loadContent("/com/ecommerce/content/DashboardView.fxml");
                    System.out.println("[INFO] Dashboard berhasil dimuat setelah login.");
                } else {
                    System.err.println("[WARN] MainLayoutController belum diinisialisasi. Dashboard tidak diperbarui.");
                }
            } else {
                loggedInUser = null;
                LocalStorageUtils.remove("authToken");
                LocalStorageUtils.remove("userRole");
                LocalStorageUtils.remove("userId");
                System.out.println("[INFO] User login direset. Semua session detail dihapus.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kesalahan saat mengatur logged-in user:");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
