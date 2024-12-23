package com.ecommerce.shared;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;

public class DashboardViewController {

    @FXML
    private StackPane profileBar; // Kontainer untuk ProfileBar

    @FXML
    private NavbarController navbarController; // Controller untuk Navbar

    private ProfileBarController profileBarController; // Controller untuk ProfileBar

    @FXML
    public void initialize() {
        if (navbarController != null) {
            navbarController.setDashboardController(this);
        } else {
            System.out.println("NavbarController belum diatur!");
        }

        if (profileBar != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/ecommerce/shared/ProfileBar.fxml"));
                StackPane loadedProfileBar = loader.load(); // Muat ProfileBar dari FXML
                profileBar.getChildren().add(loadedProfileBar); // Tambahkan ke StackPane
                profileBarController = loader.getController();
                System.out.println("ProfileBarController berhasil dihubungkan.");

            } catch (Exception e) {
                System.out.println("Gagal memuat ProfileBarController: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            System.out.println("ProfileBar tidak ditemukan!");
        }
    }


    // Metode untuk toggle ProfileBar
    @FXML
    public void toggleProfileBar() {
        if (profileBar != null) {
            boolean isVisible = profileBar.isVisible();
            profileBar.setVisible(!isVisible);
            profileBar.setManaged(!isVisible);
            System.out.println("ProfileBar visibility toggled: " + !isVisible);
        } else {
            System.out.println("ProfileBar tidak ditemukan!");
        }
    }


}
