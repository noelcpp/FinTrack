package com.fintrack.dao;

import com.fintrack.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryDAO {
    List<Category> findAll();
    Optional<Category> findById(long id);
    Category save(Category value);
    void deleteById(long id);
}
