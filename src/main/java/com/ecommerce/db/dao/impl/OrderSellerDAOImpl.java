package com.ecommerce.db.dao.impl;

import com.ecommerce.db.dao.OrderSellerDAO;
import com.ecommerce.db.models.OrderSeller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderSellerDAOImpl implements OrderSellerDAO {

    private final Connection connection;

    /**
     * Konstruktor menerima koneksi database.
     *
     * @param connection Objek Connection untuk mengakses database.
     */
    public OrderSellerDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addOrderSeller(OrderSeller orderSeller) {
        String sql = "INSERT INTO order_sellers (order_id, seller_id, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, orderSeller.getOrderId());
            preparedStatement.setInt(2, orderSeller.getSellerId());
            preparedStatement.setString(3, orderSeller.getStatus().name());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(orderSeller.getCreatedAt()));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(orderSeller.getUpdatedAt()));
            preparedStatement.executeUpdate();

            // Mendapatkan ID yang di-generate
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderSeller.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding order seller: " + e.getMessage());
        }
    }

    @Override
    public OrderSeller getOrderSellerById(int id) {
        String sql = "SELECT * FROM order_sellers WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractOrderSellerFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching order seller by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<OrderSeller> getOrderSellersByOrderId(int orderId) {
        List<OrderSeller> orderSellers = new ArrayList<>();
        String sql = "SELECT * FROM order_sellers WHERE order_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    orderSellers.add(extractOrderSellerFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching order sellers by order ID: " + e.getMessage());
        }
        return orderSellers;
    }

    @Override
    public List<OrderSeller> getOrderSellersBySellerId(int sellerId) {
        List<OrderSeller> orderSellers = new ArrayList<>();
        String sql = "SELECT * FROM order_sellers WHERE seller_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, sellerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    orderSellers.add(extractOrderSellerFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching order sellers by seller ID: " + e.getMessage());
        }
        return orderSellers;
    }

    @Override
    public void updateOrderSeller(OrderSeller orderSeller) {
        String sql = "UPDATE order_sellers SET order_id = ?, seller_id = ?, status = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderSeller.getOrderId());
            preparedStatement.setInt(2, orderSeller.getSellerId());
            preparedStatement.setString(3, orderSeller.getStatus().name());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(orderSeller.getUpdatedAt()));
            preparedStatement.setInt(5, orderSeller.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating order seller: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrderSeller(int id) {
        String sql = "DELETE FROM order_sellers WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting order seller: " + e.getMessage());
        }
    }

    /**
     * Membantu mengekstrak data sub-pesanan dari ResultSet.
     *
     * @param resultSet Objek ResultSet.
     * @return Objek OrderSeller.
     * @throws SQLException jika terjadi kesalahan dalam membaca data.
     */
    private OrderSeller extractOrderSellerFromResultSet(ResultSet resultSet) throws SQLException {
        return new OrderSeller(
                resultSet.getInt("id"),
                resultSet.getInt("order_id"),
                resultSet.getInt("seller_id"),
                OrderSeller.Status.fromString(resultSet.getString("status")),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
