package com.ecommerce.content.customer;

import com.ecommerce.App;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.utils.DatabaseUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class CartController implements MainLayoutController.MainLayoutAware {

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button checkoutButton;

    @FXML
    private Button profileButton;

    @FXML
    private Button ordersButton;

    @FXML
    private Button cartButton;

    @FXML
    private Button refreshButton;

    private MainLayoutController mainLayoutController;
    private double totalPrice = 0.0;

    @Override
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController tidak diatur. Navigasi mungkin gagal.");
        } else {
            System.out.println("[INFO] MainLayoutController berhasil diatur ke CartController.");
        }
    }

    @FXML
    private void initialize() {
        System.out.println("[INFO] CartController diinisialisasi.");

        setupNavigationButtons();

        if (mainLayoutController == null) {
            System.err.println("[WARN] MainLayoutController belum diatur. Navigasi mungkin gagal.");
        }

        loadCartItems();

        // Tombol refresh
        refreshButton.setOnAction(event -> refreshCartView());

        // Tombol checkout
        checkoutButton.setOnAction(event -> navigateToCheckout());
    }

    private void setupNavigationButtons() {
        profileButton.setOnAction(event -> navigateTo("/com/ecommerce/content/ProfileView.fxml"));
        ordersButton.setOnAction(event -> navigateTo("/com/ecommerce/content/seller/OrdersView.fxml"));
        cartButton.setOnAction(event -> navigateTo("/com/ecommerce/content/customer/CartView.fxml"));
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

    private void loadCartItems() {
        cartItemsContainer.getChildren().clear();
        totalPrice = 0.0;

        try (Connection connection = DatabaseUtils.getConnection()) {
            // Query untuk mengambil data cart_items
            String query = """
                    SELECT 
                        cart_items.id AS cart_item_id,
                        products.name AS product_name,
                        cart_items.quantity AS quantity,
                        products.price AS price
                    FROM 
                        cart_items
                    JOIN 
                        products ON cart_items.product_id = products.id
                    JOIN 
                        cart_sellers ON cart_items.cart_seller_id = cart_sellers.id
                    WHERE 
                        cart_sellers.user_id = ?
                    """;

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, App.loggedInUser.getId());
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int cartItemId = resultSet.getInt("cart_item_id");
                String productName = resultSet.getString("product_name");
                int quantity = resultSet.getInt("quantity");
                double price = resultSet.getDouble("price");

                totalPrice += price * quantity;

                // Buat card untuk item cart secara manual
                HBox card = createCartItemCard(cartItemId, productName, quantity, price);
                cartItemsContainer.getChildren().add(card);
            }

            updateTotalPriceLabel();
            System.out.println("[INFO] Cart items berhasil dimuat.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat cart items dari database:");
            e.printStackTrace();
        }
    }

    private HBox createCartItemCard(int cartItemId, String productName, int quantity, double price) {
        HBox card = new HBox(10);
        card.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 10;");

        // Nama produk
        Label productNameLabel = new Label(productName);
        productNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // Harga total untuk item
        Label priceLabel = new Label("Rp" + String.format("%,.0f", price * quantity));
        priceLabel.setStyle("-fx-font-size: 14px;");

        // Tombol untuk mengurangi jumlah
        Button decreaseButton = new Button("-");
        decreaseButton.setOnAction(e -> {
            if (quantity > 1) {
                updateCartItemQuantity(cartItemId, quantity - 1);
                loadCartItems();
            } else {
                removeCartItem(cartItemId);
                loadCartItems();
            }
        });

        // Label jumlah
        Label quantityLabel = new Label(String.valueOf(quantity));

        // Tombol untuk menambah jumlah
        Button increaseButton = new Button("+");
        increaseButton.setOnAction(e -> {
            updateCartItemQuantity(cartItemId, quantity + 1);
            loadCartItems();
        });

        // Tombol untuk menghapus item
        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-text-fill: red;");
        removeButton.setOnAction(e -> {
            removeCartItem(cartItemId);
            loadCartItems();
        });

        card.getChildren().addAll(productNameLabel, decreaseButton, quantityLabel, increaseButton, priceLabel, removeButton);
        return card;
    }

    private void updateCartItemQuantity(int cartItemId, int newQuantity) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "UPDATE cart_items SET quantity = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, newQuantity);
            statement.setInt(2, cartItemId);
            statement.executeUpdate();
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memperbarui jumlah item di cart:");
            e.printStackTrace();
        }
    }

    private void removeCartItem(int cartItemId) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            String query = "DELETE FROM cart_items WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, cartItemId);
            statement.executeUpdate();
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal menghapus item dari cart:");
            e.printStackTrace();
        }
    }

    private void refreshCartView() {
        try {
            mainLayoutController.loadContent("/com/ecommerce/content/customer/CartView.fxml");
            System.out.println("[INFO] Halaman CartView berhasil dimuat ulang.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memuat ulang halaman CartView:");
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToCheckout() {
        if (App.loggedInUser == null) {
            System.err.println("[ERROR] Tidak ada pengguna yang sedang login. Tidak dapat melanjutkan.");
            return;
        }

        int userId = App.loggedInUser.getId();

        try (Connection connection = DatabaseUtils.getConnection()) {
            connection.setAutoCommit(false); // Mulai transaksi

            // Step 1: Hitung total harga dari cart
            String totalPriceQuery = """
            SELECT SUM(ci.quantity * p.price) AS total_price
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id
            JOIN cart_sellers cs ON ci.cart_seller_id = cs.id
            WHERE cs.user_id = ?
        """;
            double totalPrice = 0.0;

            try (PreparedStatement stmt = connection.prepareStatement(totalPriceQuery)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    totalPrice = rs.getDouble("total_price");
                }
            }

            if (totalPrice == 0.0) {
                System.err.println("[ERROR] Keranjang kosong. Tidak dapat melanjutkan checkout.");
                return;
            }

            // Step 2: Buat entri baru di tabel `orders`
            String insertOrderQuery = """
            INSERT INTO orders (user_id, total_price, status)
            VALUES (?, ?, 'PENDING')
        """;
            int orderId;

            try (PreparedStatement stmt = connection.prepareStatement(insertOrderQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, userId);
                stmt.setDouble(2, totalPrice);
                stmt.executeUpdate();

                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Gagal mendapatkan ID untuk order baru.");
                }
            }

            // Step 3: Pindahkan data dari `cart_sellers` ke `order_sellers`
            String selectCartSellersQuery = """
            SELECT id, seller_id FROM cart_sellers WHERE user_id = ?
        """;
            String insertOrderSellerQuery = """
            INSERT INTO order_sellers (order_id, seller_id, status)
            VALUES (?, ?, 'PENDING')
        """;

            Map<Integer, Integer> cartSellerToOrderSellerMap = new HashMap<>();

            try (PreparedStatement selectStmt = connection.prepareStatement(selectCartSellersQuery);
                 PreparedStatement insertStmt = connection.prepareStatement(insertOrderSellerQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {

                selectStmt.setInt(1, userId);
                ResultSet rs = selectStmt.executeQuery();

                while (rs.next()) {
                    int cartSellerId = rs.getInt("id");
                    int sellerId = rs.getInt("seller_id");

                    insertStmt.setInt(1, orderId);
                    insertStmt.setInt(2, sellerId);
                    insertStmt.executeUpdate();

                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int orderSellerId = generatedKeys.getInt(1);
                        cartSellerToOrderSellerMap.put(cartSellerId, orderSellerId);
                    }
                }
            }

            // Step 4: Pindahkan data dari `cart_items` ke `order_items`
            String selectCartItemsQuery = """
            SELECT ci.cart_seller_id, ci.product_id, ci.quantity, p.price
            FROM cart_items ci
            JOIN products p ON ci.product_id = p.id
            WHERE ci.cart_seller_id IN (?)
        """;
            String insertOrderItemQuery = """
            INSERT INTO order_items (order_seller_id, product_id, quantity, price)
            VALUES (?, ?, ?, ?)
        """;

            try (PreparedStatement selectStmt = connection.prepareStatement(selectCartItemsQuery);
                 PreparedStatement insertStmt = connection.prepareStatement(insertOrderItemQuery)) {

                for (int cartSellerId : cartSellerToOrderSellerMap.keySet()) {
                    selectStmt.setInt(1, cartSellerId);
                    ResultSet rs = selectStmt.executeQuery();

                    while (rs.next()) {
                        int productId = rs.getInt("product_id");
                        int quantity = rs.getInt("quantity");
                        double price = rs.getDouble("price");
                        int orderSellerId = cartSellerToOrderSellerMap.get(cartSellerId);

                        insertStmt.setInt(1, orderSellerId);
                        insertStmt.setInt(2, productId);
                        insertStmt.setInt(3, quantity);
                        insertStmt.setDouble(4, price);
                        insertStmt.addBatch();
                    }
                }
                insertStmt.executeBatch();
            }

            // Step 5: Hapus data dari `cart_sellers` dan `cart_items`
            String deleteCartSellersQuery = "DELETE FROM cart_sellers WHERE user_id = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteCartSellersQuery)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }

            connection.commit(); // Selesaikan transaksi
            System.out.println("[INFO] Checkout berhasil. Order ID: " + orderId);

            // Navigasi ke halaman CheckoutView
            String checkoutPath = "/com/ecommerce/content/customer/CheckoutView.fxml";
            NavbarController navbarController = App.mainLayoutController.getNavbarController();
            if (navbarController != null) {
                navbarController.addPageToStack(checkoutPath, Map.of("orderId", String.valueOf(orderId)));
            }
            App.mainLayoutController.loadContent(checkoutPath);

        } catch (Exception e) {
            System.err.println("[ERROR] Gagal menyelesaikan checkout:");
            e.printStackTrace();
        }
    }


    private void updateTotalPriceLabel() {
        totalPriceLabel.setText("Rp" + String.format("%,.2f", totalPrice));
    }
}
