package com.ecommerce.db.migrations;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.Statement;

public class MigrationService {
    private final Connection connection;

    public MigrationService(Connection connection) {
        this.connection = connection;
    }

    public void migrateUp() throws Exception {
        executeSQLFromFile("src/main/resources/db/migration/migrate_up.sql");
    }

    public void migrateDown() throws Exception {
        executeSQLFromFile("src/main/resources/db/migration/migrate_down.sql");
    }

    private void executeSQLFromFile(String filePath) throws Exception {
        String sql = new String(Files.readAllBytes(Paths.get(filePath)));

        // Memecah SQL menjadi perintah individual berdasarkan tanda ';'
        String[] queries = sql.split(";");
        try (Statement statement = connection.createStatement()) {
            for (String query : queries) {
                query = query.trim(); // Menghapus spasi atau newline yang tidak perlu
                if (!query.isEmpty()) { // Menghindari eksekusi perintah kosong
                    statement.execute(query + ";");
                }
            }
        }
    }
}
