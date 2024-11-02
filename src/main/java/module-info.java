module com.ecommerce {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.ecommerce.shared to javafx.fxml;
    opens com.ecommerce.admin to javafx.fxml;
    opens com.ecommerce.customer to javafx.fxml;
    opens com.ecommerce.seller to javafx.fxml;

    exports com.ecommerce;
}
