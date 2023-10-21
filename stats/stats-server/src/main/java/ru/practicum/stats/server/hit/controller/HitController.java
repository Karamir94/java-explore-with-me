package ru.practicum.stats.server.hit.controller;

import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.hit.service.HitService;
import ru.practicum.stats.server.hit.utils.Patterns;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@AllArgsConstructor
public class HitController {

    private final HitService hitService;

    @PostMapping("/hit")
    @ResponseStatus(CREATED)
    public void saveHit(@RequestBody @Valid HitDto hitDto) {
        hitService.saveHit(hitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatsDto> getStats(@DateTimeFormat(pattern = Patterns.DATE_PATTERN) LocalDateTime start,
                                       @DateTimeFormat(pattern = Patterns.DATE_PATTERN) LocalDateTime end,
                                       @RequestParam(defaultValue = "false") boolean unique,
                                       @RequestParam(required = false) List<String> uris) {
        return hitService.getHits(start, end, uris, unique);
    }
}
