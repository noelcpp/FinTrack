package com.fintrack.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class UserStoreTest {
    @TempDir Path tempDir;

    @Test void registersAndAuthenticatesUsingHashedPassword() {
        UserStore users = new UserStore(tempDir.resolve("users.db"));
        users.register("Noel", "SafePass123".toCharArray());

        assertTrue(users.authenticate("noel", "SafePass123".toCharArray()));
        assertTrue(users.authenticate("NOEL", "SafePass123".toCharArray()));
        assertFalse(users.authenticate("noel", "WrongPass123".toCharArray()));
    }

    @Test void rejectsDuplicateUsernamesIgnoringCase() {
        UserStore users = new UserStore(tempDir.resolve("users.db"));
        users.register("noel", "SafePass123".toCharArray());
        assertThrows(IllegalArgumentException.class, () -> users.register("NOEL", "AnotherPass123".toCharArray()));
    }

    @Test void rejectsWeakPasswordsAndClearsProvidedPasswordBuffer() {
        UserStore users = new UserStore(tempDir.resolve("users.db"));
        char[] weakPassword = "short".toCharArray();
        assertThrows(IllegalArgumentException.class, () -> users.register("noel", weakPassword));
        assertArrayEquals(new char[weakPassword.length], weakPassword);
    }

    @Test void rejectsInvalidUsernamesAndClearsProvidedPasswordBuffer() {
        UserStore users = new UserStore(tempDir.resolve("users.db"));
        char[] password = "SafePass123".toCharArray();
        assertThrows(IllegalArgumentException.class, () -> users.register("x", password));
        assertArrayEquals(new char[password.length], password);
    }
}
