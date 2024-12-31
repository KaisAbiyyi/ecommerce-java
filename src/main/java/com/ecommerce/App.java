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

        if (isValidSession(token, role, userId)) {
            loggedInUser = getUserById(Integer.parseInt(userId));
            if (loggedInUser != null) {
                openMainPage(role);
            } else {
                System.out.println("[WARN] Pengguna tidak ada. Buka login.");
                openLoginPage();
            }
        } else {
            System.out.println("[WARN] Session invalid. Buka login.");
            openLoginPage();
        }
    }

    private void initializeMainLayout() {
        try {
            // Hindari inisialisasi ulang jika sudah ada instance
            if (mainLayoutController != null) {
                System.out.println("[INFO] MainLayoutController sudah diinisialisasi. Menggunakan instance yang ada.");
                return;
            }

            System.out.println("[DEBUG] Memulai inisialisasi MainLayout...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/layouts/MainLayout.fxml"));
            AnchorPane root = loader.load();

            mainLayoutController = loader.getController();
            if (mainLayoutController == null) {
                throw new IllegalStateException("MainLayoutController tidak dapat diinisialisasi. Pastikan FXML memiliki controller yang benar.");
            }
            System.out.println("[DEBUG] MainLayoutController berhasil diambil.");

            Scene scene = new Scene(root);
            String stylesheetPath = "/application.css";

            if (getClass().getResource(stylesheetPath) != null) {
                scene.getStylesheets().add(getClass().getResource(stylesheetPath).toExternalForm());
                System.out.println("[DEBUG] Stylesheet berhasil dimuat: " + stylesheetPath);
            }

            primaryStage.setScene(scene);
            primaryStage.setTitle("E-Commerce App");
            primaryStage.show();
            System.out.println("[INFO] MainLayout berhasil diinisialisasi dan ditampilkan.");
        } catch (IllegalStateException e) {
            System.err.println("[ERROR] Terjadi masalah dengan struktur atau controller MainLayout.");
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
            // Gunakan instance MainLayoutController yang sudah ada
            if (mainLayoutController == null) {
                FXMLLoader loader = new FXMLLoader(App.class.getResource("/com/ecommerce/layouts/MainLayout.fxml"));
                AnchorPane root = loader.load();

                mainLayoutController = loader.getController();
                Scene mainScene = new Scene(root);
                primaryStage.setScene(mainScene);
                primaryStage.setTitle(role + " Dashboard - E-Commerce App");
            }

            // Pastikan role diatur dengan benar
            if (mainLayoutController != null) {
                String contentPath; // Path konten yang akan dimuat

                switch (role.toUpperCase()) {
                    case "ADMIN":
                        contentPath = "/com/ecommerce/admin/ManageUsersView.fxml";
                        break;
                    case "CUSTOMER":
                        contentPath = "/com/ecommerce/content/DashboardView.fxml";
                        break;
                    case "SELLER":
                        contentPath = "/com/ecommerce/seller/ManageProductsView.fxml";
                        break;
                    default:
                        System.err.println("[WARN] Peran tidak dikenali. Membuka login.");
                        new App().openLoginPage();
                        return;
                }

                // Tambahkan halaman ke stack navigasi melalui NavbarController
                if (mainLayoutController.getNavbarController() != null) {
                    NavbarController navbar = mainLayoutController.getNavbarController();
                    // Tambahkan ke stack navigasi dengan data tambahan (misalnya, role)
                    navbar.addPageToStack(contentPath, Map.of("role", role));
                }

                // Muat konten di MainLayout
                mainLayoutController.loadContent(contentPath);

                System.out.println("[INFO] Main page berhasil dibuka untuk peran: " + role);
            } else {
                throw new IllegalStateException("MainLayoutController tidak ditemukan.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal membuka main page.");
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
            System.out.println("[INFO] Logout berhasil. Restart aplikasi.");
            new App().start(primaryStage);
        } catch (Exception e) {
            System.err.println("[ERROR] Error logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static User getLoggedInUser() {
        if (loggedInUser != null) {
            System.out.println("User saat ini: " + loggedInUser.getUsername());
        } else {
            System.out.println("Tidak ada user login.");
        }
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        try {
            if (user != null) {
                // Set user yang login
                loggedInUser = user;
                System.out.println("[INFO] User login berhasil diatur: " + user.getUsername() + " (" + user.getEmail() + ")");

                // Simpan detail user ke LocalStorage
                LocalStorageUtils.set("authToken", "TOKEN_" + user.getId() + "_" + System.currentTimeMillis());
                LocalStorageUtils.set("userRole", user.getRole().name());
                LocalStorageUtils.set("userId", String.valueOf(user.getId()));
                System.out.println("[INFO] Detail user disimpan ke LocalStorage.");

                // Jika MainLayoutController tersedia, lakukan pembaruan UI
                if (mainLayoutController != null) {
                    try {
                        mainLayoutController.loadContent("/com/ecommerce/content/DashboardView.fxml");
                        System.out.println("[INFO] Dashboard berhasil dimuat setelah login.");
                    } catch (Exception e) {
                        System.err.println("[ERROR] Gagal memuat Dashboard setelah login.");
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("[WARN] MainLayoutController belum diinisialisasi. Dashboard tidak diperbarui.");
                }
            } else {
                // Reset logged-in user jika null
                loggedInUser = null;
                LocalStorageUtils.remove("authToken");
                LocalStorageUtils.remove("userRole");
                LocalStorageUtils.remove("userId");
                System.out.println("[INFO] User login direset. Semua session detail dihapus.");
            }
        } catch (Exception e) {
            // Tangani error saat mengatur logged-in user
            System.err.println("[ERROR] Terjadi kesalahan saat mengatur logged-in user:");
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
