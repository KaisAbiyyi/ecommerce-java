package com.ecommerce.shared;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.fxml.FXMLLoader;

public class CustomerLayoutController {

    @FXML
    private StackPane contentPane; // Area untuk konten dinamis

    /**
     * Memuat halaman ke dalam area konten.
     *
     * @param pagePath Path ke file FXML halaman yang akan dimuat.
     */
    public void setPage(String pagePath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(pagePath));
            StackPane page = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(page);
            System.out.println("Halaman berhasil dimuat: " + pagePath);
        } catch (Exception e) {
            System.err.println("Gagal memuat halaman: " + pagePath);
            e.printStackTrace();
        }
    }
}
