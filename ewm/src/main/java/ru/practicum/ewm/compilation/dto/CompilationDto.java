package ru.practicum.ewm.compilation.dto;

import lombok.*;
import ru.practicum.ewm.event.dto.ShortEventDto;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationDto {

    private Long id;
    private String title;
    private Boolean pinned;
    private List<ShortEventDto> events;
}
