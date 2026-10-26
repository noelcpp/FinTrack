package com.fintrack.app;

import com.fintrack.dao.AccountDAO;
import com.fintrack.dao.BudgetDAO;
import com.fintrack.dao.CategoryDAO;
import com.fintrack.dao.TransactionDAO;
import com.fintrack.dao.sqlite.SQLiteAccountDAO;
import com.fintrack.dao.sqlite.SQLiteBudgetDAO;
import com.fintrack.dao.sqlite.SQLiteCategoryDAO;
import com.fintrack.dao.sqlite.SQLiteTransactionDAO;
import com.fintrack.database.DatabaseConnection;
import com.fintrack.database.DatabaseInitializer;
import com.fintrack.service.AccountService;
import com.fintrack.service.BudgetService;
import com.fintrack.service.CategoryService;
import com.fintrack.service.DashboardService;
import com.fintrack.service.ReportService;
import com.fintrack.service.TransactionService;

public final class AppContext {
    private final DatabaseConnection databaseConnection;
    private final TransactionDAO transactionDAO;
    private final CategoryDAO categoryDAO;
    private final AccountDAO accountDAO;
    private final BudgetDAO budgetDAO;
    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final AccountService accountService;
    private final BudgetService budgetService;
    private final DashboardService dashboardService;
    private final ReportService reportService;

    public AppContext() {
        databaseConnection = new DatabaseConnection();
        new DatabaseInitializer(databaseConnection).initialize();
        transactionDAO = new SQLiteTransactionDAO(databaseConnection);
        categoryDAO = new SQLiteCategoryDAO(databaseConnection);
        accountDAO = new SQLiteAccountDAO(databaseConnection);
        budgetDAO = new SQLiteBudgetDAO(databaseConnection);
        transactionService = new TransactionService(transactionDAO);
        categoryService = new CategoryService(categoryDAO);
        accountService = new AccountService(accountDAO);
        budgetService = new BudgetService(budgetDAO, transactionDAO);
        dashboardService = new DashboardService(transactionDAO);
        reportService = new ReportService(transactionDAO, categoryDAO);
    }

    public TransactionService getTransactionService() { return transactionService; }
    public CategoryService getCategoryService() { return categoryService; }
    public AccountService getAccountService() { return accountService; }
    public BudgetService getBudgetService() { return budgetService; }
    public DashboardService getDashboardService() { return dashboardService; }
    public ReportService getReportService() { return reportService; }
    public TransactionDAO getTransactionDAO() { return transactionDAO; }
    public CategoryDAO getCategoryDAO() { return categoryDAO; }
    public AccountDAO getAccountDAO() { return accountDAO; }
    public BudgetDAO getBudgetDAO() { return budgetDAO; }
    public DatabaseConnection getDatabaseConnection() { return databaseConnection; }
}
