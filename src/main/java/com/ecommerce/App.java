package com.ecommerce;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.dao.impl.UserDAOImpl;
import com.ecommerce.models.User;
import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.utils.LocalStorageUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.sql.Connection;

public class App extends Application {

    private static Stage primaryStage;
    private static User loggedInUser; // Pengguna yang sedang login

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Atur ukuran default aplikasi
        primaryStage.setWidth(1440);
        primaryStage.setHeight(1024);

        // Cek token dan role di local storage
        String token = LocalStorageUtils.get("authToken");
        String role = LocalStorageUtils.get("userRole");
        String userId = LocalStorageUtils.get("userId");

        if (isValidSession(token, role, userId)) {
            loggedInUser = getUserById(Integer.parseInt(userId));
            if (loggedInUser != null) {
                showMainView(role);
            } else {
                System.out.println("Pengguna tidak ditemukan, kembali ke halaman login.");
                showLoginView();
            }
        } else {
            System.out.println("Session tidak valid, kembali ke halaman login.");
            showLoginView();
        }
    }

    private boolean isValidSession(String token, String role, String userId) {
        return token != null && !token.isEmpty()
                && role != null && !role.isEmpty()
                && userId != null && userId.matches("\\d+"); // Pastikan userId adalah angka
    }

    public void showLoginView() {
        System.out.println("Menampilkan halaman login...");
        loadView("/com/ecommerce/shared/LoginView.fxml", "Login");
    }

    public static void showMainView(String role) {
        System.out.println("Menampilkan halaman utama untuk role: " + role);
        String viewPath;
        String title;

        switch (role.toUpperCase()) {
            case "ADMIN":
                viewPath = "/com/ecommerce/admin/ManageUsersView.fxml";
                title = "Admin Dashboard";
                break;
            case "CUSTOMER":
                viewPath = "/com/ecommerce/shared/DashboardView.fxml";
                title = "Customer Dashboard";
                break;
            case "SELLER":
                viewPath = "/com/ecommerce/seller/ManageProductsView.fxml";
                title = "Seller Dashboard";
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        loadView(viewPath, title);
    }


    private static void loadView(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(App.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            scene.getStylesheets().add(App.class.getResource("/application.css").toExternalForm());

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("Gagal memuat view: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static void logout() {
        try {
            // Hapus data lokal
            LocalStorageUtils.remove("authToken");
            LocalStorageUtils.remove("userRole");
            LocalStorageUtils.remove("userId");

            loggedInUser = null; // Reset pengguna yang sedang login

            // Tampilkan halaman login
            primaryStage.close();
            new App().start(primaryStage);
        } catch (Exception e) {
            System.err.println("Error saat logout: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static User getLoggedInUser() {
        if (loggedInUser != null) {
            System.out.println("Pengguna yang sedang login: " + loggedInUser.getUsername());
        } else {
            System.out.println("Tidak ada pengguna yang login.");
        }
        return loggedInUser;
    }

    private User getUserById(int userId) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            UserDAO userDAO = new UserDAOImpl(connection);
            User user = userDAO.getUserById(userId);
            if (user != null) {
                System.out.println("Pengguna ditemukan: " + user.getUsername());
            } else {
                System.out.println("Pengguna dengan ID " + userId + " tidak ditemukan.");
            }
            return user;
        } catch (Exception e) {
            System.err.println("Error saat mengambil pengguna dengan ID: " + userId);
            e.printStackTrace();
            return null;
        }
    }


    public static void main(String[] args) {
        launch(args);
    }
}
