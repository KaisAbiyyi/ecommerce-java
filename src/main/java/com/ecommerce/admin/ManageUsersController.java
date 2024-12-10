package com.ecommerce.admin;

import com.ecommerce.App;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;

public class ManageUsersController {

    @FXML
    private ListView<?> userList;

    @FXML
    private void handleLogout() {
        try {
            App.logout();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
