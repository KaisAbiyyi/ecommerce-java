package com.ecommerce.db.dao;

import com.ecommerce.db.models.CartItem;

import java.util.List;

public interface CartItemDAO {

    /**
     * Menambahkan item keranjang baru ke database.
     *
     * @param cartItem Objek CartItem yang akan ditambahkan.
     */
    void addCartItem(CartItem cartItem);

    /**
     * Mengambil item keranjang berdasarkan ID.
     *
     * @param id ID item keranjang.
     * @return Objek CartItem, atau null jika tidak ditemukan.
     */
    CartItem getCartItemById(int id);

    /**
     * Mengambil semua item keranjang berdasarkan ID sub-keranjang.
     *
     * @param cartSellerId ID sub-keranjang.
     * @return Daftar CartItem terkait sub-keranjang.
     */
    List<CartItem> getCartItemsByCartSellerId(int cartSellerId);

    /**
     * Memperbarui informasi item keranjang.
     *
     * @param cartItem Objek CartItem dengan informasi terbaru.
     */
    void updateCartItem(CartItem cartItem);

    /**
     * Menghapus item keranjang berdasarkan ID.
     *
     * @param id ID item keranjang yang akan dihapus.
     */
    void deleteCartItem(int id);
}
