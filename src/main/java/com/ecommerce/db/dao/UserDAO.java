package com.ecommerce.db.dao;

import java.util.List;
import com.ecommerce.db.models.User;

/**
 * Interface untuk mengelola operasi database yang berhubungan dengan entitas User.
 */
public interface UserDAO {

    /**
     * Menambahkan pengguna baru ke database.
     *
     * @param user Objek User yang berisi informasi pengguna yang akan ditambahkan.
     * @return true jika berhasil ditambahkan, false jika gagal.
     */
    boolean addUser(User user);

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
     * @param user Objek User yang diperbarui, termasuk ID yang menunjukkan pengguna yang akan diperbarui.
     * @return true jika update berhasil, false jika gagal.
     */
    boolean updateUser(User user);

    /**
     * Menghapus pengguna dari database berdasarkan ID.
     *
     * @param id ID unik dari pengguna yang akan dihapus.
     * @return true jika penghapusan berhasil, false jika gagal.
     */
    boolean deleteUser(int id);

    /**
     * Mendapatkan pengguna berdasarkan email dan password. 
     * Metode ini digunakan untuk autentikasi saat login.
     *
     * @param email Email dari pengguna.
     * @param plainPassword Password dari pengguna (plaintext).
     * @return Objek User jika email dan password cocok, atau null jika tidak ditemukan.
     */
    User getUserByEmailAndPassword(String email, String plainPassword);

    /**
     * Mendapatkan pengguna berdasarkan email saja.
     * Metode ini digunakan untuk memeriksa apakah email sudah terdaftar.
     *
     * @param email Email dari pengguna.
     * @return Objek User jika email ditemukan, atau null jika tidak ada.
     */
    User getUserByEmail(String email);

    /**
     * Mengecek apakah email sudah terdaftar di database.
     *
     * @param email Email yang akan diperiksa.
     * @return true jika email ditemukan, false jika tidak ada.
     */
    boolean isEmailRegistered(String email);

    /**
     * Mendapatkan pengguna berdasarkan token autentikasi (digunakan untuk fitur "tetap login").
     *
     * @param token Token autentikasi pengguna.
     * @return Objek User jika token ditemukan, atau null jika tidak ada.
     */
    User getUserByToken(String token);

    /**
     * Mendapatkan daftar pengguna dengan status seller request tertentu.
     *
     * @param sellerRequest Status seller request (NONE, PENDING, APPROVED, REJECTED).
     * @return List berisi objek User dengan status seller request yang diminta.
     */
    List<User> getUsersBySellerRequest(User.SellerRequest sellerRequest);

    /**
     * Memperbarui status seller request dari pengguna.
     *
     * @param userId ID unik pengguna yang seller request-nya akan diperbarui.
     * @param sellerRequest Status seller request baru (PENDING, APPROVED, REJECTED).
     * @return true jika update berhasil, false jika gagal.
     */
    boolean updateSellerRequest(int userId, User.SellerRequest sellerRequest);
}
