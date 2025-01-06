package com.ecommerce.db.dao;

import com.ecommerce.db.models.Order;

import java.util.List;

public interface OrderDAO {

    /**
     * Menambahkan pesanan baru ke database.
     *
     * @param order Objek Order yang akan ditambahkan.
     */
    void addOrder(Order order);

    /**
     * Mengambil pesanan berdasarkan ID.
     *
     * @param id ID pesanan.
     * @return Objek Order, atau null jika tidak ditemukan.
     */
    Order getOrderById(int id);

    /**
     * Mengambil semua pesanan yang dibuat oleh pengguna tertentu.
     *
     * @param userId ID pengguna.
     * @return Daftar pesanan yang dibuat oleh pengguna.
     */
    List<Order> getOrdersByUserId(int userId);

    /**
     * Mengambil semua pesanan di database.
     *
     * @return Daftar semua pesanan.
     */
    List<Order> getAllOrders();

    /**
     * Memperbarui informasi pesanan.
     *
     * @param order Objek Order dengan informasi terbaru.
     */
    void updateOrder(Order order);

    /**
     * Menghapus pesanan berdasarkan ID.
     *
     * @param id ID pesanan yang akan dihapus.
     */
    void deleteOrder(int id);
}
