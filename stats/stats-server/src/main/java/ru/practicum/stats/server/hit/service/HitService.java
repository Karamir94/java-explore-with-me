package ru.practicum.stats.server.hit.service;

import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface HitService {

    void saveHit(HitDto hitDto);

    List<ViewStatsDto> getHits(LocalDateTime start,
                               LocalDateTime end,
                               List<String> uris,
                               boolean unique);
}
