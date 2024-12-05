package com.ecommerce.test;

import com.ecommerce.utils.DatabaseUtils;
import com.ecommerce.dao.impl.UserDAOImpl;
import com.ecommerce.models.User;
import com.ecommerce.models.User.Role;

import java.sql.Connection;
import java.time.LocalDateTime;
import java.util.List;

public class UserTestApp {

    public static void main(String[] args) {
        try (Connection connection = DatabaseUtils.getConnection()) {
            UserDAOImpl userDAO = new UserDAOImpl(connection);

            // 1. Insert User
            System.out.println("=== INSERT USER ===");
            User newUser = new User(0, "testuser", "testuser@example.com", "password123", Role.CUSTOMER, LocalDateTime.now(), LocalDateTime.now());
            userDAO.addUser(newUser);
            System.out.println("User inserted successfully.");

            // 2. Retrieve All Users
            System.out.println("\n=== RETRIEVE ALL USERS ===");
            List<User> users = userDAO.getAllUsers();
            users.forEach(System.out::println);

            // 3. Update User
            System.out.println("\n=== UPDATE USER ===");
            User existingUser = users.get(0); // Assuming we take the first user
            existingUser.setEmail("updateduser@example.com");
            userDAO.updateUser(existingUser);
            System.out.println("User updated successfully.");

            // 4. Retrieve User by ID
            System.out.println("\n=== RETRIEVE USER BY ID ===");
            User retrievedUser = userDAO.getUserById(existingUser.getId());
            System.out.println("Retrieved User: " + retrievedUser);

            // 5. Delete User
            System.out.println("\n=== DELETE USER ===");
            userDAO.deleteUser(existingUser.getId());
            System.out.println("User deleted successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error during database operation: " + e.getMessage());
        }
    }
}
