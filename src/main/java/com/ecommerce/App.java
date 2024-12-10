package com.ecommerce;

import com.ecommerce.utils.LocalStorageUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        // Mulai aplikasi dengan ukuran default 1920x1080
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

        // Cek token di local storage
        String token = LocalStorageUtils.get("authToken");
        String role = LocalStorageUtils.get("userRole");

        if (token != null && !token.isEmpty() && role != null) {
            // Jika token valid, langsung ke dashboard
            showMainView(role);
        } else {
            // Jika tidak ada token, tampilkan halaman login
            showLoginView();
        }
    }

    public void showLoginView() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/ecommerce/shared/LoginView.fxml"));
        Scene scene = new Scene(root);

        // Tambahkan stylesheet (opsional)
        scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void showMainView(String role) throws Exception {
        String viewPath;

        // Tentukan view berdasarkan role pengguna
        switch (role.toUpperCase()) {
            case "ADMIN":
                viewPath = "/com/ecommerce/admin/ManageUsersView.fxml";
                primaryStage.setTitle("Admin Dashboard");
                break;
            case "CUSTOMER":
                viewPath = "/com/ecommerce/customer/ProductView.fxml";
                primaryStage.setTitle("Customer Dashboard");
                break;
            case "SELLER":
                viewPath = "/com/ecommerce/seller/ManageProductsView.fxml";
                primaryStage.setTitle("Seller Dashboard");
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        Parent root = FXMLLoader.load(App.class.getResource(viewPath));
        Scene scene = new Scene(root);

        // Tambahkan stylesheet (opsional)
        scene.getStylesheets().add(App.class.getResource("/application.css").toExternalForm());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void logout() throws Exception {
        // Hapus token dari local storage
        LocalStorageUtils.remove("authToken");
        LocalStorageUtils.remove("userRole");
        LocalStorageUtils.remove("userId");

        // Tampilkan halaman login
        primaryStage.close();
        new App().start(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
