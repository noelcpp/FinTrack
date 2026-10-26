package com.fintrack.auth;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.regex.Pattern;

/** Maps each authenticated username to a separate SQLite database file. */
public final class UserDatabasePaths {
    private static final Pattern SAFE_USERNAME = Pattern.compile("[a-z0-9][a-z0-9._-]{2,31}");
    private static final Path PROFILE_DIRECTORY = Path.of("data", "profiles");
    private static final Path LEGACY_DATABASE = Path.of("data", "fintrack.db");
    private static final Path MIGRATION_MARKER = PROFILE_DIRECTORY.resolve(".legacy-migration-complete");

    private UserDatabasePaths() { }

    public static synchronized Path forUsername(String usernameInput) {
        String username = usernameInput == null ? "" : usernameInput.trim().toLowerCase(Locale.ROOT);
        if (!SAFE_USERNAME.matcher(username).matches()) throw new IllegalArgumentException("Invalid username for data storage.");
        Path target = PROFILE_DIRECTORY.resolve(username + ".db").toAbsolutePath().normalize();
        try {
            Files.createDirectories(target.getParent());
            migrateLegacyDatabaseOnce(target);
            return target;
        } catch (IOException ex) {
            throw new IllegalStateException("Could not prepare the user's local database.", ex);
        }
    }

    private static void migrateLegacyDatabaseOnce(Path target) throws IOException {
        Path marker = MIGRATION_MARKER.toAbsolutePath().normalize();
        Path legacy = LEGACY_DATABASE.toAbsolutePath().normalize();
        if (Files.exists(marker)) return;
        boolean copied = false;
        if (Files.exists(legacy) && !Files.exists(target)) {
            Files.copy(legacy, target, StandardCopyOption.COPY_ATTRIBUTES);
            Path legacyWal = Path.of(legacy + "-wal");
            Path legacyShm = Path.of(legacy + "-shm");
            if (Files.exists(legacyWal)) Files.copy(legacyWal, Path.of(target + "-wal"), StandardCopyOption.REPLACE_EXISTING);
            if (Files.exists(legacyShm)) Files.copy(legacyShm, Path.of(target + "-shm"), StandardCopyOption.REPLACE_EXISTING);
            copied = true;
        }
        String note = copied ? "Legacy data assigned once to profile: " + target.getFileName() : "Legacy migration checked for profile: " + target.getFileName();
        Files.writeString(marker, note + System.lineSeparator());
    }
}
