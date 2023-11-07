package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.entity.Location;
import ru.practicum.ewm.event.enums.EventState;

import javax.validation.constraints.NotBlank;
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
public class LongEventDto {
    private Long id;

    @NotBlank
    @Size(min = 20, max = 2000)
    private String annotation;

    private CategoryDto category;

    private Integer confirmedRequests;

    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private String createdOn;

    @Size(max = 7000)
    private String description;

    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDateTime eventDate;

    private ShortEventDto initiator;

    private Location location;

    private Boolean paid;

    private Long participantLimit;

    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDateTime publishedOn;

    private Boolean requestModeration;

    @Size (max = 30)
    private EventState state;

    @Size(max = 120)
    private String title;

    private Long views;
}
