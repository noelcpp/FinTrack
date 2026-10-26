CREATE TABLE IF NOT EXISTS categories (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL COLLATE NOCASE,
    type TEXT NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    UNIQUE(name, type)
);

CREATE TABLE IF NOT EXISTS accounts (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL UNIQUE,
    type TEXT NOT NULL CHECK (type IN ('CASH', 'BANK', 'CREDIT_CARD', 'WALLET')),
    opening_balance TEXT NOT NULL DEFAULT '0.00'
);

CREATE TABLE IF NOT EXISTS transactions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    description TEXT NOT NULL,
    amount TEXT NOT NULL,
    transaction_date TEXT NOT NULL,
    type TEXT NOT NULL CHECK (type IN ('INCOME', 'EXPENSE')),
    category_id INTEGER,
    account_id INTEGER NOT NULL,
    FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE SET NULL,
    FOREIGN KEY(account_id) REFERENCES accounts(id) ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS budgets (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    category_id INTEGER NOT NULL,
    budget_month TEXT NOT NULL,
    limit_amount TEXT NOT NULL,
    UNIQUE(category_id, budget_month),
    FOREIGN KEY(category_id) REFERENCES categories(id) ON DELETE CASCADE
);
