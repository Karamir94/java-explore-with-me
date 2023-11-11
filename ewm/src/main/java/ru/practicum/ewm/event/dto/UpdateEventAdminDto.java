package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.enums.StateActionForAdmin;

@Getter
@Setter
public class UpdateEventAdminDto extends UpdateEventDto {
    private StateActionForAdmin stateAction;
}
