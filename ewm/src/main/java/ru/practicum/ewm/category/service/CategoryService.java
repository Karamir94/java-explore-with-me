package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.SavedCategoryDto;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

public interface CategoryService {

    CategoryDto saveCategory(SavedCategoryDto savedCategoryDto) throws SQLIntegrityConstraintViolationException;

    CategoryDto updateCategory(Long id, CategoryDto categoryDto) throws SQLIntegrityConstraintViolationException;

    void deleteCategory(Long id);

    List<CategoryDto> getCategories(int from, int size);

    CategoryDto getCategory(Long id);
}
