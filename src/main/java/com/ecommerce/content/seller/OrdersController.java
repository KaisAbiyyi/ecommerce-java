package com.ecommerce.content.seller;

import com.ecommerce.App;
import com.ecommerce.components.OrderCard;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class OrdersController implements MainLayoutController.MainLayoutAware {

    @FXML
    private VBox orderContainer;

    @FXML
    private Button profileButton;

    @FXML
    private Button productsButton;

    @FXML
    private Button ordersButton;

    @FXML
    private Button refreshButton;

    private MainLayoutController mainLayoutController;

    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController tidak diatur. Navigasi mungkin gagal.");
        } else {
            System.out.println("[INFO] MainLayoutController berhasil diatur ke OrdersController.");
        }
    }

    private void refreshOrdersView() {
        try {
            // Gunakan MainLayoutController untuk memuat ulang halaman
            mainLayoutController.loadContent("/com/ecommerce/content/seller/OrdersView.fxml");
            System.out.println("[INFO] Halaman OrdersView berhasil dimuat ulang.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat ulang halaman OrdersView:");
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        System.out.println("[INFO] OrdersController diinisialisasi.");

        setupNavigationButtons();

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController belum diatur. Navigasi mungkin gagal.");
        }

        loadOrders();
        refreshButton.setOnAction(event -> refreshOrdersView());
    }

    private void setupNavigationButtons() {
        profileButton.setOnAction(event -> navigateTo("/com/ecommerce/content/ProfileView.fxml"));
        productsButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/ManageProductsView.fxml"));
        ordersButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/OrdersView.fxml"));
    }

    private void navigateTo(String path) {
        if (mainLayoutController == null) {
            System.err.println("[ERROR] MainLayoutController is not set.");
            return;
        }

        try {
            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(path, Map.of());
            }

            mainLayoutController.loadContent(path);
            System.out.println("[INFO] Navigasi ke " + path + " berhasil.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal navigasi ke " + path + ":");
            e.printStackTrace();
        }
    }

    private void loadOrders() {
        orderContainer.getChildren().clear();

        try (Connection connection = DatabaseUtils.getConnection()) {
            // Query untuk mengambil data dari order_sellers
            String orderQuery = """
                        SELECT 
                            order_sellers.id AS order_id, 
                            order_sellers.status AS status, 
                            order_sellers.created_at AS created_at, 
                            COUNT(order_items.id) AS total_items
                        FROM 
                            order_sellers
                        LEFT JOIN 
                            order_items ON order_sellers.id = order_items.order_seller_id
                        WHERE 
                            order_sellers.seller_id = ?
                        GROUP BY 
                            order_sellers.id
                        ORDER BY 
                            order_sellers.created_at DESC
                    """;

            // Query untuk mengambil data dari order_items berdasarkan order_seller_id
            String itemsQuery = """
                        SELECT 
                            products.name AS product_name, 
                            order_items.quantity AS quantity, 
                            order_items.price AS price
                        FROM 
                            order_items
                        JOIN 
                            products ON order_items.product_id = products.id
                        WHERE 
                            order_items.order_seller_id = ?
                    """;

            PreparedStatement orderStatement = connection.prepareStatement(orderQuery);
            orderStatement.setInt(1, App.loggedInUser.getId()); // Gunakan ID seller yang sedang login
            ResultSet orderResultSet = orderStatement.executeQuery();

            while (orderResultSet.next()) {
                int orderId = orderResultSet.getInt("order_id");
                String status = orderResultSet.getString("status");
                LocalDateTime createdAt = orderResultSet.getTimestamp("created_at").toLocalDateTime();
                int totalItems = orderResultSet.getInt("total_items");

                // Ambil data order_items untuk setiap order_seller
                PreparedStatement itemsStatement = connection.prepareStatement(itemsQuery);
                itemsStatement.setInt(1, orderId);
                ResultSet itemsResultSet = itemsStatement.executeQuery();

                List<OrderCard.OrderItem> items = new ArrayList<>();
                while (itemsResultSet.next()) {
                    String productName = itemsResultSet.getString("product_name");
                    int quantity = itemsResultSet.getInt("quantity");
                    double price = itemsResultSet.getDouble("price");

                    items.add(new OrderCard.OrderItem(productName, quantity, price));
                }

                // Tambahkan OrderCard ke orderContainer
                orderContainer.getChildren().add(new OrderCard(orderId, status, createdAt, totalItems, items));
            }

            System.out.println("[INFO] Pesanan berhasil dimuat.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat pesanan dari database:");
            e.printStackTrace();
        }
    }


    @FXML
    private void onRefreshClicked() {
        loadOrders();
    }
}
