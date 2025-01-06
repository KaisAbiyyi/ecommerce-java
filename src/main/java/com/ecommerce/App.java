package com.ecommerce;

import com.ecommerce.db.dao.UserDAO;
import com.ecommerce.db.dao.impl.UserDAOImpl;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.db.models.User;
import com.ecommerce.db.DatabaseUtils;
import com.ecommerce.utils.LocalStorageUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;

public class App extends Application {

    public static Stage primaryStage;
    public static User loggedInUser;
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

        String initialPage;
        if (isLoggedIn) {
            loggedInUser = getUserById(Integer.parseInt(userId));
            if (loggedInUser != null) {
                initialPage = getDashboardPath(role); // Path dashboard sesuai role
            } else {
                isLoggedIn = false; // User tidak ditemukan, anggap belum login
                initialPage = "/com/ecommerce/content/DashboardView.fxml"; // Dashboard default
            }
        } else {
            initialPage = "/com/ecommerce/content/DashboardView.fxml"; // Dashboard default
        }

        // Load halaman awal ke MainLayout
        mainLayoutController.loadContent(initialPage);

        // Tambahkan halaman awal ke stack navigasi
        NavbarController navbarController = mainLayoutController.getNavbarController();
        if (navbarController != null) {
            navbarController.addPageToStack(initialPage, null);

            // Update tombol auth di NavbarController
            navbarController.updateAuthButtonState(isLoggedIn);

            // Set tombol role berdasarkan role user (jika user login)
            if (isLoggedIn && role != null) {
                navbarController.setUserRole(role); ; // Pastikan tombol role terlihat
                System.out.println("[INFO] Tombol role diatur sesuai dengan userRole: " + role);
            } else {
                navbarController.setUserRole(null);
            }
        } else {
            System.err.println("[WARN] NavbarController belum diinisialisasi.");
        }
    }



    public static String getDashboardPath(String role) {
        switch (role.toUpperCase()) {
            case "ADMIN":
                return "/com/ecommerce/content/admin/SellerApprovalView.fxml";
            case "CUSTOMER":
                return "/com/ecommerce/content/DashboardView.fxml";
            case "SELLER":
                return "/com/ecommerce/content/seller/ManageProductsView.fxml";
            default:
                System.err.println("[ERROR] Role tidak dikenali: " + role);
                return "/com/ecommerce/content/LoginView.fxml";
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
                mainLayoutController.loadContent("/com/ecommerce/content/LoginView.fxml");
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
                    NavbarController navbarController = mainLayoutController.getNavbarController();
                    if (navbarController != null) {
                        navbarController.setUserRole(user.getRole().name());
                    } else {
                        System.err.println("[WARN] NavbarController belum diinisialisasi.");
                    }
                    mainLayoutController.loadContent(getDashboardPath(user.getRole().name()));
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
