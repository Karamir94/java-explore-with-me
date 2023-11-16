package ru.practicum.ewm.event.entity;

import ru.practicum.ewm.event.dto.ShortEventDto;

import java.util.Comparator;

public class EventDtoViewsComparator implements Comparator<ShortEventDto> {
    @Override
    public int compare(ShortEventDto o1, ShortEventDto o2) {
        return (int) (o1.getViews() - o2.getViews());
    }
}
