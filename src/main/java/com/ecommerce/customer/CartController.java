package com.ecommerce.customer;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

public class CartController {

    @FXML
    private ListView<String> cartList; // ListView untuk menampilkan item di keranjang

    @FXML
    private Label totalPriceLabel; // Label untuk menampilkan total harga

    @FXML
    private Button checkoutButton; // Tombol untuk melakukan checkout

    @FXML
    private Button backButton; // Tombol untuk kembali ke halaman dashboard

    private final ObservableList<String> cartItems = FXCollections.observableArrayList(); // Data item di keranjang
    private double totalPrice = 0.0; // Total harga

    /**
     * Inisialisasi controller saat file FXML dimuat.
     */
    @FXML
    public void initialize() {
        // Hubungkan data keranjang ke ListView
        cartList.setItems(cartItems);

        // Contoh data awal (bisa dihapus dan diganti dengan data dinamis)
        addItemToCart("Samsung Galaxy S21", 800.00);
        addItemToCart("Apple MacBook Pro 14-inch", 2000.00);

        // Hitung total harga awal
        updateTotalPrice();

        // Tambahkan event handler ke tombol checkout
        checkoutButton.setOnAction(event -> handleCheckout());

        // Tambahkan event handler ke tombol kembali
        backButton.setOnAction(event -> handleBack());
    }

    /**
     * Tambahkan item ke keranjang.
     *
     * @param item  Nama item.
     * @param price Harga item.
     */
    public void addItemToCart(String item, double price) {
        cartItems.add(item + " - Rp" + price);
        totalPrice += price;
        updateTotalPrice();
    }

    /**
     * Hitung ulang total harga.
     */
    private void updateTotalPrice() {
        totalPriceLabel.setText("Rp" + String.format("%.2f", totalPrice));
    }

    /**
     * Proses checkout: mencetak item, dan menghapus semua item dari keranjang.
     */
    private void handleCheckout() {
        if (cartItems.isEmpty()) {
            System.out.println("[INFO] Keranjang kosong. Tidak ada yang bisa di-checkout.");
            return;
        }

        System.out.println("[INFO] Melakukan checkout untuk item berikut:");
        cartItems.forEach(item -> System.out.println("- " + item));

        // Kosongkan keranjang setelah checkout
        cartItems.clear();
        totalPrice = 0.0;
        updateTotalPrice();

        System.out.println("[INFO] Keranjang telah dikosongkan setelah checkout.");
    }

    /**
     * Tangani tombol kembali ke dashboard.
     */
    private void handleBack() {
        System.out.println("[INFO] Kembali ke halaman dashboard.");
        // Navigasi kembali ke dashboard (misalnya menggunakan MainLayoutController)
        // Implementasi navigasi disesuaikan dengan struktur aplikasi Anda
    }
}
