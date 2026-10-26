package com.fintrack.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnection {
    private final Path databasePath;

    public DatabaseConnection() {
        this(Path.of(System.getProperty("fintrack.db", "data/fintrack.db")));
    }

    public DatabaseConnection(Path databasePath) {
        this.databasePath = databasePath.toAbsolutePath().normalize();
    }

    public Connection getConnection() throws SQLException {
        try {
            Path parent = databasePath.getParent();
            if (parent != null) Files.createDirectories(parent);
        } catch (IOException ex) {
            throw new SQLException("Could not create database directory: " + databasePath, ex);
        }
        Connection connection = DriverManager.getConnection("jdbc:sqlite:" + databasePath);
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
            statement.execute("PRAGMA busy_timeout = 5000");
            return connection;
        } catch (SQLException ex) {
            try {
                connection.close();
            } catch (SQLException closeError) {
                ex.addSuppressed(closeError);
            }
            throw ex;
        }
    }

    public Path getDatabasePath() { return databasePath; }
}
