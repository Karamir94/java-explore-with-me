package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ru.practicum.ewm.event.entity.Event;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    Page<Event> findAllByInitiatorId(Long userId, Pageable page);

    Optional<Event> findByIdAndPublishedOnIsNotNull(Long id);

    List<Event> findAllByIdIn(Set<Long> eventIds);

    Boolean existsByCategoryId(Long id);
}
