package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import ru.practicum.ewm.event.dto.LocationDto;
import ru.practicum.ewm.event.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toLocationDto(Location location);

    Location toLocation(LocationDto locationDto);
}
