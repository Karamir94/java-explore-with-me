package ru.practicum.stats.server.hit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;
import ru.practicum.stats.server.hit.mapper.HitMapper;
import ru.practicum.stats.server.hit.mapper.ViewStatsMapper;
import ru.practicum.stats.server.hit.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HitServiceImpl implements HitService {

    private final StatsRepository statsRepository;
    private final HitMapper hitMapper;
    private final ViewStatsMapper viewStatsMapper;

    @Override
    @Transactional
    public void saveHit(HitDto hitDto) {
        statsRepository.save(hitMapper.toEntity(hitDto));
    }

    @Override
    public List<ViewStatsDto> getHits(LocalDateTime start,
                                      LocalDateTime end,
                                      List<String> uris,
                                      boolean unique) {
        return statsRepository.getStats(start, end, uris, unique)
                .stream()
                .map(viewStatsMapper::toDto)
                .collect(toList());
    }
}
