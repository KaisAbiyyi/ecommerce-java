package com.ecommerce.components;

import com.ecommerce.App;
import com.ecommerce.utils.DatabaseUtils;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDateTime;
import java.util.List;

public class OrderCard extends VBox {

    private static final double ID_WIDTH = 100.0;
    private static final double STATUS_WIDTH = 150.0;
    private static final double CREATED_AT_WIDTH = 200.0;
    private static final double TOTAL_ITEMS_WIDTH = 100.0;
    private static final double ACTION_WIDTH = 200.0;

    private int orderId;
    private String currentStatus;

    public OrderCard(int orderId, String status, LocalDateTime createdAt, int totalItems, List<OrderItem> items) {
        super(10); // Spacing antar elemen
        this.orderId = orderId;
        this.currentStatus = status; // Simpan status saat ini
        this.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f9f9f9;");

        // Header: Order Information
        HBox orderInfo = new HBox(10); // Spacing antar kolom
        orderInfo.setStyle("-fx-padding: 10;");

        // Order ID
        VBox idBox = createVBox("Order ID:", new Label(String.valueOf(orderId)), ID_WIDTH);

        // Status
        VBox statusBox = createVBox("Status:", new Label(status), STATUS_WIDTH);

        // Created At
        VBox createdAtBox = createVBox("Created At:", new Label(createdAt.toString()), CREATED_AT_WIDTH);

        // Total Items
        VBox totalItemsBox = createVBox("Total Items:", new Label(String.valueOf(totalItems)), TOTAL_ITEMS_WIDTH);

        // Action Box
        VBox actionBox = createActionBox(ACTION_WIDTH);

        // Tambahkan semua ke HBox
        orderInfo.getChildren().addAll(idBox, statusBox, createdAtBox, totalItemsBox, actionBox);

        // Item List: Daftar produk di dalam pesanan
        VBox itemList = new VBox(5); // Spacing antar item
        itemList.setStyle("-fx-padding: 10; -fx-background-color: #f0f0f0;");

        Label itemListLabel = new Label("Order Items:");
        itemListLabel.setStyle("-fx-font-weight: bold; -fx-padding: 5;");
        itemList.getChildren().add(itemListLabel);

        // Tambahkan setiap item ke itemList
        for (OrderItem item : items) {
            HBox itemBox = new HBox(10); // Spacing antar elemen item
            itemBox.setStyle("-fx-padding: 5;");

            Label productName = new Label("Product: " + item.getProductName());
            Label quantity = new Label("Quantity: " + item.getQuantity());
            Label price = new Label("Price: $" + item.getPrice());

            itemBox.getChildren().addAll(productName, quantity, price);
            itemList.getChildren().add(itemBox);
        }

        // Tambahkan Header dan Item List ke OrderCard
        this.getChildren().addAll(orderInfo, itemList);
    }

    private VBox createVBox(String labelText, Label valueLabel, double width) {
        VBox box = new VBox();
        box.setPrefWidth(width);
        box.setStyle("-fx-padding: 5;");
        Label label = new Label(labelText);
        valueLabel.setWrapText(true);
        box.getChildren().addAll(label, valueLabel);
        return box;
    }

    private VBox createActionBox(double width) {
        VBox box = new VBox();
        box.setPrefWidth(width);
        box.setStyle("-fx-padding: 5;");
        Label label = new Label("Actions:");

        if (currentStatus.equals("COMPLETED")) {
            // Tampilkan teks saja jika status COMPLETED
            Label completedLabel = new Label("Status: COMPLETED");
            box.getChildren().addAll(label, completedLabel);
        } else {
            // ComboBox untuk mengubah status
            ComboBox<String> statusComboBox = new ComboBox<>();
            statusComboBox.getItems().addAll("PENDING", "SHIPPED", "COMPLETED");
            statusComboBox.setValue(currentStatus);

            // Disable opsi yang tidak valid berdasarkan status saat ini
            statusComboBox.getItems().removeIf(status ->
                    (currentStatus.equals("SHIPPED") && status.equals("PENDING")) ||
                            (currentStatus.equals("COMPLETED") && !status.equals("COMPLETED"))
            );

            // Event handler untuk perubahan status
            statusComboBox.setOnAction(event -> {
                String newStatus = statusComboBox.getValue();
                handleStatusChange(newStatus);
            });

            box.getChildren().addAll(label, statusComboBox);
        }

        return box;
    }

    private void handleStatusChange(String newStatus) {
        if (!newStatus.equals(currentStatus)) {
            System.out.println("[INFO] Mengubah status order ID " + orderId + " dari " + currentStatus + " menjadi " + newStatus);
            currentStatus = newStatus;

            // Simpan perubahan status ke database
            updateStatusInDatabase(newStatus);

            // Reload halaman setelah status diperbarui
            reloadOrdersView();
        }
    }

    private void updateStatusInDatabase(String newStatus) {
        try (var connection = DatabaseUtils.getConnection()) {
            String updateQuery = "UPDATE order_sellers SET status = ? WHERE id = ?";
            try (var preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newStatus);
                preparedStatement.setInt(2, orderId);
                preparedStatement.executeUpdate();
                System.out.println("[INFO] Status berhasil diperbarui di database.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memperbarui status di database:");
            e.printStackTrace();
        }
    }

    private void reloadOrdersView() {
        try {
            App.mainLayoutController.loadContent("/com/ecommerce/content/seller/OrdersView.fxml");
            System.out.println("[INFO] Halaman OrdersView berhasil dimuat ulang.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat ulang halaman OrdersView:");
            e.printStackTrace();
        }
    }

    // Inner Class untuk Item di dalam Order
    public static class OrderItem {
        private final String productName;
        private final int quantity;
        private final double price;

        public OrderItem(String productName, int quantity, double price) {
            this.productName = productName;
            this.quantity = quantity;
            this.price = price;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantity() {
            return quantity;
        }

        public double getPrice() {
            return price;
        }
    }
}
