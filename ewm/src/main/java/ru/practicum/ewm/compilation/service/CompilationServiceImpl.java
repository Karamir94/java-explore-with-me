package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationUpdateRequest;
import ru.practicum.ewm.compilation.dto.SavedCompilationDto;
import ru.practicum.ewm.compilation.entity.Compilation;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.error.exception.NotExistException;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.repository.EventRepository;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    public CompilationDto saveCompilation(SavedCompilationDto savedCompilationDto) {
        List<Event> events;
        if (savedCompilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(savedCompilationDto.getEvents());
        } else {
            events = List.of();
        }

        var compilation = Compilation.builder()
                .pinned(savedCompilationDto.getPinned() != null && savedCompilationDto.getPinned())
                .title(savedCompilationDto.getTitle())
                .events(new HashSet<>(events))
                .build();

        var saved = compilationRepository.save(compilation);
        return compilationMapper.mapToCompilationDto(saved);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId,
                                            CompilationUpdateRequest compilationUpdateRequest) {
        var old = compilationRepository.findById(compId).orElseThrow(
                () -> new NotExistException("Compilation does not exist"));
        var eventsIds = compilationUpdateRequest.getEvents();
        if (eventsIds != null) {
            var events = eventRepository.findAllByIdIn(eventsIds);
            old.setEvents(new HashSet<>(events));
        }
        if (compilationUpdateRequest.getPinned() != null)
            old.setPinned(compilationUpdateRequest.getPinned());
        if (compilationUpdateRequest.getTitle() != null && !compilationUpdateRequest.getTitle().isBlank())
            old.setTitle(compilationUpdateRequest.getTitle());

        var updated = compilationRepository.save(old);
        return compilationMapper.mapToCompilationDto(updated);
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        var compilation = compilationRepository.findById(compId).orElseThrow(
                () -> new NotExistException("Compilation does not exist"));
        return compilationMapper.mapToCompilationDto(compilation);
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned,
                                                Integer from,
                                                Integer size) {
        Pageable page = PageRequest.of(from, size, Sort.by(Sort.Direction.ASC, "id"));

        if (pinned == null) {
            return compilationMapper.mapToCompilationDtos(compilationRepository.findAll(page).getContent());
        } else {
            return compilationMapper.mapToCompilationDtos(compilationRepository.findAllByPinned(pinned, page));
        }
    }
}
