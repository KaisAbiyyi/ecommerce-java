package com.ecommerce.shared;

import com.ecommerce.App;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;

public class ProfileBarController {

    @FXML
    private StackPane profileBar; // Ubah dari AnchorPane ke StackPane

    // Tombol Close
    @FXML
    public void closeProfileBar() {
        if (profileBar != null) {
            profileBar.setVisible(false);
            profileBar.setManaged(false);
        }
    }

    // Tombol Log out
    @FXML
    private void handleLogout() {
        try {
            App.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
