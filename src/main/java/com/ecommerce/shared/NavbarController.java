package com.ecommerce.shared;

import javafx.fxml.FXML;

public class NavbarController {

    private DashboardViewController dashboardController;

    public void setDashboardController(DashboardViewController dashboardController) {
        this.dashboardController = dashboardController;
    }

    @FXML
    public void toggleProfileBar() {
        if (dashboardController != null) {
            dashboardController.toggleProfileBar();
            System.out.println("Tombol P ditekan.");
        } else {
            System.out.println("DashboardViewController belum diatur!");
        }
    }
}
