package com.ecommerce.db.migrations;

import com.ecommerce.db.DatabaseUtils;

import java.sql.Connection;

public class DatabaseMigrator {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: java DatabaseMigrator [up|down]");
            return;
        }

        try (Connection connection = DatabaseUtils.getConnection()) {
            MigrationService migrationService = new MigrationService(connection);

            switch (args[0].toLowerCase()) {
                case "up":
                    System.out.println("Applying migrations...");
                    migrationService.migrateUp();
                    System.out.println("Migrations applied successfully.");
                    break;

                case "down":
                    System.out.println("Reverting migrations...");
                    migrationService.migrateDown();
                    System.out.println("Migrations reverted successfully.");
                    break;

                default:
                    System.out.println("Invalid command. Use 'up' for migrations or 'down' to revert migrations.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
