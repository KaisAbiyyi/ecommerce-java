package com.ecommerce.shared;

import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public abstract class BaseController implements Initializable {

    // Utility function to show an information alert
    protected void showInfoAlert(String title, String header, String content) {
        showAlert(AlertType.INFORMATION, title, header, content);
    }

    // Utility function to show an error alert
    protected void showErrorAlert(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }

    // Generic alert function
    private void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // Add any other shared methods for controllers here
}
