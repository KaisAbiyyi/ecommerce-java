package com.ecommerce.layouts;

import com.ecommerce.customer.CartController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainLayoutController {

    @FXML
    private StackPane contentPane; // Tempat untuk memuat konten halaman

    private Object currentController; // Referensi controller halaman saat ini

    private CartController cartController; // Referensi ke CartController

    @FXML
    private NavbarController navbarController; // Referensi ke NavbarController

    /**
     * Inisialisasi MainLayoutController.
     */
    @FXML
    public void initialize() {
        System.out.println("[INFO] MainLayoutController diinisialisasi.");

        // Preload CartView untuk memastikan CartController siap digunakan
        try {
            System.out.println("[DEBUG] Preloading CartView...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/customer/CartView.fxml"));
            loader.load();
            cartController = loader.getController();
            System.out.println("[INFO] CartController berhasil di-preload.");
        } catch (IOException e) {
            System.err.println("[ERROR] Gagal preload CartView:");
            e.printStackTrace();
        }

        // Hubungkan NavbarController dengan MainLayoutController
        if (navbarController != null) {
            navbarController.setMainLayoutController(this);
            System.out.println("[INFO] NavbarController berhasil dihubungkan.");
        } else {
            System.err.println("[WARN] NavbarController belum diatur. Pastikan dihubungkan melalui FXML.");
        }
    }

    /**
     * Memuat halaman konten dinamis ke dalam contentPane.
     *
     * @param fxmlPath Path file FXML yang ingin dimuat.
     */
    public void loadContent(String fxmlPath) {
        try {
            System.out.println("[DEBUG] Memulai proses memuat halaman: " + fxmlPath);

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent content = loader.load();

            // Simpan referensi controller halaman yang baru dimuat
            currentController = loader.getController();

            // Jika halaman yang dimuat adalah CartView, simpan referensi ke CartController
            if (currentController instanceof CartController) {
                cartController = (CartController) currentController;
                System.out.println("[INFO] CartController berhasil diatur.");
            }

            // Jika halaman yang dimuat adalah ProductDetailView, set MainLayoutController
            if (currentController instanceof com.ecommerce.content.ProductDetailViewController) {
                ((com.ecommerce.content.ProductDetailViewController) currentController).setMainLayoutController(this);
                System.out.println("[INFO] MainLayoutController berhasil diatur pada ProductDetailViewController.");
            }

            // Bersihkan konten lama dan tambahkan konten baru
            contentPane.getChildren().clear();
            contentPane.getChildren().add(content);

            System.out.println("[INFO] Halaman konten berhasil dimuat: " + fxmlPath);
        } catch (IOException e) {
            System.err.println("[ERROR] File FXML tidak ditemukan atau tidak dapat dimuat: " + fxmlPath);
            e.printStackTrace();
        }
    }

    /**
     * Mengembalikan controller halaman yang sedang aktif.
     *
     * @return Referensi ke controller halaman aktif.
     */
    public Object getCurrentController() {
        return currentController;
    }

    /**
     * Mengembalikan referensi ke NavbarController.
     *
     * @return Referensi ke NavbarController.
     */
    public NavbarController getNavbarController() {
        if (navbarController != null) {
            return navbarController;
        } else {
            System.err.println("[ERROR] NavbarController belum diinisialisasi. Periksa pengaturan di FXML.");
            return null;
        }
    }

    /**
     * Mengembalikan referensi ke CartController.
     *
     * @return Referensi ke CartController.
     */
    public CartController getCartController() {
        if (cartController != null) {
            System.out.println("[INFO] Mengembalikan referensi ke CartController.");
        } else {
            System.err.println("[WARN] CartController belum diinisialisasi.");
        }
        return cartController;
    }
}
