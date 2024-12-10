package com.ecommerce.customer;

import com.ecommerce.App;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ProductController {

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
