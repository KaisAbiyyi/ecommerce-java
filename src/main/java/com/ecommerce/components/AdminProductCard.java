package com.ecommerce.components;

import com.ecommerce.App;
import com.ecommerce.content.seller.EditProductController;
import com.ecommerce.dao.ProductDAO;
import com.ecommerce.dao.impl.ProductDAOImpl;
import com.ecommerce.layouts.MainLayoutController;
import com.ecommerce.layouts.NavbarController;
import com.ecommerce.utils.DatabaseUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.Connection;
import java.util.Map;
import java.util.Optional;

public class AdminProductCard extends HBox {

    private static final double IMAGE_WIDTH = 200.0;
    private static final double NAME_WIDTH = 300.0;
    private static final double DESCRIPTION_WIDTH = 400.0;
    private static final double PRICE_WIDTH = 150.0;
    private static final double STOCK_WIDTH = 150.0;
    private static final double ACTION_WIDTH = 200.0;
    private int productId;

    public AdminProductCard(int productId, String name, String description, String price, String stock, Image image) {
        super(10); // Spacing antar elemen
        this.productId = productId;
        this.setStyle("-fx-padding: 10; -fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f9f9f9;");
        this.setPrefWidth(IMAGE_WIDTH + NAME_WIDTH + DESCRIPTION_WIDTH + PRICE_WIDTH + STOCK_WIDTH + ACTION_WIDTH);

        // Gambar produk
        VBox imageBox = createVBox("Image:", image, IMAGE_WIDTH);

        // Nama produk
        VBox nameBox = createVBox("Name:", new Label(name), NAME_WIDTH);

        // Deskripsi produk
        VBox descriptionBox = createVBox("Description:", new Label(description), DESCRIPTION_WIDTH);

        // Harga produk
        VBox priceBox = createVBox("Price:", new Label("$" + price), PRICE_WIDTH);

        // Stok produk
        VBox stockBox = createVBox("Stock:", new Label(stock), STOCK_WIDTH);

        // Aksi (Edit dan Delete)
        VBox actionBox = createActionBox(ACTION_WIDTH);

        // Tambahkan semua elemen ke dalam HBox
        this.getChildren().addAll(imageBox, nameBox, descriptionBox, priceBox, stockBox, actionBox);
    }

    private VBox createVBox(String labelText, Label valueLabel, double width) {
        VBox box = new VBox();
        box.setPrefWidth(width);
        box.setStyle("-fx-padding: 5;");
        Label label = new Label(labelText);
        valueLabel.setWrapText(true);
        box.getChildren().addAll(label, valueLabel);
        return box;
    }

    private VBox createVBox(String labelText, Image image, double width) {
        VBox box = new VBox();
        box.setPrefWidth(width);
        box.setStyle("-fx-padding: 5;");
        Label label = new Label(labelText);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width * 0.9);
        imageView.setPreserveRatio(true);
        box.getChildren().addAll(label, imageView);
        return box;
    }

    private VBox createActionBox(double width) {
        VBox box = new VBox();
        box.setPrefWidth(width);
        box.setStyle("-fx-padding: 5;");
        Label label = new Label("Action:");
        HBox actionButtons = new HBox(10);

        // Tombol Edit
        Button editButton = new Button("Edit");
        editButton.setPrefWidth(80);
        editButton.setStyle("-fx-padding: 5;");

        // Tombol Delete
        Button deleteButton = new Button("Delete");
        deleteButton.setPrefWidth(80);
        deleteButton.setStyle("-fx-padding: 5;");

        actionButtons.getChildren().addAll(editButton, deleteButton);
        box.getChildren().addAll(label, actionButtons);

        // Tambahkan event handler
        editButton.setOnAction(event -> handleEdit());
        deleteButton.setOnAction(event -> handleDelete());
        return box;
    }

    private void handleEdit() {
        try {
            // Pastikan MainLayoutController tersedia
            if (App.mainLayoutController != null) {
                // Tambahkan halaman ke stack navigasi
                NavbarController navbarController = App.mainLayoutController.getNavbarController();
                if (navbarController != null) {
                    // Tambahkan halaman dengan data tambahan (ID produk)
                    navbarController.addPageToStack(
                            "/com/ecommerce/content/seller/EditProductView.fxml",
                            Map.of("productId", productId) // Data tambahan: ID produk
                    );
                }

                // Muat konten menggunakan MainLayoutController
                App.mainLayoutController.loadContent("/com/ecommerce/content/seller/EditProductView.fxml");

                // Dapatkan controller dari konten yang dimuat
                EditProductController controller =
                        (EditProductController) App.mainLayoutController.getCurrentController();

                // Pastikan controller berhasil diambil dan set ID produk
                if (controller != null) {
                    controller.setProductId(productId); // Tetapkan ID produk
                    System.out.println("[INFO] Berhasil navigasi ke halaman Edit Product dengan ID: " + productId);
                } else {
                    System.err.println("[ERROR] Gagal mendapatkan controller EditProductController.");
                }
            } else {
                System.err.println("[ERROR] MainLayoutController tidak ditemukan. Pastikan layout utama telah diatur dengan benar.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kesalahan saat navigasi ke halaman Edit Product.");
            e.printStackTrace();
        }
    }



    private void handleDelete() {
        System.out.println("[INFO] Delete button clicked for product with ID: " + productId);

        // Konfirmasi penghapusan
        boolean confirmDelete = showConfirmationDialog("Delete Product", "Are you sure you want to delete this product?");
        if (!confirmDelete) {
            System.out.println("[INFO] Delete action canceled.");
            return;
        }

        // Hapus dari database
        try (Connection connection = DatabaseUtils.getConnection()) {
            ProductDAO productDAO = new ProductDAOImpl(connection);
            productDAO.deleteProduct(productId);

            // Validasi apakah produk benar-benar terhapus
            boolean stillExists = productDAO.isProductExists(productId);
            if (!stillExists) {
                System.out.println("[INFO] Product deleted successfully from database.");
                ((VBox) this.getParent()).getChildren().remove(this); // Hapus dari UI
            } else {
                System.err.println("[ERROR] Product still exists in database. Delete failed.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] An error occurred while deleting the product:");
            e.printStackTrace();
        }

    }


    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && ((java.util.Optional<?>) result).get() == ButtonType.OK;
    }

}
