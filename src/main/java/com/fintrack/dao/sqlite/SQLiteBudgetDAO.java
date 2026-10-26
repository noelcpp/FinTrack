package com.fintrack.dao.sqlite;

import com.fintrack.dao.BudgetDAO;
import com.fintrack.database.DatabaseConnection;
import com.fintrack.exception.DataAccessException;
import com.fintrack.model.Budget;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteBudgetDAO implements BudgetDAO {
    private final DatabaseConnection db;
    public SQLiteBudgetDAO(DatabaseConnection db) { this.db = db; }
    @Override public List<Budget> findAll() {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT id,category_id,budget_month,limit_amount FROM budgets ORDER BY budget_month DESC, id ASC"); ResultSet rs=ps.executeQuery()) {
            List<Budget> values=new ArrayList<>(); while(rs.next()) values.add(map(rs)); return values;
        } catch(SQLException ex) { throw new DataAccessException("Could not load budgets",ex); }
    }
    @Override public Optional<Budget> findById(long id) {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT id,category_id,budget_month,limit_amount FROM budgets WHERE id=?")) {
            ps.setLong(1,id); try(ResultSet rs=ps.executeQuery()) { return rs.next()?Optional.of(map(rs)):Optional.empty(); }
        } catch(SQLException ex) { throw new DataAccessException("Could not find budget",ex); }
    }
    @Override public Budget save(Budget value) {
        try(Connection c=db.getConnection()) {
            if(value.getId()==null) {
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO budgets(category_id,budget_month,limit_amount) VALUES(?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                    ps.setLong(1,value.getCategoryId()); ps.setString(2,value.getMonth().toString()); ps.setString(3,value.getLimitAmount().toPlainString()); ps.executeUpdate();
                    try(ResultSet keys=ps.getGeneratedKeys()) { if(keys.next()) value.setId(keys.getLong(1)); }
                }
            } else {
                try(PreparedStatement ps=c.prepareStatement("UPDATE budgets SET category_id=?,budget_month=?,limit_amount=? WHERE id=?")) {
                    ps.setLong(1,value.getCategoryId()); ps.setString(2,value.getMonth().toString()); ps.setString(3,value.getLimitAmount().toPlainString()); ps.setLong(4,value.getId());
                    if(ps.executeUpdate()==0) throw new SQLException("Budget not found: "+value.getId());
                }
            } return value;
        } catch(SQLException ex) { throw new DataAccessException("Could not save budget",ex); }
    }
    @Override public void deleteById(long id) {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("DELETE FROM budgets WHERE id=?")) { ps.setLong(1,id); ps.executeUpdate(); }
        catch(SQLException ex) { throw new DataAccessException("Could not delete budget",ex); }
    }
    private Budget map(ResultSet rs) throws SQLException { return new Budget(rs.getLong("id"),rs.getLong("category_id"),YearMonth.parse(rs.getString("budget_month")),new BigDecimal(rs.getString("limit_amount"))); }
}
