package ru.practicum.stats.server.hit.mapper;

import org.mapstruct.Mapper;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.hit.model.ViewStats;

@Mapper(componentModel = "spring")
public interface ViewStatsMapper {

    ViewStatsDto toDto(ViewStats viewStats);
}