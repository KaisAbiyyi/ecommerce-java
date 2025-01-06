package com.ecommerce.db.dao;

import com.ecommerce.db.models.OrderSeller;

import java.util.List;

public interface OrderSellerDAO {

    /**
     * Menambahkan sub-pesanan baru (per seller) ke database.
     *
     * @param orderSeller Objek OrderSeller yang akan ditambahkan.
     */
    void addOrderSeller(OrderSeller orderSeller);

    /**
     * Mengambil sub-pesanan berdasarkan ID.
     *
     * @param id ID sub-pesanan.
     * @return Objek OrderSeller, atau null jika tidak ditemukan.
     */
    OrderSeller getOrderSellerById(int id);

    /**
     * Mengambil semua sub-pesanan untuk pesanan utama tertentu.
     *
     * @param orderId ID pesanan utama.
     * @return Daftar OrderSeller terkait pesanan utama.
     */
    List<OrderSeller> getOrderSellersByOrderId(int orderId);

    /**
     * Mengambil semua sub-pesanan untuk seller tertentu.
     *
     * @param sellerId ID seller.
     * @return Daftar OrderSeller terkait seller.
     */
    List<OrderSeller> getOrderSellersBySellerId(int sellerId);

    /**
     * Memperbarui informasi sub-pesanan.
     *
     * @param orderSeller Objek OrderSeller dengan informasi terbaru.
     */
    void updateOrderSeller(OrderSeller orderSeller);

    /**
     * Menghapus sub-pesanan berdasarkan ID.
     *
     * @param id ID sub-pesanan yang akan dihapus.
     */
    void deleteOrderSeller(int id);
}
