package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.ewm.event.entity.Location;
import ru.practicum.ewm.event.enums.StateActionForAdmin;

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
public class UpdateEventAdminDto {

    @Size(min = 20, max = 2000)
    private String annotation;

    private Long category;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(shape = STRING, pattern = DATE_PATTERN)
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @Size(min = 3, max = 120)
    private String title;

    private Long participantLimit;

    private Boolean requestModeration;

    private StateActionForAdmin stateAction;
}
