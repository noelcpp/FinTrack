package com.fintrack.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/** Stores usernames and salted PBKDF2 password hashes in a local authentication database. */
public final class UserStore {
    private static final int ITERATIONS = 210_000;
    private static final int HASH_BITS = 256;
    private static final int SALT_BYTES = 16;
    private static final Pattern USERNAME_PATTERN = Pattern.compile("[a-z0-9][a-z0-9._-]{2,31}");
    private static final SecureRandom RANDOM = new SecureRandom();
    private final Path databasePath;

    public UserStore() { this(Path.of("data", "users.db")); }

    public UserStore(Path databasePath) {
        this.databasePath = databasePath.toAbsolutePath().normalize();
        initialize();
    }

    public void register(String usernameInput, char[] password) {
        byte[] hash = null;
        try {
            String username = normalizeUsername(usernameInput);
            validateUsername(username);
            validatePassword(password);
            byte[] salt = new byte[SALT_BYTES];
            RANDOM.nextBytes(salt);
            hash = hash(password, salt, ITERATIONS);
            String sql = "INSERT INTO app_users(username, salt, password_hash, iterations, created_at) VALUES(?,?,?,?,?)";
            try (Connection connection = connect(); PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);
                statement.setBytes(2, salt);
                statement.setBytes(3, hash);
                statement.setInt(4, ITERATIONS);
                statement.setString(5, Instant.now().toString());
                statement.executeUpdate();
            }
        } catch (SQLException ex) {
            if (ex.getMessage() != null && ex.getMessage().toLowerCase(Locale.ROOT).contains("unique")) {
                throw new IllegalArgumentException("That username is already registered.");
            }
            throw new IllegalStateException("Could not create the local user account.", ex);
        } finally {
            if (hash != null) Arrays.fill(hash, (byte) 0);
            clearPassword(password);
        }
    }

    public boolean authenticate(String usernameInput, char[] password) {
        if (password == null) return false;
        try {
            String username = normalizeUsername(usernameInput);
            try {
                validateUsername(username);
            } catch (IllegalArgumentException ex) {
                return false;
            }
            try (Connection connection = connect();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT salt, password_hash, iterations FROM app_users WHERE username = ?")) {
                statement.setString(1, username);
                try (ResultSet result = statement.executeQuery()) {
                    if (!result.next()) return false;
                    byte[] salt = result.getBytes("salt");
                    byte[] expected = result.getBytes("password_hash");
                    int iterations = result.getInt("iterations");
                    byte[] actual = hash(password, salt, iterations);
                    try {
                        return MessageDigest.isEqual(expected, actual);
                    } finally {
                        Arrays.fill(actual, (byte) 0);
                        Arrays.fill(expected, (byte) 0);
                        Arrays.fill(salt, (byte) 0);
                    }
                }
            }
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not verify the local user account.", ex);
        } finally {
            clearPassword(password);
        }
    }

    private static void clearPassword(char[] password) {
        if (password != null) Arrays.fill(password, '\0');
    }

    public Path getDatabasePath() { return databasePath; }

    private void initialize() {
        try {
            Path parent = databasePath.getParent();
            if (parent != null) Files.createDirectories(parent);
        } catch (IOException ex) {
            throw new IllegalStateException("Could not create the local data directory.", ex);
        }
        try (Connection connection = connect(); Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS app_users (" +
                    "username TEXT PRIMARY KEY COLLATE NOCASE, " +
                    "salt BLOB NOT NULL, password_hash BLOB NOT NULL, iterations INTEGER NOT NULL, created_at TEXT NOT NULL)");
        } catch (SQLException ex) {
            throw new IllegalStateException("Could not initialize local authentication storage.", ex);
        }
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databasePath);
    }

    private static String normalizeUsername(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static void validateUsername(String username) {
        if (!USERNAME_PATTERN.matcher(username).matches()) {
            throw new IllegalArgumentException("Username must be 3–32 characters and start with a letter or number. Use only letters, numbers, dots, underscores or hyphens.");
        }
    }

    private static void validatePassword(char[] password) {
        if (password == null || password.length < 8) {
            throw new IllegalArgumentException("Password must contain at least 8 characters.");
        }
        if (password.length > 256) throw new IllegalArgumentException("Password is too long.");
    }

    private static byte[] hash(char[] password, byte[] salt, int iterations) {
        PBEKeySpec spec = new PBEKeySpec(password, salt, iterations, HASH_BITS);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException ex) {
            throw new IllegalStateException("Password hashing is unavailable on this Java runtime.", ex);
        } finally {
            spec.clearPassword();
        }
    }
}
