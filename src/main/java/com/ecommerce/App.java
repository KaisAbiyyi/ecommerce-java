package com.ecommerce;

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
        showLoginView();
    }

    // Method untuk menampilkan tampilan login
    public void showLoginView() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/ecommerce/shared/LoginView.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 400, 300));
        primaryStage.show();
    }

    // Method untuk menampilkan tampilan sesuai dengan role pengguna
    public static void showMainView(String role) throws Exception {
        String viewPath;

        switch (role.toLowerCase()) {
            case "admin":
                viewPath = "/com/ecommerce/admin/ManageUsersView.fxml";
                primaryStage.setTitle("Admin Dashboard");
                break;
            case "customer":
                viewPath = "/com/ecommerce/customer/ProductView.fxml";
                primaryStage.setTitle("Customer Dashboard");
                break;
            case "seller":
                viewPath = "/com/ecommerce/seller/ManageProductsView.fxml";
                primaryStage.setTitle("Seller Dashboard");
                break;
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }

        Parent root = FXMLLoader.load(App.class.getResource(viewPath));
        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
