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

public class SQLiteCategoryDAO implements CategoryDAO {
    private final DatabaseConnection db;
    public SQLiteCategoryDAO(DatabaseConnection db) { this.db = db; }
    @Override public List<Category> findAll() {
        try (Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT id,name,type FROM categories ORDER BY type,name"); ResultSet rs=ps.executeQuery()) {
            List<Category> values=new ArrayList<>(); while(rs.next()) values.add(map(rs)); return values;
        } catch(SQLException ex) { throw new DataAccessException("Could not load categories",ex); }
    }
    @Override public Optional<Category> findById(long id) {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("SELECT id,name,type FROM categories WHERE id=?")) {
            ps.setLong(1,id); try(ResultSet rs=ps.executeQuery()) { return rs.next()?Optional.of(map(rs)):Optional.empty(); }
        } catch(SQLException ex) { throw new DataAccessException("Could not find category",ex); }
    }
    @Override public Category save(Category value) {
        try(Connection c=db.getConnection()) {
            if(value.getId()==null) {
                try(PreparedStatement ps=c.prepareStatement("INSERT INTO categories(name,type) VALUES(?,?)",Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1,value.getName()); ps.setString(2,value.getType().name()); ps.executeUpdate();
                    try(ResultSet keys=ps.getGeneratedKeys()) { if(keys.next()) value.setId(keys.getLong(1)); }
                }
            } else {
                try(PreparedStatement ps=c.prepareStatement("UPDATE categories SET name=?,type=? WHERE id=?")) {
                    ps.setString(1,value.getName()); ps.setString(2,value.getType().name()); ps.setLong(3,value.getId());
                    if(ps.executeUpdate()==0) throw new SQLException("Category not found: "+value.getId());
                }
            } return value;
        } catch(SQLException ex) { throw new DataAccessException("Could not save category",ex); }
    }
    @Override public void deleteById(long id) {
        try(Connection c=db.getConnection(); PreparedStatement ps=c.prepareStatement("DELETE FROM categories WHERE id=?")) { ps.setLong(1,id); ps.executeUpdate(); }
        catch(SQLException ex) { throw new DataAccessException("Could not delete category",ex); }
    }
    private Category map(ResultSet rs) throws SQLException { return new Category(rs.getLong("id"),rs.getString("name"),TransactionType.valueOf(rs.getString("type"))); }
}
