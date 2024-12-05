package com.ecommerce.config;

public class DatabaseConfig {

    private static final String URL = "jdbc:mysql://localhost:3306/ecommerce-test";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static String getUrl() {
        return URL;
    }

    public static String getUser() {
        return USER;
    }

    public static String getPassword() {
        return PASSWORD;
    }
}
