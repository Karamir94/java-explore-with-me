package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.event.entity.Location;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonFormat.Shape.STRING;
import static ru.practicum.ewm.utils.Patterns.DATE_PATTERN;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class SavedEventDto {

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    @NotNull
    @Size(min = 3, max = 120)
    private String title;

    @NotNull
    private Long category;

    @NotNull
    @Size(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDateTime eventDate;

    @NotNull
    private Location location;

    private boolean paid;

    private int participantLimit;

    private Boolean requestModeration;
}
