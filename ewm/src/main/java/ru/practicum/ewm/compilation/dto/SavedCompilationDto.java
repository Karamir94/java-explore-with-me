package ru.practicum.ewm.compilation.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SavedCompilationDto {

    @NotBlank
    @Size(max = 50)
    private String title;

    private Boolean pinned;
    private Set<Long> events;
}
