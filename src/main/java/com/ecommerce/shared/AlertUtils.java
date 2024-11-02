package com.ecommerce.shared;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import java.util.Optional;

public class AlertUtils {

    // Show a confirmation dialog
    public static boolean showConfirmationAlert(String title, String header, String content) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // Show an information dialog
    public static void showInfoAlert(String title, String header, String content) {
        showAlert(AlertType.INFORMATION, title, header, content);
    }

    // Show an error dialog
    public static void showErrorAlert(String title, String header, String content) {
        showAlert(AlertType.ERROR, title, header, content);
    }

    // Generic method for showing an alert
    private static void showAlert(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
