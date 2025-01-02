package com.ecommerce.dao.impl;

import com.ecommerce.dao.CartItemDAO;
import com.ecommerce.models.CartItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CartItemDAOImpl implements CartItemDAO {

    private final Connection connection;

    /**
     * Konstruktor menerima koneksi database.
     *
     * @param connection Objek Connection untuk mengakses database.
     */
    public CartItemDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addCartItem(CartItem cartItem) {
        String sql = "INSERT INTO cart_items (cart_seller_id, product_id, quantity, created_at, updated_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setInt(1, cartItem.getCartSellerId());
            preparedStatement.setInt(2, cartItem.getProductId());
            preparedStatement.setInt(3, cartItem.getQuantity());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(cartItem.getCreatedAt()));
            preparedStatement.setTimestamp(5, Timestamp.valueOf(cartItem.getUpdatedAt()));
            preparedStatement.executeUpdate();

            // Mendapatkan ID yang di-generate
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    cartItem.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error adding cart item: " + e.getMessage());
        }
    }

    @Override
    public CartItem getCartItemById(int id) {
        String sql = "SELECT * FROM cart_items WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return extractCartItemFromResultSet(resultSet);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching cart item by ID: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<CartItem> getCartItemsByCartSellerId(int cartSellerId) {
        List<CartItem> cartItems = new ArrayList<>();
        String sql = "SELECT * FROM cart_items WHERE cart_seller_id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cartSellerId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    cartItems.add(extractCartItemFromResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error fetching cart items by cart seller ID: " + e.getMessage());
        }
        return cartItems;
    }

    @Override
    public void updateCartItem(CartItem cartItem) {
        String sql = "UPDATE cart_items SET cart_seller_id = ?, product_id = ?, quantity = ?, updated_at = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, cartItem.getCartSellerId());
            preparedStatement.setInt(2, cartItem.getProductId());
            preparedStatement.setInt(3, cartItem.getQuantity());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(cartItem.getUpdatedAt()));
            preparedStatement.setInt(5, cartItem.getId());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error updating cart item: " + e.getMessage());
        }
    }

    @Override
    public void deleteCartItem(int id) {
        String sql = "DELETE FROM cart_items WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deleting cart item: " + e.getMessage());
        }
    }

    /**
     * Membantu mengekstrak data item keranjang dari ResultSet.
     *
     * @param resultSet Objek ResultSet.
     * @return Objek CartItem.
     * @throws SQLException jika terjadi kesalahan dalam membaca data.
     */
    private CartItem extractCartItemFromResultSet(ResultSet resultSet) throws SQLException {
        CartItem cartItem = new CartItem();
        cartItem.setId(resultSet.getInt("id"));
        cartItem.setCartSellerId(resultSet.getInt("cart_seller_id"));
        cartItem.setProductId(resultSet.getInt("product_id"));
        cartItem.setQuantity(resultSet.getInt("quantity"));
        cartItem.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        cartItem.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
        return cartItem;
    }

}
