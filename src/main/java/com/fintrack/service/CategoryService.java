package com.fintrack.service;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.exception.ValidationException;
import com.fintrack.model.Category;
import com.fintrack.util.InputValidator;
import java.util.List;
import java.util.Optional;

public class CategoryService {
    private final CategoryDAO dao;
    public CategoryService(CategoryDAO dao) { this.dao = dao; }
    public List<Category> findAll() { return dao.findAll(); }
    public Optional<Category> findById(long id) { return dao.findById(id); }
    public Category create(Category category) {
        validate(category); category.setId(null); return dao.save(category);
    }
    public Category update(Category category) {
        validate(category); if (category.getId() == null) throw new ValidationException("Category ID is required for update."); return dao.save(category);
    }
    public void deleteById(long id) { if (id <= 0) throw new ValidationException("Category ID must be positive."); dao.deleteById(id); }
    private void validate(Category category) {
        if (category == null || !InputValidator.isNonBlank(category.getName())) throw new ValidationException("Category name is required.");
        if (category.getName().trim().length() > 60) throw new ValidationException("Category name must be 60 characters or fewer.");
        if (category.getType() == null) throw new ValidationException("Category type is required.");
        category.setName(category.getName().trim());
    }
}
