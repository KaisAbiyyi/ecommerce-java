package com.ecommerce.db.dao.impl;

import com.ecommerce.db.dao.OrderItemDAO;
import com.ecommerce.db.models.OrderItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAOImpl implements OrderItemDAO {

    private final Connection connection;

    /**
     * Konstruktor menerima koneksi database.
     *
     * @param connection Objek Connection untuk mengakses database.
     */
    public OrderItemDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addOrderItem(OrderItem orderItem) {
        String sql = "INSERT INTO order_items (order_seller_id, product_id, quantity, price, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, orderItem.getOrderSellerId());
            preparedStatement.setInt(2, orderItem.getProductId());
            preparedStatement.setInt(3, orderItem.getQuantity());
            preparedStatement.setDouble(4, orderItem.getPrice());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(orderItem.getCreatedAt()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(orderItem.getUpdatedAt()));
            preparedStatement.executeUpdate();

            // Mendapatkan ID yang di-generate
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    orderItem.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding order item: " + e.getMessage());
        }
    }

    @Override
    public OrderItem getOrderItemById(int id) {
        String sql = "SELECT * FROM order_items WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractOrderItemFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching order item by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderSellerId(int orderSellerId) {
        List<OrderItem> orderItems = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_seller_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderSellerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    orderItems.add(extractOrderItemFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching order items by order seller ID: " + e.getMessage());
        }
        return orderItems;
    }

    @Override
    public void updateOrderItem(OrderItem orderItem) {
        String sql = "UPDATE order_items SET order_seller_id = ?, product_id = ?, quantity = ?, price = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderItem.getOrderSellerId());
            preparedStatement.setInt(2, orderItem.getProductId());
            preparedStatement.setInt(3, orderItem.getQuantity());
            preparedStatement.setDouble(4, orderItem.getPrice());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(orderItem.getUpdatedAt()));
            preparedStatement.setInt(6, orderItem.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating order item: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrderItem(int id) {
        String sql = "DELETE FROM order_items WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting order item: " + e.getMessage());
        }
    }

    /**
     * Membantu mengekstrak data item pesanan dari ResultSet.
     *
     * @param resultSet Objek ResultSet.
     * @return Objek OrderItem.
     * @throws SQLException jika terjadi kesalahan dalam membaca data.
     */
    private OrderItem extractOrderItemFromResultSet(ResultSet resultSet) throws SQLException {
        return new OrderItem(
                resultSet.getInt("id"),
                resultSet.getInt("order_seller_id"),
                resultSet.getInt("product_id"),
                resultSet.getInt("quantity"),
                resultSet.getDouble("price"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
