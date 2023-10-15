package ru.practicum.stats.server.hit.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.stats.dto.EndpointHitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.hit.mapper.ViewStatsMapper;
import ru.practicum.stats.server.hit.model.ViewStats;
import ru.practicum.stats.server.hit.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static ru.practicum.stats.server.hit.mapper.EndpointHitMapper.toEndpointHit;
import static ru.practicum.stats.server.hit.mapper.EndpointHitMapper.toEndpointHitDto;

@Slf4j
@Service
@AllArgsConstructor
public class EndpointHitServiceImpl implements EndpointHitService {

    private final EndpointHitRepository endpointHitRepository;

    @Override
    public EndpointHitDto createEndpointHit(EndpointHitDto endpointHitDto) {
        return toEndpointHitDto(endpointHitRepository.save(toEndpointHit(endpointHitDto)));
    }

    @Override
    public List<ViewStatsDto> getEndpointHits(LocalDateTime start,
                                              LocalDateTime end,
                                              List<String> uris,
                                              boolean unique) {
        List<ViewStats> hits;

        if (unique)
            hits = endpointHitRepository.getDistinctEndpointHits(start, end, uris);
        else
            hits = endpointHitRepository.getEndpointHits(start, end, uris);

        return hits.stream()
                .map(ViewStatsMapper::toViewStatsDto)
                .collect(toList());
    }
}