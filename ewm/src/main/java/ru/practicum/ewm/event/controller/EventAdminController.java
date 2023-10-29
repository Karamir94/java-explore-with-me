package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.LongEventDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminDto;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class EventAdminController {

    private final EventService eventService;

    @GetMapping("/events")
    public List<LongEventDto> getEvents(@RequestParam(required = false, defaultValue = "10") Integer size,
                                        @RequestParam(required = false, defaultValue = "0") Integer from,
                                        @RequestParam(required = false) List<Long> categories,
                                        @RequestParam(required = false) EventState states,
                                        @RequestParam(required = false) String rangeStart,
                                        @RequestParam(required = false) List<Long> users,
                                        @RequestParam(required = false) String rangeEnd) {
        return eventService.getEventsWithParamsByAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
    }

    @PatchMapping("/events/{eventId}")
    public LongEventDto updateEvent(@Valid @RequestBody UpdateEventAdminDto updateEventAdminDto,
                                    @PathVariable Long eventId) {
        return eventService.updateEvent(eventId, updateEventAdminDto);
    }
}