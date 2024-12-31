module com.ecommerce {
    // Required modules from JDK and JavaFX
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql; // Diperlukan untuk koneksi database
    requires jbcrypt; // Untuk hashing password

    // Membuka package FXML agar dapat diakses oleh FXMLLoader
    opens com.ecommerce.shared to javafx.fxml;
    opens com.ecommerce.product to javafx.fxml;
    opens com.ecommerce.admin to javafx.fxml;
    opens com.ecommerce.customer to javafx.fxml;
    opens com.ecommerce.seller to javafx.fxml;
    opens com.ecommerce.utils to javafx.fxml; // Jika utilitas Anda membutuhkan akses FXML

    // Mengekspor package agar dapat diimpor dari modul eksternal
    exports com.ecommerce;
    exports com.ecommerce.utils;
    opens com.ecommerce.layouts to javafx.fxml;
    opens com.ecommerce.content to javafx.fxml; // Hanya jika utils dipakai di luar modul ini
}
