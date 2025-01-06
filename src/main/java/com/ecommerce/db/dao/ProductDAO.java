package com.ecommerce.db.dao;

import com.ecommerce.db.models.Product;

import java.util.List;

/**
 * Interface untuk operasi terkait produk di database.
 */
public interface ProductDAO {

    /**
     * Menambahkan produk baru ke dalam database.
     *
     * @param product Objek produk yang akan ditambahkan.
     */
    void addProduct(Product product);

    /**
     * Mengambil produk berdasarkan ID.
     *
     * @param id ID produk.
     * @return Objek produk, atau null jika tidak ditemukan.
     */
    Product getProductById(int id);

    /**
     * Mengambil semua produk yang ada di database.
     *
     * @return Daftar produk.
     */
    List<Product> getAllProducts();

    /**
     * Memperbarui informasi produk di database.
     *
     * @param product Objek produk dengan informasi terbaru.
     */
    void updateProduct(Product product);

    /**
     * Menghapus produk berdasarkan ID dari database.
     *
     * @param id ID produk yang akan dihapus.
     */
    void deleteProduct(int id);

    /**
     * Mengambil semua produk berdasarkan kategori tertentu.
     *
     * @param categoryId ID kategori produk.
     * @return Daftar produk dalam kategori tertentu.
     */
    List<Product> getProductsByCategory(int categoryId);

    /**
     * Mengambil semua produk yang dijual oleh seller tertentu.
     *
     * @param sellerId ID seller.
     * @return Daftar produk yang dijual oleh seller tertentu.
     */
    List<Product> getProductsBySeller(int sellerId);

    /**
     * Mencari produk berdasarkan nama atau kata kunci.
     *
     * @param name Nama produk atau kata kunci pencarian.
     * @return Daftar produk yang cocok dengan nama atau kata kunci.
     */
    List<Product> searchProductsByName(String name);

    /**
     * Mengambil semua produk yang memiliki gambar (imageUrl tidak null).
     *
     * @return Daftar produk yang memiliki gambar.
     */
    List<Product> getProductsWithImages();

    /**
     * Mengambil jumlah total produk berdasarkan kategori tertentu.
     *
     * @param categoryId ID kategori produk.
     * @return Jumlah produk dalam kategori tersebut.
     */
    int countProductsByCategory(int categoryId);

    /**
     * Mengambil produk dengan stok rendah (threshold tertentu).
     *
     * @param threshold Batas stok minimal untuk dipertimbangkan rendah.
     * @return Daftar produk dengan stok rendah.
     */
    List<Product> getLowStockProducts(int threshold);

    public boolean isProductExists(int id);
}
