package ru.practicum.ewm.category.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class SavedCategoryDto {

    @NotBlank
    private String name;
}
