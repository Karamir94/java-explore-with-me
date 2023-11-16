package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.SavedCategoryDto;
import ru.practicum.ewm.category.entity.Category;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(SavedCategoryDto newCategoryDto);

    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoryDtos(List<Category> categoryList);
}
