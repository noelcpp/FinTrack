package com.fintrack.dao;

import com.fintrack.dao.sqlite.SQLiteCategoryDAO;
import com.fintrack.database.DatabaseConnection;
import com.fintrack.database.DatabaseInitializer;
import com.fintrack.model.Category;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CategoryDAOTest {
    @TempDir Path tempDir;

    @Test void listsExpensesFirstThenIncomeAndAlphabeticallyWithinEachGroup() {
        DatabaseConnection database = new DatabaseConnection(tempDir.resolve("categories.db"));
        new DatabaseInitializer(database).initialize();

        List<String> names = new SQLiteCategoryDAO(database).findAll().stream()
                .map(Category::getName)
                .toList();

        assertEquals(List.of("Bills", "Food", "Shopping", "Transport", "Other income", "Salary"), names);
    }
}
