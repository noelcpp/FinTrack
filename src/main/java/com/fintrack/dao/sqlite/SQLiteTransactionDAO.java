package com.fintrack.dao.sqlite;

import com.fintrack.dao.TransactionDAO;
import com.fintrack.database.DatabaseConnection;
import com.fintrack.exception.DataAccessException;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.TransactionType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteTransactionDAO implements TransactionDAO {
    private final DatabaseConnection db;
    public SQLiteTransactionDAO(DatabaseConnection db) { this.db = db; }

    @Override public List<FinanceTransaction> findAll() {
        String sql = "SELECT id, description, amount, transaction_date, type, category_id, account_id FROM transactions ORDER BY transaction_date DESC, id DESC";
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            List<FinanceTransaction> result = new ArrayList<>();
            while (rs.next()) result.add(map(rs));
            return result;
        } catch (SQLException ex) { throw new DataAccessException("Could not load transactions", ex); }
    }

    @Override public Optional<FinanceTransaction> findById(long id) {
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement("SELECT * FROM transactions WHERE id = ?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) { return rs.next() ? Optional.of(map(rs)) : Optional.empty(); }
        } catch (SQLException ex) { throw new DataAccessException("Could not find transaction " + id, ex); }
    }

    @Override public FinanceTransaction save(FinanceTransaction tx) {
        try (Connection c = db.getConnection()) {
            if (tx.getId() == null) {
                String sql = "INSERT INTO transactions(description, amount, transaction_date, type, category_id, account_id) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    bind(ps, tx); ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) { if (keys.next()) tx.setId(keys.getLong(1)); }
                }
            } else {
                String sql = "UPDATE transactions SET description=?, amount=?, transaction_date=?, type=?, category_id=?, account_id=? WHERE id=?";
                try (PreparedStatement ps = c.prepareStatement(sql)) {
                    bind(ps, tx); ps.setLong(7, tx.getId());
                    if (ps.executeUpdate() == 0) throw new SQLException("Transaction not found: " + tx.getId());
                }
            }
            return tx;
        } catch (SQLException ex) { throw new DataAccessException("Could not save transaction", ex); }
    }

    private void bind(PreparedStatement ps, FinanceTransaction tx) throws SQLException {
        ps.setString(1, tx.getDescription()); ps.setString(2, tx.getAmount().toPlainString());
        ps.setString(3, tx.getDate().toString()); ps.setString(4, tx.getType().name());
        if (tx.getCategoryId() == null) ps.setNull(5, java.sql.Types.INTEGER); else ps.setLong(5, tx.getCategoryId());
        ps.setLong(6, tx.getAccountId());
    }

    @Override public void deleteById(long id) {
        try (Connection c = db.getConnection(); PreparedStatement ps = c.prepareStatement("DELETE FROM transactions WHERE id=?")) {
            ps.setLong(1, id); ps.executeUpdate();
        } catch (SQLException ex) { throw new DataAccessException("Could not delete transaction " + id, ex); }
    }

    private FinanceTransaction map(ResultSet rs) throws SQLException {
        long category = rs.getLong("category_id"); Long categoryId = rs.wasNull() ? null : category;
        return new FinanceTransaction(rs.getLong("id"), rs.getString("description"), new BigDecimal(rs.getString("amount")),
                LocalDate.parse(rs.getString("transaction_date")), TransactionType.valueOf(rs.getString("type")), categoryId,
                rs.getLong("account_id"));
    }
}
