package com.fintrack.service;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.exception.ValidationException;
import com.fintrack.model.Category;
import com.fintrack.util.InputValidator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/** Validates category operations before delegating to the persistence layer. */
public class CategoryService {
    private final CategoryDAO dao;

    public CategoryService(CategoryDAO dao) {
        this.dao = Objects.requireNonNull(dao, "dao");
    }

    public List<Category> findAll() {
        return dao.findAll();
    }

    public Optional<Category> findById(long id) {
        if (id <= 0) {
            throw new ValidationException("Category ID must be positive.");
        }
        return dao.findById(id);
    }

    public Category create(Category category) {
        if (category != null) {
            category.setId(null);
        }
        validate(category);
        return dao.save(category);
    }

    public Category update(Category category) {
        if (category == null || category.getId() == null || category.getId() <= 0) {
            throw new ValidationException("A valid category ID is required for update.");
        }
        validate(category);
        return dao.save(category);
    }

    public void deleteById(long id) {
        if (id <= 0) {
            throw new ValidationException("Category ID must be positive.");
        }
        dao.deleteById(id);
    }

    private void validate(Category category) {
        if (category == null || !InputValidator.isNonBlank(category.getName())) {
            throw new ValidationException("Category name is required.");
        }
        String normalizedName = category.getName().trim();
        if (normalizedName.length() > 60) {
            throw new ValidationException("Category name must be 60 characters or fewer.");
        }
        if (category.getType() == null) {
            throw new ValidationException("Category type is required.");
        }

        boolean duplicate = dao.findAll().stream()
                .anyMatch(existing -> existing.getType() == category.getType()
                        && existing.getName().equalsIgnoreCase(normalizedName)
                        && !Objects.equals(existing.getId(), category.getId()));
        if (duplicate) {
            throw new ValidationException("A " + category.getType().toString().toLowerCase(java.util.Locale.ROOT)
                    + " category named '" + normalizedName + "' already exists.");
        }
        category.setName(normalizedName);
    }
}
