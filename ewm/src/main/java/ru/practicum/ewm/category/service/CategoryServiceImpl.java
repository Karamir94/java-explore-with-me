package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.SavedCategoryDto;
import ru.practicum.ewm.category.exception.CategoryNotEmptyException;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.NotExistException;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.List;

import static org.springframework.data.domain.PageRequest.of;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto saveCategory(SavedCategoryDto savedCategoryDto) {
        var entity = categoryMapper.toCategory(savedCategoryDto);
        var saved = categoryRepository.save(entity);
        return categoryMapper.toCategoryDto(saved);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long id, CategoryDto categoryDto) {
        var category = categoryRepository.findById(id).orElseThrow(
                () -> new NotExistException("Category#" + id + " does not exist"));
        if (category.getName().equals(categoryDto.getName())) {
            return categoryMapper.toCategoryDto(category);
        }
        category.setName(categoryDto.getName());
        var saved = categoryRepository.save(category);
        return categoryMapper.toCategoryDto(saved);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        if (eventRepository.existsByCategoryId(id))
            throw new CategoryNotEmptyException("Category#" + id + " is not empty");
        categoryRepository.deleteById(id);
    }

    @Override
    public List<CategoryDto> getCategories(int from, int size) {
        return categoryMapper.toCategoryDtos(categoryRepository.findAll(of(from / size, size)).toList());
    }

    @Override
    public CategoryDto getCategory(Long id) {
        var category = categoryRepository.findById(id).orElseThrow(
                () -> new NotExistException("Category#" + id + " does not exist"));
        return categoryMapper.toCategoryDto(category);
    }
}
