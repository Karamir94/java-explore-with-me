package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.enums.StateActionForUser;

@Getter
@Setter
public class UpdateEventUserDto extends UpdateEventDto {
    private StateActionForUser stateAction;
}
