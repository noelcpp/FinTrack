package com.fintrack.database;

import com.fintrack.exception.DataAccessException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    private final DatabaseConnection databaseConnection;

    public DatabaseInitializer(DatabaseConnection databaseConnection) {
        this.databaseConnection = databaseConnection;
    }

    public void initialize() {
        try (InputStream input = getClass().getResourceAsStream("/db/schema.sql")) {
            if (input == null) throw new DataAccessException("Missing resource /db/schema.sql", null);
            String schema = new String(input.readAllBytes(), StandardCharsets.UTF_8);
            try (Connection connection = databaseConnection.getConnection(); Statement statement = connection.createStatement()) {
                for (String sql : schema.split(";")) {
                    String trimmed = sql.trim();
                    if (!trimmed.isEmpty()) statement.execute(trimmed);
                }
                statement.executeUpdate("INSERT OR IGNORE INTO accounts(name, type, opening_balance) VALUES ('Cash', 'CASH', '0.00')");
                statement.executeUpdate("INSERT OR IGNORE INTO categories(name, type) VALUES ('Food', 'EXPENSE')");
                statement.executeUpdate("INSERT OR IGNORE INTO categories(name, type) VALUES ('Transport', 'EXPENSE')");
                statement.executeUpdate("INSERT OR IGNORE INTO categories(name, type) VALUES ('Bills', 'EXPENSE')");
                statement.executeUpdate("INSERT OR IGNORE INTO categories(name, type) VALUES ('Shopping', 'EXPENSE')");
                statement.executeUpdate("INSERT OR IGNORE INTO categories(name, type) VALUES ('Salary', 'INCOME')");
                statement.executeUpdate("INSERT OR IGNORE INTO categories(name, type) VALUES ('Other income', 'INCOME')");
            }
        } catch (IOException | SQLException ex) {
            throw new DataAccessException("Could not initialize the FinTrack database", ex);
        }
    }
}
