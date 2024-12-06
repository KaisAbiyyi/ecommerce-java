package com.ecommerce.dao;

import com.ecommerce.models.OrderItem;

import java.util.List;

public interface OrderItemDAO {

    /**
     * Menambahkan item pesanan baru ke database.
     *
     * @param orderItem Objek OrderItem yang akan ditambahkan.
     */
    void addOrderItem(OrderItem orderItem);

    /**
     * Mengambil item pesanan berdasarkan ID.
     *
     * @param id ID item pesanan.
     * @return Objek OrderItem, atau null jika tidak ditemukan.
     */
    OrderItem getOrderItemById(int id);

    /**
     * Mengambil semua item pesanan berdasarkan ID sub-pesanan.
     *
     * @param orderSellerId ID sub-pesanan.
     * @return Daftar OrderItem terkait sub-pesanan.
     */
    List<OrderItem> getOrderItemsByOrderSellerId(int orderSellerId);

    /**
     * Memperbarui informasi item pesanan.
     *
     * @param orderItem Objek OrderItem dengan informasi terbaru.
     */
    void updateOrderItem(OrderItem orderItem);

    /**
     * Menghapus item pesanan berdasarkan ID.
     *
     * @param id ID item pesanan yang akan dihapus.
     */
    void deleteOrderItem(int id);
}
