package com.ecommerce.layouts;

import com.ecommerce.App;
import com.ecommerce.content.product.ProductDetailViewController;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.Map;
import java.util.Stack;

public class NavbarController {

    private MainLayoutController mainLayoutController;

    // Riwayat navigasi untuk mendukung goBack dan navigasi lainnya
    private final Stack<PageState> backStack = new Stack<>();
    private final Stack<PageState> forwardStack = new Stack<>();
    private PageState currentPageState = null;


    @FXML
    private Button authButton;
    @FXML
    private Button profileButton;
    private String userRole;

    /**
     * Mengatur referensi ke MainLayoutController.
     */

    @FXML
    private void initialize() {
        System.out.println("[INFO] NavbarController diinisialisasi.");

        // Periksa status login
        boolean isLoggedIn = App.loggedInUser != null;
        updateAuthButtonState(isLoggedIn);

        if (isLoggedIn) {
            String userRole = App.loggedInUser.getRole().name().toUpperCase();

            if ("ADMIN".equals(userRole)) {
                // Sembunyikan elemen navigasi dan hanya tampilkan teks "Toko Kami" serta tombol Logout
                toggleNavigationVisibility(false);

            } else {
                // Jika bukan admin, tampilkan semua elemen navigasi
                toggleNavigationVisibility(true);

                setUserRole(userRole);
            }
        } else {
            toggleNavigationVisibility(true);

        }
    }


    @FXML
    private HBox navigationSection;


    private void toggleNavigationVisibility(boolean isVisible) {
        // Sembunyikan atau tampilkan tombol navigasi
        navigationSection.setVisible(isVisible);
        navigationSection.setManaged(isVisible);
    }


    public static class PageState {
        private final String pagePath;
        private final Object additionalData;

        public PageState(String pagePath, Object additionalData) {
            this.pagePath = pagePath;
            this.additionalData = additionalData;
        }

        public String getPagePath() {
            return pagePath;
        }

        public Object getAdditionalData() {
            return additionalData;
        }
    }

