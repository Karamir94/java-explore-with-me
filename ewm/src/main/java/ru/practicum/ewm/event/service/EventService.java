package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.SortValue;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    LongEventDto saveEvent(Long userId,
                           SavedEventDto savedEventDto);

    public Event updateEvent(UpdateEventDto updateEventDto, Event event);

    public LongEventDto updateEventByAdmin(Long eventId,
                                           UpdateEventAdminDto updateEventAdminDto);

    LongEventDto updateEventByUser(Long userId,
                                   Long eventId,
                                   UpdateEventUserDto updateEventUserDto);

    List<ShortEventDto> getEvents(Long userId,
                                  Integer from,
                                  Integer size);

    LongEventDto getEventByUser(Long userId,
                                Long eventId);

    LongEventDto getEvent(Long id,
                          HttpServletRequest request);

    List<LongEventDto> getEventsWithParamsByAdmin(List<Long> users,
                                                  EventState states,
                                                  List<Long> categoriesId,
                                                  String rangeStart,
                                                  String rangeEnd,
                                                  Integer from,
                                                  Integer size);

    List<LongEventDto> getEventsWithParamsByUser(String text,
                                                 List<Long> categories,
                                                 Boolean paid,
                                                 String rangeStart,
                                                 String rangeEnd,
                                                 Boolean onlyAvailable,
                                                 SortValue sort,
                                                 Integer from,
                                                 Integer size,
                                                 HttpServletRequest request);
}
