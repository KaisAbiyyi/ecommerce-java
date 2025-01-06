package com.ecommerce.db.dao;

import com.ecommerce.db.models.CartSeller;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartSellerDAOImpl implements CartSellerDAO {

    private final Connection connection;

    /**
     * Konstruktor menerima koneksi database.
     *
     * @param connection Objek Connection untuk mengakses database.
     */
    public CartSellerDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addCartSeller(CartSeller cartSeller) {
        String sql = "INSERT INTO cart_sellers (user_id, seller_id, created_at, updated_at) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, cartSeller.getUserId());
            preparedStatement.setInt(2, cartSeller.getSellerId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(cartSeller.getCreatedAt()));
            preparedStatement.setTimestamp(4, Timestamp.valueOf(cartSeller.getUpdatedAt()));
            preparedStatement.executeUpdate();

            // Mendapatkan ID yang di-generate
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cartSeller.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding cart seller: " + e.getMessage());
        }
    }

    @Override
    public CartSeller getCartSellerById(int id) {
        String sql = "SELECT * FROM cart_sellers WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractCartSellerFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching cart seller by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<CartSeller> getCartSellersByUserId(int userId) {
        List<CartSeller> cartSellers = new ArrayList<>();
        String sql = "SELECT * FROM cart_sellers WHERE user_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, userId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cartSellers.add(extractCartSellerFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching cart sellers by user ID: " + e.getMessage());
        }
        return cartSellers;
    }

    @Override
    public void updateCartSeller(CartSeller cartSeller) {
        String sql = "UPDATE cart_sellers SET user_id = ?, seller_id = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cartSeller.getUserId());
            preparedStatement.setInt(2, cartSeller.getSellerId());
            preparedStatement.setTimestamp(3, Timestamp.valueOf(cartSeller.getUpdatedAt()));
            preparedStatement.setInt(4, cartSeller.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating cart seller: " + e.getMessage());
        }
    }

    @Override
    public void deleteCartSeller(int id) {
        String sql = "DELETE FROM cart_sellers WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting cart seller: " + e.getMessage());
        }
    }

    /**
     * Membantu mengekstrak data CartSeller dari ResultSet.
     *
     * @param resultSet Objek ResultSet.
     * @return Objek CartSeller.
     * @throws SQLException jika terjadi kesalahan dalam membaca data.
     */
    private CartSeller extractCartSellerFromResultSet(ResultSet resultSet) throws SQLException {
        return new CartSeller(
                resultSet.getInt("id"),
                resultSet.getInt("user_id"),
                resultSet.getInt("seller_id"),
                resultSet.getTimestamp("created_at").toLocalDateTime(),
                resultSet.getTimestamp("updated_at").toLocalDateTime()
        );
    }
}

