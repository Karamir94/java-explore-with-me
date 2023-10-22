package ru.practicum.stats.server.hit.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.server.hit.model.Hit;
import ru.practicum.stats.server.hit.utils.Patterns;

@Mapper(componentModel = "spring")
public interface HitMapper {

    @Mapping(target = "timestamp", source = "timestamp", dateFormat = Patterns.DATE_PATTERN)
    Hit toEntity(HitDto hitDto);
}