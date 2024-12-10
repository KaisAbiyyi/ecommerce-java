package com.ecommerce.dao;

import java.util.List;

import com.ecommerce.models.User;

/**
 * Interface untuk mengelola operasi database yang berhubungan dengan entitas
 * User.
 */
public interface UserDAO {

    /**
     * Menambahkan pengguna baru ke database.
     *
     * @param user Objek User yang berisi informasi pengguna yang akan
     * ditambahkan.
     */
    void addUser(User user);

    /**
     * Mendapatkan pengguna berdasarkan ID mereka.
     *
     * @param id ID unik pengguna yang akan diambil.
     * @return Objek User yang sesuai dengan ID, atau null jika tidak ditemukan.
     */
    User getUserById(int id);

    /**
     * Mendapatkan daftar semua pengguna di database.
     *
     * @return List berisi objek User dari semua pengguna yang ada.
     */
    List<User> getAllUsers();

    /**
     * Memperbarui informasi pengguna yang ada di database.
     *
     * @param user Objek User yang diperbarui, termasuk ID yang menunjukkan
     * pengguna yang akan diperbarui.
     */
    void updateUser(User user);

    /**
     * Menghapus pengguna dari database berdasarkan ID.
     *
     * @param id ID unik dari pengguna yang akan dihapus.
     */
    void deleteUser(int id);

    /**
     * Mendapatkan pengguna berdasarkan username dan password. Metode ini
     * digunakan untuk autentikasi saat login.
     *
     * @param username Username dari pengguna.
     * @param password Password dari pengguna (bisa dalam bentuk plaintext atau
     * hash, tergantung pada implementasi).
     * @return Objek User jika username dan password cocok, atau null jika tidak
     * ditemukan.
     */
    User getUserByUsernameAndPassword(String username, String password);
}