    /**
     * Navigasi ke halaman Dashboard/Home.
     */
    @FXML
    public void goHome() {
        try {
            // Path ke halaman Dashboard/Home
            String homePagePath = "/com/ecommerce/content/DashboardView.fxml";

            // Tambahkan halaman ke stack navigasi
            addPageToStack(homePagePath, null);

            // Muat halaman Dashboard/Home
            if (mainLayoutController != null) {
                mainLayoutController.loadContent(homePagePath);
                System.out.println("[INFO] Navigasi ke halaman Home: " + homePagePath);
            } else {
                System.err.println("[ERROR] MainLayoutController belum diatur.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal berpindah ke halaman Home.");
            e.printStackTrace();
        }
    }

    public void updateAuthButtonState(boolean isLoggedIn) {
        try {
            if (isLoggedIn) {
                System.out.println("[INFO] User terautentikasi. Menampilkan tombol Logout.");
                authButton.setText("Logout");
                if (!(App.loggedInUser.getRole().name().equalsIgnoreCase("ADMIN"))) {
                    profileButton.setVisible(true);
                }
                authButton.setOnAction(event -> handleLogout());
            } else {
                System.out.println("[INFO] User belum login. Menampilkan tombol Login.");
                authButton.setText("Login");
                authButton.setOnAction(event -> goToLogin());
                // Tampilkan semua elemen jika tidak login
                toggleNavigationVisibility(true);
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal memperbarui tombol autentikasi.");
            e.printStackTrace();
        }
    }


    private void goToLogin() {
        try {
            String loginPagePath = "/com/ecommerce/content/LoginView.fxml";
            clearNavigationStacks(); // Bersihkan stack navigasi
            addPageToStack(loginPagePath, null);
            if (mainLayoutController != null) {
                mainLayoutController.loadContent(loginPagePath);
                System.out.println("[INFO] Navigasi ke halaman Login: " + loginPagePath);
            } else {
                System.err.println("[ERROR] MainLayoutController belum diatur.");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal berpindah ke halaman Login.");
            e.printStackTrace();
        }
    }

    public void setUserRole(String userRole) {
        if (App.loggedInUser == null) {
            System.err.println("[ERROR] Tidak ada user login. Tombol role tidak diperbarui.");
            profileButton.setVisible(false);
            return;
        }

        this.userRole = userRole.toLowerCase();

        // Jika user adalah admin, sembunyikan tombol role
        if ("admin".equals(this.userRole)) {
            toggleNavigationVisibility(false); // Sembunyikan elemen navigasi
            profileButton.setVisible(false);
        } else {
            toggleNavigationVisibility(true);
            profileButton.setOnAction(event -> goToProfile());
        }
    }


    @FXML
    public void goToProfile() {
        try {
            String manageProductsPagePath = "/com/ecommerce/content/ProfileView.fxml";
            addPageToStack(manageProductsPagePath, null);
            mainLayoutController.loadContent(manageProductsPagePath);
            System.out.println("[INFO] Navigasi ke halaman Manage Products.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal membuka halaman Manage Products.");
            e.printStackTrace();
        }
    }


    public void addPageToStack(String page, Object additionalData) {
        if (page == null || page.isEmpty()) {
            System.err.println("[ERROR] Halaman tidak valid untuk ditambahkan ke stack.");
            return;
        }

        if (currentPageState != null) {
            backStack.push(currentPageState); // Simpan halaman saat ini ke backStack
            System.out.println("[INFO] Halaman saat ini ditambahkan ke backStack: " + currentPageState.getPagePath());
        }

        forwardStack.clear(); // Kosongkan forwardStack
        currentPageState = new PageState(page, additionalData); // Set halaman baru sebagai currentPageState

        logStacks(); // Log stack navigasi untuk debugging
    }


    public void setMainLayoutController(MainLayoutController mainLayoutController) {
        this.mainLayoutController = mainLayoutController;
        System.out.println("[INFO] MainLayoutController berhasil diatur pada NavbarController.");
    }

    public void clearNavigationStacks() {
        try {
            backStack.clear(); // Bersihkan stack navigasi sebelumnya
            forwardStack.clear(); // Bersihkan stack navigasi berikutnya
            currentPageState = null; // Reset halaman saat ini
            System.out.println("[INFO] Semua stack navigasi telah dibersihkan.");
        } catch (Exception e) {
            System.err.println("[ERROR] Gagal membersihkan stack navigasi:");
            e.printStackTrace();
        }
    }


    /**
     * Menampilkan atau menyembunyikan ProfileBar.
     */
    @FXML
    public void handleLogout() {
        try {
            updateAuthButtonState(false);
            System.out.println("[INFO] Logout button clicked. Logging out...");
            clearNavigationStacks(); // Bersihkan stack navigasi
            App.logout();

            // Kembalikan elemen navigasi dan tombol role agar terlihat
            toggleNavigationVisibility(true);
            profileButton.setVisible(false);

            goToLogin(); // Navigasi ke halaman login
            System.out.println("[INFO] Logout berhasil. Redirect ke halaman login.");
        } catch (Exception e) {
            System.err.println("[ERROR] Terjadi kesalahan saat logout:");
            e.printStackTrace();
        }
    }


    /**
     * Memulihkan data tambahan dari PageState ke controller saat ini.
     *
     * @param additionalData Data tambahan yang ingin dipulihkan.
     */
    public void restoreAdditionalData(Object additionalData) {
        if (additionalData == null) {
            System.out.println("[WARN] Tidak ada data tambahan untuk dipulihkan.");
            return;
        }

        // Identifikasi tipe data tambahan dan operasikan ke controller terkait
        if (additionalData instanceof Integer) {
            // Contoh: Untuk halaman detail produk
            if (mainLayoutController != null) {
                Object controller = mainLayoutController.getCurrentController();
                if (controller instanceof ProductDetailViewController) {
                    ((ProductDetailViewController) controller).setProductId((Integer) additionalData);
                    System.out.println("[INFO] Data tambahan productId dipulihkan: " + additionalData);
                } else {
                    System.err.println("[WARN] Controller saat ini bukan ProductDetailViewController.");
                }
            } else {
                System.err.println("[ERROR] MainLayoutController belum diatur.");
            }
        } else {
            System.out.println("[WARN] Tipe data tambahan tidak dikenali: " + additionalData.getClass().getName());
        }
    }


    /**
     * Navigasi ke halaman sebelumnya.
     */

    @FXML
    public void goBack() {
        if (!backStack.isEmpty()) {
            try {
                forwardStack.push(currentPageState); // Simpan halaman saat ini ke forwardStack
                currentPageState = backStack.pop(); // Ambil halaman sebelumnya dari backStack
                mainLayoutController.loadContent(currentPageState.getPagePath()); // Muat halaman sebelumnya
                restoreAdditionalData(currentPageState); // Pulihkan data tambahan

                // Cek jika halaman adalah ProductDetailView
                if (currentPageState.getPagePath().equals("/com/ecommerce/content/product/ProductDetailView.fxml")) {
                    ProductDetailViewController controller =
                            (ProductDetailViewController) App.mainLayoutController.getCurrentController();
                    if (controller != null) {
                        Object additionalData = currentPageState.getAdditionalData();
                        if (additionalData instanceof Map<?, ?> additionalDataMap) {
                            Object productIdObj = additionalDataMap.get("productId");
                            if (productIdObj instanceof Integer productId) {
                                controller.setProductId(productId); // Set Product ID
                                System.out.println("[INFO] Product ID dipulihkan: " + productId);
                            } else {
                                System.err.println("[ERROR] Data tambahan Product ID tidak valid.");
                            }
                        } else {
                            System.err.println("[ERROR] Additional Data bukan tipe Map.");
                        }
                    } else {
                        System.err.println("[ERROR] Controller untuk ProductDetailView tidak ditemukan.");
                    }
                }

                logStacks();
                System.out.println("[INFO] Berpindah ke halaman sebelumnya: " + currentPageState.getPagePath());
            } catch (Exception e) {
                System.err.println("[ERROR] Gagal berpindah ke halaman sebelumnya.");
                e.printStackTrace();
            }
        } else {
            System.out.println("[WARN] Tidak ada halaman sebelumnya.");
        }
    }

    @FXML
    public void goForward() {
        if (!forwardStack.isEmpty()) {
            try {
                backStack.push(currentPageState); // Simpan halaman saat ini ke backStack
                currentPageState = forwardStack.pop(); // Ambil halaman berikutnya dari forwardStack
                mainLayoutController.loadContent(currentPageState.getPagePath()); // Muat halaman berikutnya
                restoreAdditionalData(currentPageState); // Pulihkan data tambahan

                // Cek jika halaman adalah ProductDetailView
                if (currentPageState.getPagePath().equals("/com/ecommerce/content/product/ProductDetailView.fxml")) {
                    ProductDetailViewController controller =
                            (ProductDetailViewController) App.mainLayoutController.getCurrentController();
                    if (controller != null) {
                        Object additionalData = currentPageState.getAdditionalData();
                        if (additionalData instanceof Map<?, ?> additionalDataMap) {
                            Object productIdObj = additionalDataMap.get("productId");
                            if (productIdObj instanceof Integer productId) {
                                controller.setProductId(productId); // Set Product ID
                                System.out.println("[INFO] Product ID dipulihkan: " + productId);
                            } else {
                                System.err.println("[ERROR] Data tambahan Product ID tidak valid.");
                            }
                        } else {
                            System.err.println("[ERROR] Additional Data bukan tipe Map.");
                        }
                    } else {
                        System.err.println("[ERROR] Controller untuk ProductDetailView tidak ditemukan.");
                    }
                }

                logStacks();
                System.out.println("[INFO] Berpindah ke halaman berikutnya: " + currentPageState.getPagePath());
            } catch (Exception e) {
                System.err.println("[ERROR] Gagal berpindah ke halaman berikutnya.");
                e.printStackTrace();
            }
        } else {
            System.out.println("[WARN] Tidak ada halaman berikutnya.");
        }
    }


    /**
     * Log isi stack navigasi.
     */
    /**
     * Log isi stack navigasi, termasuk data tambahan.
     */
    private void logStacks() {
        System.out.println("=== Navigasi Log ===");

        // Log Back Stack
        System.out.println("Back Stack:");
        if (backStack.isEmpty()) {
            System.out.println("  [Kosong]");
        } else {
            backStack.forEach(pageState -> System.out.println("  - " + pageState));
        }

        // Log Forward Stack
        System.out.println("Forward Stack:");
        if (forwardStack.isEmpty()) {
            System.out.println("  [Kosong]");
        } else {
            forwardStack.forEach(pageState -> System.out.println("  - " + pageState));
        }

        // Log Current Page
        System.out.println("Current Page:");
        if (currentPageState != null) {
            System.out.println("  - Page Path: " + currentPageState.getPagePath());
            if (currentPageState.getAdditionalData() != null) {
                System.out.println("  - Additional Data: " + currentPageState.getAdditionalData());
            } else {
                System.out.println("  - Additional Data: [Kosong]");
            }
        } else {
            System.out.println("  [Kosong]");
        }

        System.out.println("====================");
    }

}
