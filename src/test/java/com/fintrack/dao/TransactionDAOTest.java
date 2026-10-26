package com.fintrack.dao;

import com.fintrack.dao.sqlite.SQLiteAccountDAO;
import com.fintrack.dao.sqlite.SQLiteCategoryDAO;
import com.fintrack.dao.sqlite.SQLiteTransactionDAO;
import com.fintrack.database.DatabaseConnection;
import com.fintrack.database.DatabaseInitializer;
import com.fintrack.model.Account;
import com.fintrack.model.Category;
import com.fintrack.model.FinanceTransaction;
import com.fintrack.model.enums.AccountType;
import com.fintrack.model.enums.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

class TransactionDAOTest {
    @TempDir Path tempDir;
    @Test void persistsAndReadsTransaction() {
        DatabaseConnection db = new DatabaseConnection(tempDir.resolve("test.db")); new DatabaseInitializer(db).initialize();
        SQLiteCategoryDAO categoryDAO = new SQLiteCategoryDAO(db); SQLiteAccountDAO accountDAO = new SQLiteAccountDAO(db);
        Category category = categoryDAO.findAll().stream().filter(c -> c.getType() == TransactionType.EXPENSE).findFirst().orElseThrow();
        Account account = accountDAO.findAll().get(0);
        SQLiteTransactionDAO dao = new SQLiteTransactionDAO(db);
        FinanceTransaction saved = dao.save(new FinanceTransaction("Test purchase",new BigDecimal("12.50"), LocalDate.of(2026,10,1),TransactionType.EXPENSE,category.getId(),account.getId()));
        FinanceTransaction found = dao.findById(saved.getId()).orElseThrow();
        assertEquals("Test purchase",found.getDescription()); assertEquals(new BigDecimal("12.50"),found.getAmount());
    }
}
