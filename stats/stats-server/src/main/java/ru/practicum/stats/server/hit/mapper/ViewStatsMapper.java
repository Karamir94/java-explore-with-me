package ru.practicum.stats.server.hit.mapper;

import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.hit.model.ViewStats;

public class ViewStatsMapper {

    public static ViewStatsDto toViewStatsDto(ViewStats viewStats) {
        return ViewStatsDto.builder()
                .hits(viewStats.getHits())
                .uri(viewStats.getUri())
                .app(viewStats.getApp())
                .build();
    }
}