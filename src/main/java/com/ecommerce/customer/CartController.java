package com.ecommerce.customer;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.List;

public class CartController {

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label totalPriceLabel;

    @FXML
    private Button checkoutButton;

    private final List<CartItem> cartItems = new ArrayList<>();
    private double totalPrice = 0.0;

    @FXML
    public void initialize() {
        System.out.println("[INFO] CartController diinisialisasi.");
        updateTotalPrice();
    }

    public void addItemToCart(String productName, double price, int quantity) {
        // Cek apakah produk sudah ada di keranjang
        CartItem existingItem = cartItems.stream()
                .filter(item -> item.getName().equals(productName))
                .findFirst()
                .orElse(null);

        if (existingItem != null) {
            existingItem.increaseQuantity(quantity);
        } else {
            CartItem newItem = new CartItem(productName, price, quantity);
            cartItems.add(newItem);

            // Tambahkan elemen UI untuk item baru
            HBox cartItemCard = createCartItemCard(newItem);
            cartItemsContainer.getChildren().add(cartItemCard);
        }

        updateTotalPrice();
    }

    private HBox createCartItemCard(CartItem item) {
        HBox card = new HBox(10);
        card.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-border-radius: 10; -fx-padding: 10;");

        Label productNameLabel = new Label(item.getName());
        productNameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        Label priceLabel = new Label("Rp" + String.format("%,.0f", item.getPrice() * item.getQuantity()));
        priceLabel.setStyle("-fx-font-size: 14px;");

        Button decreaseButton = new Button("-");
        decreaseButton.setOnAction(e -> {
            item.decreaseQuantity();
            if (item.getQuantity() <= 0) {
                cartItems.remove(item);
                cartItemsContainer.getChildren().remove(card);
            }
            updateCartItemUI(item, priceLabel);
        });

        Label quantityLabel = new Label(String.valueOf(item.getQuantity()));

        Button increaseButton = new Button("+");
        increaseButton.setOnAction(e -> {
            item.increaseQuantity(1);
            updateCartItemUI(item, priceLabel);
        });

        Button removeButton = new Button("X");
        removeButton.setStyle("-fx-text-fill: red;");
        removeButton.setOnAction(e -> {
            cartItems.remove(item);
            cartItemsContainer.getChildren().remove(card);
            updateTotalPrice();
        });

        card.getChildren().addAll(productNameLabel, decreaseButton, quantityLabel, increaseButton, priceLabel, removeButton);
        return card;
    }

    private void updateCartItemUI(CartItem item, Label priceLabel) {
        priceLabel.setText("Rp" + String.format("%,.0f", item.getPrice() * item.getQuantity()));
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        totalPrice = cartItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        totalPriceLabel.setText("Rp" + String.format("%,.2f", totalPrice));
    }
}
