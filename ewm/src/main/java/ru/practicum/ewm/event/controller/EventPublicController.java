package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.LongEventDto;
import ru.practicum.ewm.event.dto.ShortEventDto;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventPublicController {

    private final EventService eventService;

    @GetMapping("/{id}")
    public LongEventDto getEvent(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEvent(id, request);
    }

    @GetMapping
    public List<ShortEventDto> getEventsWithParamsByUser(@RequestParam(defaultValue = "10") @Positive Integer size,
                                                         @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                                         @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                         @RequestParam(required = false) List<Long> categories,
                                                         @RequestParam(required = false) String rangeStart,
                                                         @RequestParam(required = false) String rangeEnd,
                                                         @RequestParam(required = false) String sort,
                                                         @RequestParam(required = false) Boolean paid,
                                                         @RequestParam(required = false) String text,
                                                         HttpServletRequest request) {
        return eventService.getEventsWithParamsByUser(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size, request);
    }
}
