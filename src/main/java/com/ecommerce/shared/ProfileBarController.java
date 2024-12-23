package com.ecommerce.shared;

import com.ecommerce.App;
import com.ecommerce.models.User;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ProfileBarController {

    @FXML
    private Text profileNameText; // Elemen teks untuk menampilkan nama pengguna

    // Tombol Close
    @FXML
    public void closeProfileBar() {
        if (profileNameText.getScene() != null) {
            profileNameText.getScene().lookup("#profileBar").setVisible(false);
            profileNameText.getScene().lookup("#profileBar").setManaged(false);
            System.out.println("ProfileBar berhasil ditutup.");
        } else {
            System.out.println("Elemen profileBar tidak ditemukan!");
        }
    }

    // Tombol Log out
    @FXML
    private void handleLogout() {
        try {
            App.logout(); // Panggil fungsi logout dari kelas App
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Inisialisasi controller
    @FXML
    public void initialize() {
        if (profileNameText == null) {
            System.out.println("profileNameText is null! Pastikan elemen ini terhubung di FXML.");
            return;
        }

        User loggedInUser = App.getLoggedInUser();
        if (loggedInUser != null) {
            // Update nama pengguna saat inisialisasi
            javafx.application.Platform.runLater(() -> {
                profileNameText.setText(loggedInUser.getUsername());
                System.out.println("Updated text on initialize: " + profileNameText.getText());
            });
        } else {
            javafx.application.Platform.runLater(() -> {
                profileNameText.setText("Unknown User");
                System.out.println("Set text to 'Unknown User' on initialize.");
            });
        }
    }
}
