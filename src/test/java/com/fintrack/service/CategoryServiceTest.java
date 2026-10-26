package com.fintrack.service;

import com.fintrack.dao.CategoryDAO;
import com.fintrack.exception.ValidationException;
import com.fintrack.model.Category;
import com.fintrack.model.enums.TransactionType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class CategoryServiceTest {
    @Test void rejectsDuplicateNamesIgnoringCaseWithinSameType() {
        CategoryService service = new CategoryService(new MemoryCategoryDAO());
        service.create(new Category("Food", TransactionType.EXPENSE));
        assertThrows(ValidationException.class, () -> service.create(new Category("food", TransactionType.EXPENSE)));
    }

    @Test void permitsSameNameForDifferentTransactionTypes() {
        CategoryService service = new CategoryService(new MemoryCategoryDAO());
        service.create(new Category("Other", TransactionType.EXPENSE));
        assertDoesNotThrow(() -> service.create(new Category("Other", TransactionType.INCOME)));
    }

    private static final class MemoryCategoryDAO implements CategoryDAO {
        private final List<Category> values = new ArrayList<>();
        private long nextId = 1;

        @Override public List<Category> findAll() { return new ArrayList<>(values); }
        @Override public Optional<Category> findById(long id) { return values.stream().filter(c -> c.getId() != null && c.getId() == id).findFirst(); }
        @Override public Category save(Category category) {
            if (category.getId() == null) category.setId(nextId++);
            values.removeIf(existing -> existing.getId().equals(category.getId()));
            values.add(category);
            return category;
        }
        @Override public void deleteById(long id) { values.removeIf(c -> c.getId() != null && c.getId() == id); }
    }
}
