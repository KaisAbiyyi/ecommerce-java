package com.ecommerce.dao;

import com.ecommerce.models.Category;

import java.util.List;

public interface CategoryDAO {

    /**
     * Menambahkan kategori baru ke dalam database.
     *
     * @param category Objek kategori yang akan ditambahkan.
     */
    void addCategory(Category category);

    /**
     * Mengambil kategori berdasarkan ID.
     *
     * @param id ID kategori.
     * @return Objek kategori, atau null jika tidak ditemukan.
     */
    Category getCategoryById(int id);

    /**
     * Mengambil semua kategori dari database.
     *
     * @return Daftar kategori.
     */
    List<Category> getAllCategories();

    /**
     * Memperbarui informasi kategori.
     *
     * @param category Objek kategori dengan informasi terbaru.
     */
    void updateCategory(Category category);

    /**
     * Menghapus kategori berdasarkan ID.
     *
     * @param id ID kategori yang akan dihapus.
     */
    void deleteCategory(int id);

    /**
     * Mencari kategori berdasarkan nama.
     *
     * @param name Nama kategori atau kata kunci.
     * @return Daftar kategori yang cocok dengan nama.
     */
    List<Category> searchCategoryByName(String name);
}
