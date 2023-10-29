package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedCompilationDto {

    @NotBlank
    private String title;

    private Boolean pinned;
    private List<Long> events;
}
