package com.ecommerce.db.dao;

import com.ecommerce.db.models.CartSeller;

import java.util.List;

public interface CartSellerDAO {

    /**
     * Menambahkan entri keranjang untuk seller baru ke database.
     *
     * @param cartSeller Objek CartSeller yang akan ditambahkan.
     */
    void addCartSeller(CartSeller cartSeller);

    /**
     * Mengambil entri keranjang berdasarkan ID.
     *
     * @param id ID entri keranjang.
     * @return Objek CartSeller, atau null jika tidak ditemukan.
     */
    CartSeller getCartSellerById(int id);

    /**
     * Mengambil semua entri keranjang berdasarkan ID pengguna.
     *
     * @param userId ID pengguna.
     * @return Daftar CartSeller terkait pengguna.
     */
    List<CartSeller> getCartSellersByUserId(int userId);

    /**
     * Memperbarui informasi entri keranjang.
     *
     * @param cartSeller Objek CartSeller dengan informasi terbaru.
     */
    void updateCartSeller(CartSeller cartSeller);

    /**
     * Menghapus entri keranjang berdasarkan ID.
     *
     * @param id ID entri keranjang yang akan dihapus.
     */
    void deleteCartSeller(int id);
}
