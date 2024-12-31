package com.ecommerce.utils;
import java.util.logging.Logger;

public class Router {
    private static final Logger LOGGER = Logger.getLogger(Router.class.getName());

    // Path untuk layout utama
    public static final String MAIN_LAYOUT = validatePath("/com/ecommerce/layouts/MainLayout.fxml");

    // Path untuk halaman konten
    public static final String DASHBOARD = validatePath("/com/ecommerce/content/DashboardView.fxml");
    public static final String PRODUCT_DETAIL = validatePath("/com/ecommerce/content/ProductDetailView.fxml");

    // Path untuk halaman autentikasi
    public static final String LOGIN = validatePath("/com/ecommerce/shared/LoginView.fxml");
    public static final String REGISTER = validatePath("/com/ecommerce/shared/RegisterView.fxml");

    // Path untuk komponen
    public static final String PROFILE_BAR = validatePath("/com/ecommerce/layouts/ProfileBar.fxml");

    /**
     * Validasi path untuk memastikan file tersedia.
     *
     * @param path Path ke file FXML.
     * @return Path jika valid, atau pesan error jika tidak ditemukan.
     */
    private static String validatePath(String path) {
        if (Router.class.getResource(path) == null) {
            LOGGER.severe("[ERROR] Path FXML tidak ditemukan: " + path);
            return "INVALID_PATH";
        }
        return path;
    }
}
