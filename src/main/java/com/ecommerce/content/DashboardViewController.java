package com.ecommerce.content;

import com.ecommerce.App;
import com.ecommerce.layouts.MainLayoutController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

public class DashboardViewController {

    @FXML
    private StackPane contentPane; // Kontainer untuk konten dinamis di Dashboard

    private MainLayoutController mainLayoutController; // Referensi ke MainLayoutController

    /**
     * Inisialisasi DashboardViewController.
     */
    @FXML
    public void initialize() {
        System.out.println("[INFO] DashboardViewController diinisialisasi.");
    }

    /**
     * Hubungkan DashboardViewController dengan MainLayoutController.
     *
     * @param mainLayoutController Referensi ke MainLayoutController.
     */
    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        System.out.println("[INFO] MainLayoutController terhubung dengan DashboardViewController.");
    }

    /**
     * Buka halaman baru di konten Dashboard.
     *
     * @param page Path FXML halaman yang akan dibuka.
     */
    public void openPage(String page) {
        if (contentPane != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(page));
                StackPane newPage = loader.load();
                contentPane.getChildren().clear(); // Bersihkan konten lama
                contentPane.getChildren().add(newPage); // Tambahkan konten baru
                System.out.println("[INFO] Halaman dibuka: " + page);
            } catch (Exception e) {
                System.err.println("[ERROR] Gagal membuka halaman: " + page);
                e.printStackTrace();
            }
        } else {
            System.err.println("[ERROR] contentPane tidak ditemukan.");
        }
    }

    /**
     * Pencarian menggunakan kata kunci.
     *
     * @param query Kata kunci pencarian.
     */
    public void search(String query) {
        System.out.println("[INFO] Pencarian dengan kata kunci: " + query);

        // Logika tambahan untuk pencarian bisa ditambahkan di sini
        if (query != null && !query.isEmpty()) {
            System.out.println("[INFO] Memproses pencarian untuk: " + query);
            openPage("/com/ecommerce/search/SearchResults.fxml");
        } else {
            System.err.println("[WARN] Kata kunci pencarian kosong.");
        }
    }
}
