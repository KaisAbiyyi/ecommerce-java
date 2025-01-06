package com.ecommerce.db.dao.impl;

import com.ecommerce.db.dao.OrderDAO;
import com.ecommerce.db.models.Order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAOImpl implements OrderDAO {

    private final Connection connection;

    /**
     * Konstruktor menerima koneksi database.
     *
     * @param connection Objek Connection untuk mengakses database.
     */
    public OrderDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addOrder(Order order) {
        String sql = "INSERT INTO orders (user_id, total_price, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, order.getUserId());
            preparedStatement.setBigDecimal(2, order.getTotalPrice());
            preparedStatement.setString(3, order.getStatus().name());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(order.getCreatedAt()));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(order.getUpdatedAt()));
            preparedStatement.executeUpdate();

            // Mendapatkan ID yang di-generate
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    order.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding order: " + e.getMessage());
        }
    }

    @Override
    public Order getOrderById(int id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractOrderFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching order by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Order> getOrdersByUserId(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    orders.add(extractOrderFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching orders by user ID: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            while (resultSet.next()) {
                orders.add(extractOrderFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching all orders: " + e.getMessage());
        }
        return orders;
    }

    @Override
    public void updateOrder(Order order) {
        String sql = "UPDATE orders SET user_id = ?, total_price = ?, status = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, order.getUserId());
            preparedStatement.setBigDecimal(2, order.getTotalPrice());
            preparedStatement.setString(3, order.getStatus().name());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(order.getUpdatedAt()));
            preparedStatement.setInt(5, order.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating order: " + e.getMessage());
        }
    }

    @Override
    public void deleteOrder(int id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting order: " + e.getMessage());
        }
    }

    /**
     * Membantu mengekstrak data pesanan dari ResultSet.
     *
     * @param resultSet Objek ResultSet.
     * @return Objek Order.
     * @throws SQLException jika terjadi kesalahan dalam membaca data.
     */
    private Order extractOrderFromResultSet(ResultSet resultSet) throws SQLException {
        return new Order(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getBigDecimal("total_price"),
                Order.Status.fromString(resultSet.getString("status")),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}
