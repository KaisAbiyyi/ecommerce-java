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

    public void showLoginView() throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/com/ecommerce/shared/LoginView.fxml"));
        primaryStage.setTitle("Login");
        primaryStage.setScene(new Scene(root, 800, 600)); // Sesuaikan ukuran
        primaryStage.show();
    }

    public static void showMainView(String role) throws Exception {
        String viewPath;

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
        primaryStage.setScene(new Scene(root, 1024, 768)); // Disesuaikan untuk ukuran dashboard
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
