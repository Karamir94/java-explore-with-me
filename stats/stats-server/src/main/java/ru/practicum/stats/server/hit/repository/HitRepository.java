package ru.practicum.stats.server.hit.repository;

import ru.practicum.stats.server.hit.model.ViewStats;

import java.time.LocalDateTime;
import java.util.List;

public interface HitRepository {

    List<ViewStats> getStats(LocalDateTime start,
                             LocalDateTime end,
                             List<String> uris,
                             boolean unique);
}
