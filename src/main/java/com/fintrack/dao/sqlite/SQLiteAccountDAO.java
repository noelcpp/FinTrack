package com.fintrack.dao.sqlite;

import com.fintrack.dao.AccountDAO;
import com.fintrack.database.DatabaseConnection;
import com.fintrack.exception.DataAccessException;
import com.fintrack.model.Account;
import com.fintrack.model.enums.AccountType;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SQLiteAccountDAO implements AccountDAO {
    private final DatabaseConnection db;
    public SQLiteAccountDAO(DatabaseConnection db) { this.db = db; }
    @Override public List<Account> findAll() {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT id,name,type,opening_balance FROM accounts ORDER BY name"); ResultSet rs=ps.executeQuery()) {
            List<Account> values=new ArrayList<>(); while(rs.next()) values.add(map(rs)); return values;
        } catch(SQLException ex) { throw new DataAccessException("Could not load accounts",ex); }
    }
    @Override public Optional<Account> findById(long id) {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT id,name,type,opening_balance FROM accounts WHERE id=?")) {
            ps.setLong(1,id); try(ResultSet rs=ps.executeQuery()) { return rs.next()?Optional.of(map(rs)):Optional.empty(); }
        } catch(SQLException ex) { throw new DataAccessException("Could not find account",ex); }
    }
    @Override public Account save(Account value) {
        try(Connection c=db.getConnection()) {
            if(value.getId()==null) {
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO accounts(name,type,opening_balance) VALUES(?,?,?)",Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1,value.getName()); ps.setString(2,value.getType().name()); ps.setString(3,value.getOpeningBalance().toPlainString()); ps.executeUpdate();
                    try(ResultSet keys=ps.getGeneratedKeys()) { if(keys.next()) value.setId(keys.getLong(1)); }
                }
            } else {
                try(PreparedStatement ps=c.prepareStatement("UPDATE accounts SET name=?,type=?,opening_balance=? WHERE id=?")) {
                    ps.setString(1,value.getName()); ps.setString(2,value.getType().name()); ps.setString(3,value.getOpeningBalance().toPlainString()); ps.setLong(4,value.getId());
                    if(ps.executeUpdate()==0) throw new SQLException("Account not found: "+value.getId());
                }
            } return value;
        } catch(SQLException ex) { throw new DataAccessException("Could not save account",ex); }
    }
    @Override public void deleteById(long id) {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("DELETE FROM accounts WHERE id=?")) { ps.setLong(1,id); ps.executeUpdate(); }
        catch(SQLException ex) { throw new DataAccessException("Could not delete account",ex); }
    }
    private Account map(ResultSet rs) throws SQLException { return new Account(rs.getLong("id"),rs.getString("name"),AccountType.valueOf(rs.getString("type")),new BigDecimal(rs.getString("opening_balance"))); }
}
