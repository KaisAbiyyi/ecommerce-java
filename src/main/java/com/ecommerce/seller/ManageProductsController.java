package com.ecommerce.seller;

import com.ecommerce.App;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ManageProductsController {

    @FXML
    private ListView<?> productList;

    @FXML
    private void handleLogout() {
        try {
            App.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
