package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.event.dto.LongEventDto;
import ru.practicum.ewm.event.dto.SavedEventDto;
import ru.practicum.ewm.event.dto.ShortEventDto;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(source = "category", target = "category.id")
    Event toEvent(SavedEventDto savedEventDto);

    LongEventDto toLongEventDto(Event event);

    List<ShortEventDto> toShortEventDtos(List<Event> events);

    List<LongEventDto> toLongEventDtos(List<Event> events);
}
