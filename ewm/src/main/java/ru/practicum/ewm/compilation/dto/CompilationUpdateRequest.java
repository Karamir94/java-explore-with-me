package ru.practicum.ewm.compilation.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompilationUpdateRequest {

    private String title;
    private Boolean pinned;
    private List<Long> events;
}
