package com.ecommerce.dao.impl;

import com.ecommerce.dao.UserDAO;
import com.ecommerce.models.User;
import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.utils.PasswordUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAOImpl implements UserDAO {

    private static final String INSERT_USER = "INSERT INTO users (username, email, password, role, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_USER_BY_ID = "SELECT * FROM users WHERE id = ?";
    private static final String SELECT_ALL_USERS = "SELECT * FROM users";
    private static final String UPDATE_USER = "UPDATE users SET username = ?, email = ?, password = ?, role = ?, updated_at = ? WHERE id = ?";
    private static final String DELETE_USER = "DELETE FROM users WHERE id = ?";

    @Override
    public void addUser(User user) {
        try (Connection connection = DatabaseUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(INSERT_USER)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, PasswordUtils.encrypt(user.getPassword()));
            preparedStatement.setString(4, user.getRole().toString());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(user.getCreatedAt()));
            preparedStatement.setTimestamp(6, Timestamp.valueOf(user.getUpdatedAt()));

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public User getUserById(int id) {
        User user = null;
        try (Connection connection = DatabaseUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(SELECT_USER_BY_ID)) {

            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                user = extractUserFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection connection = DatabaseUtils.getConnection(); Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(SELECT_ALL_USERS)) {

            while (resultSet.next()) {
                users.add(extractUserFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    @Override
    public void updateUser(User user) {
        try (Connection connection = DatabaseUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_USER)) {

            preparedStatement.setString(1, user.getUsername());
            preparedStatement.setString(2, user.getEmail());
            preparedStatement.setString(3, PasswordUtils.encrypt(user.getPassword()));
            preparedStatement.setString(4, user.getRole().toString());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(user.getUpdatedAt()));
            preparedStatement.setInt(6, user.getId());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteUser(int id) {
        try (Connection connection = DatabaseUtils.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(DELETE_USER)) {

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Helper method to extract User object from ResultSet
    private User extractUserFromResultSet(ResultSet resultSet) throws SQLException {
        User user = new User();
        user.setId(resultSet.getInt("id"));
        user.setUsername(resultSet.getString("username"));
        user.setEmail(resultSet.getString("email"));
        user.setPassword(resultSet.getString("password")); // Stored as hashed password
        user.setRole(User.Role.fromString(resultSet.getString("role")));
        user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
        user.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());

        return user;
    }
}
