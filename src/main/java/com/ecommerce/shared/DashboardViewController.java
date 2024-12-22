package com.ecommerce.shared;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class DashboardViewController {

    @FXML
    private StackPane profileBar; // Sesuaikan tipe dengan elemen di FXML

    @FXML
    private NavbarController navbarController; // Controller untuk Navbar

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setDashboardController(this);
        } else {
            System.out.println("NavbarController belum diatur!");
        }
    }

    // Metode untuk toggle ProfileBar
    public void toggleProfileBar() {
        if (profileBar != null) {
            boolean isVisible = profileBar.isVisible();
            profileBar.setVisible(!isVisible); // Toggle visibilitas
            profileBar.setManaged(!isVisible); // Pastikan layout diperbarui
            System.out.println("ProfileBar visibility toggled: " + !isVisible);
        } else {
            System.out.println("ProfileBar tidak ditemukan!");
        }
    }
}
