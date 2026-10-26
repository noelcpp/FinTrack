package com.fintrack.dao.sqlite;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.database.DatabaseConnection;
import com.fintrack.exception.DataAccessException;
import com.fintrack.model.Category;
import com.fintrack.model.enums.TransactionType;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** SQLite persistence implementation for categories. */
public class SQLiteCategoryDAO implements CategoryDAO {
    private static final String SELECT_COLUMNS = "SELECT id, name, type FROM categories ";

    private final DatabaseConnection db;

    public SQLiteCategoryDAO(DatabaseConnection db) {
        this.db = db;
    }

    @Override
    public List<Category> findAll() {
        String sql = SELECT_COLUMNS + "ORDER BY "
                + "CASE UPPER(type) "
                + "WHEN 'EXPENSE' THEN 1 "
                + "WHEN 'INCOME' THEN 2 "
                + "ELSE 3 END, "
                + "name COLLATE NOCASE ASC, id ASC";

        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet result = statement.executeQuery()) {
            List<Category> categories = new ArrayList<>();
            while (result.next()) {
                categories.add(map(result));
            }
            return categories;
        } catch (SQLException ex) {
            throw new DataAccessException("Could not load categories", ex);
        }
    }

    @Override
    public Optional<Category> findById(long id) {
        String sql = SELECT_COLUMNS + "WHERE id = ?";
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            try (ResultSet result = statement.executeQuery()) {
                return result.next() ? Optional.of(map(result)) : Optional.empty();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Could not find category " + id, ex);
        }
    }

    @Override
    public Category save(Category category) {
        try (Connection connection = db.getConnection()) {
            if (category.getId() == null) {
                String sql = "INSERT INTO categories(name, type) VALUES(?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    statement.setString(1, category.getName());
                    statement.setString(2, category.getType().name());
                    statement.executeUpdate();
                    try (ResultSet keys = statement.getGeneratedKeys()) {
                        if (keys.next()) {
                            category.setId(keys.getLong(1));
                        }
                    }
                }
            } else {
                String sql = "UPDATE categories SET name = ?, type = ? WHERE id = ?";
                try (PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setString(1, category.getName());
                    statement.setString(2, category.getType().name());
                    statement.setLong(3, category.getId());
                    if (statement.executeUpdate() == 0) {
                        throw new SQLException("Category not found: " + category.getId());
                    }
                }
            }
            return category;
        } catch (SQLException ex) {
            throw new DataAccessException("Could not save category", ex);
        }
    }

    @Override
    public void deleteById(long id) {
        try (Connection connection = db.getConnection();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM categories WHERE id = ?")) {
            statement.setLong(1, id);
            statement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Could not delete category " + id, ex);
        }
    }

    private Category map(ResultSet result) throws SQLException {
        return new Category(
                result.getLong("id"),
                result.getString("name"),
                TransactionType.valueOf(result.getString("type")));
    }
}
