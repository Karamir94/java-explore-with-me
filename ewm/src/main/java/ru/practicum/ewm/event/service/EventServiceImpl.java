package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.BadParamException;
import ru.practicum.ewm.error.exception.NotExistException;
import ru.practicum.ewm.error.exception.WrongTimeException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.Location;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.enums.SortValue;
import ru.practicum.ewm.event.exception.EventCanceledException;
import ru.practicum.ewm.event.exception.EventPublishedException;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.utils.Patterns;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

import javax.persistence.EntityManager;
import javax.persistence.criteria.Predicate;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.time.LocalDateTime.now;
import static java.time.LocalDateTime.parse;
import static org.springframework.data.domain.PageRequest.of;
import static ru.practicum.ewm.event.enums.EventState.*;
import static ru.practicum.ewm.event.enums.StateActionForAdmin.PUBLISH_EVENT;
import static ru.practicum.ewm.event.enums.StateActionForAdmin.REJECT_EVENT;
import static ru.practicum.ewm.event.enums.StateActionForUser.SEND_TO_REVIEW;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {

    ApplicationContext context =
            new AnnotationConfigApplicationContext("ru.practicum.stats.client");
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EntityManager entityManager;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final StatsClient statsClient = context.getBean(StatsClient.class);
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN);

    @Override
    @Transactional
    public LongEventDto saveEvent(Long userId,
                                  SavedEventDto savedEventDto) {
        var category = categoryRepository.findById(savedEventDto.getCategory()).orElseThrow(
                () -> new NotExistException("This category does not exist"));
        var eventDate = savedEventDto.getEventDate();

        if (eventDate.isBefore(now().plusHours(2)))
            throw new WrongTimeException("EventDate should be in future");

        Location location = locationMapper.toLocation(savedEventDto.getLocation());
        var event = eventMapper.toEvent(savedEventDto);

        event.setCategory(category);
        event.setLocation(location);

        var user = userRepository.findById(userId).orElseThrow(
                () -> new NotExistException("User#" + userId + " does not exist"));

        event.setInitiator(user);
        LongEventDto eventDto = eventMapper.toLongEventDto(eventRepository.save(event));
        eventDto.setConfirmedRequests(0L);
        eventDto.setViews(0L);

        return eventDto;
    }

    @Override
    @Transactional
    public Event updateEvent(UpdateEventDto updateEventDto, Event event) {

        if (updateEventDto == null)
            return null;

        if (updateEventDto.getAnnotation() != null && !updateEventDto.getAnnotation().isBlank())
            event.setAnnotation(updateEventDto.getAnnotation());

        if (updateEventDto.getCategory() != null) {
            var category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(
                    () -> new NotExistException("This category does not exist"));
            event.setCategory(category);
        }
        if (updateEventDto.getDescription() != null && !updateEventDto.getDescription().isBlank())
            event.setDescription(updateEventDto.getDescription());

        if (updateEventDto.getLocation() != null)
            event.setLocation(locationMapper.toLocation(updateEventDto.getLocation()));

        if (updateEventDto.getPaid() != null)
            event.setPaid(updateEventDto.getPaid());

        if (updateEventDto.getParticipantLimit() != null)
            event.setParticipantLimit(updateEventDto.getParticipantLimit().intValue());

        if (updateEventDto.getRequestModeration() != null)
            event.setRequestModeration(updateEventDto.getRequestModeration());

        if (updateEventDto.getTitle() != null && !updateEventDto.getTitle().isBlank())
            event.setTitle(updateEventDto.getTitle());

        if (updateEventDto.getEventDate() != null) {
            var eventTime = updateEventDto.getEventDate();
            if (eventTime.isBefore(now()) || event.getPublishedOn() != null
                    && eventTime.isBefore(event.getPublishedOn().plusHours(1)))
                throw new WrongTimeException("Wrong time");

            event.setEventDate(updateEventDto.getEventDate());
        }
        return event;
    }

    @Override
    @Transactional
    public LongEventDto updateEventByAdmin(Long eventId,
                                           UpdateEventAdminDto updateEventAdminDto) {
        var event = eventRepository.findById(eventId).orElseThrow(
                () -> new NotExistException("Event#" + eventId + " does not exist"));

        event = updateEvent(updateEventAdminDto, event);

        if (event == null)
            return eventMapper.toLongEventDto(event);

        if (updateEventAdminDto.getStateAction() != null) {
            if (PUBLISH_EVENT.equals(updateEventAdminDto.getStateAction())) {
                if (event.getPublishedOn() != null)
                    throw new EventPublishedException("Event has been published");
                if (CANCELED.equals(event.getState()))
                    throw new EventCanceledException("Event has been canceled");
                event.setState(PUBLISHED);
                event.setPublishedOn(now());
            } else if (REJECT_EVENT.equals(updateEventAdminDto.getStateAction())) {
                if (event.getPublishedOn() != null)
                    throw new EventPublishedException("Event has been published");
                event.setState(CANCELED);
            }
        }

        LongEventDto eventDto = eventMapper.toLongEventDto(eventRepository.save(event));
        addConfirmedRequest(eventDto);
        eventDto.setViews(0L);

        return eventDto;
    }

    @Override
    @Transactional
    public LongEventDto updateEventByUser(Long userId,
                                          Long eventId,
                                          UpdateEventUserDto updateEventUserDto) {
        var event = eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotExistException("Event#" + eventId + " does not exist"));

        if (event.getPublishedOn() != null)
            throw new EventPublishedException("Event has been published");

        event = updateEvent(updateEventUserDto, event);

        if (event == null)
            return eventMapper.toLongEventDto(event);

        if (updateEventUserDto.getStateAction() != null) {
            if (SEND_TO_REVIEW.equals(updateEventUserDto.getStateAction()))
                event.setState(PENDING);
            else
                event.setState(CANCELED);
        }

        LongEventDto eventDto = eventMapper.toLongEventDto(eventRepository.save(event));
        addConfirmedRequest(eventDto);
        eventDto.setViews(getView(event));

        return eventDto;
    }

    @Override
    public List<ShortEventDto> getEvents(Long userId,
                                         Integer from,
                                         Integer size) {
        var events = eventRepository.findAllByInitiatorId(userId, of(from / size, size)).toList();
        return eventMapper.toShortEventDtos(events);
    }

    @Override
    public LongEventDto getEventByUser(Long userId,
                                       Long eventId) {
        LongEventDto eventDto = eventMapper.toLongEventDto(eventRepository.findByIdAndInitiatorId(eventId, userId).orElseThrow(
                () -> new NotExistException("Event#" + eventId + " does not exist")));

        return eventDto;
    }

    @Override
    public LongEventDto getEvent(Long id,
                                 HttpServletRequest request) {
        var event = eventRepository.findByIdAndPublishedOnIsNotNull(id).orElseThrow(
                () -> new NotExistException("Event#" + id + " does not exist"));
        long view = getView(event) + 1;
        sendStats(event, request);
        LongEventDto eventDto = eventMapper.toLongEventDto(event);
        addConfirmedRequest(eventDto);
        eventDto.setViews(view);

        return eventDto;
    }

    @Override
    public List<LongEventDto> getEventsWithParamsByAdmin(List<Long> users,
                                                         EventState states,
                                                         List<Long> categories,
                                                         String rangeStart,
                                                         String rangeEnd,
                                                         Integer from,
                                                         Integer size) {
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Event.class);
        var root = query.from(Event.class);
        var criteria = builder.conjunction();

        var start = rangeStart == null ? null : parse(rangeStart, formatter);
        var end = rangeEnd == null ? null : parse(rangeEnd, formatter);

        if (rangeStart != null)
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));

        if (rangeEnd != null)
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), end));

        if (categories != null && categories.size() > 0)
            criteria = builder.and(criteria, root.get("category").in(categories));

        if (users != null && users.size() > 0)
            criteria = builder.and(criteria, root.get("initiator").in(users));

        if (states != null)
            criteria = builder.and(criteria, root.get("state").in(states));

        query.select(root).where(criteria);

        List<Event> events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (events.size() == 0) return new ArrayList<>();

        Map<Long, Long> views = getViews(events);
        List<LongEventDto> eventDtos = eventMapper.toLongEventDtos(events);

        eventDtos = eventDtos.stream()
                .peek(dto -> dto.setConfirmedRequests(
                        requestRepository.countAllByEventAndStatus(dto.getId(), RequestStatus.CONFIRMED)))
                .peek(dto -> dto.setViews(views.getOrDefault(dto.getId(), 0L)))
                .collect(Collectors.toList());

        return eventDtos;
    }

    @Override
    public List<LongEventDto> getEventsWithParamsByUser(String text,
                                                        List<Long> categories,
                                                        Boolean paid,
                                                        String rangeStart,
                                                        String rangeEnd,
                                                        Boolean available,
                                                        SortValue sort,
                                                        Integer from,
                                                        Integer size,
                                                        HttpServletRequest request) {
        var builder = entityManager.getCriteriaBuilder();
        var query = builder.createQuery(Event.class);
        var root = query.from(Event.class);
        var criteria = builder.conjunction();

        var start = rangeStart == null ? null : parse(rangeStart, formatter);
        var end = rangeEnd == null ? null : parse(rangeEnd, formatter);

        if (text != null) {
            criteria = builder.and(criteria, builder.or(
                    builder.like(
                            builder.lower(root.get("annotation")), "%" + text.toLowerCase() + "%"),
                    builder.like(
                            builder.lower(root.get("description")), "%" + text.toLowerCase() + "%")));
        }

        if (categories != null && categories.size() > 0)
            criteria = builder.and(criteria, root.get("category").in(categories));

        if (paid != null) {
            Predicate predicate;
            if (paid) predicate = builder.isTrue(root.get("paid"));
            else predicate = builder.isFalse(root.get("paid"));
            criteria = builder.and(criteria, predicate);
        }

        if (rangeEnd != null) {
            if (rangeStart != null) {
                LocalDateTime startTime = LocalDateTime.parse(rangeStart, formatter);
                LocalDateTime finishTime = LocalDateTime.parse(rangeEnd, formatter);
                if (startTime.isAfter(finishTime)) {
                    throw new BadParamException("rangeEnd before than startTime");
                }
            }
            criteria = builder.and(criteria, builder.lessThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class),
                    end));
        }

        if (rangeStart != null)
            criteria = builder.and(criteria, builder.greaterThanOrEqualTo(root.get("eventDate").as(LocalDateTime.class), start));

        query.select(root).where(criteria).orderBy(builder.asc(root.get("eventDate")));

        var events = entityManager.createQuery(query)
                .setFirstResult(from)
                .setMaxResults(size)
                .getResultList();

        if (events.size() == 0) return new ArrayList<>();

        sendStats(events, request);
        return eventMapper.toLongEventDtos(events);
    }

    private List<ViewStatsDto> getStats(String start,
                                        String end,
                                        List<String> uris) {
        return statsClient.getStats(start, end, uris, false);
    }

    private long getView(Event event) {
        var start = event.getCreatedOn().format(formatter);
        var end = now().format(formatter);
        var uris = List.of("/events/" + event.getId());
        var stats = getStats(start, end, uris);

        if (stats.size() == 1)
            return stats.get(0).getHits();
        else
            return 0L;
    }

    private Map<Long, Long> getViews(List<Event> events) {
        events.sort(Comparator.comparing(Event::getCreatedOn));
        var startDate = events.get(0).getCreatedOn().format(formatter);
        var endDate = now().format(formatter);
        List<Long> ids = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<String> uris = new ArrayList<>();

        for (Long id : ids) {
            uris.add("/events/" + id);
        }
        var stats = statsClient.getStats(startDate, endDate, uris, false);

        Map<Long, Long> result = new HashMap<>();

        for (int i = 0; i < stats.size(); i++) {
            result.put(ids.get(i), stats.get(i).getHits());
        }

        return result;
    }

    private void addConfirmedRequest(LongEventDto event) {
        event.setConfirmedRequests(requestRepository.countAllByEventAndStatus(event.getId(), RequestStatus.CONFIRMED));
    }

    private void sendStats(Event event,
                           HttpServletRequest request) {
        var now = now();
        var requestDto = HitDto.builder()
                .ip(request.getRemoteAddr())
                .app("main")
                .uri("/events")
                .timestamp(now)
                .build();

        statsClient.addStats(requestDto);
        sendStatsForTheEvent(
                event.getId(),
                request.getRemoteAddr(),
                now
        );
    }

    private void sendStats(List<Event> events,
                           HttpServletRequest request) {
        var now = now();
        var requestDto = HitDto.builder()
                .ip(request.getRemoteAddr())
                .app("main")
                .uri("/events")
                .timestamp(now)
                .build();

        statsClient.addStats(requestDto);
        sendStatsForEveryEvent(
                events,
                request.getRemoteAddr(),
                now
        );
    }

    private void sendStatsForTheEvent(Long eventId,
                                      String remoteAddress,
                                      LocalDateTime now) {
        var requestDto = HitDto.builder()
                .ip(remoteAddress)
                .app("main")
                .uri("/events/" + eventId)
                .timestamp(now)
                .build();

        statsClient.addStats(requestDto);
    }

    private void sendStatsForEveryEvent(List<Event> events,
                                        String remoteAddress,
                                        LocalDateTime now) {
        for (var event : events) {
            var requestDto = HitDto.builder()
                    .ip(remoteAddress)
                    .app("main")
                    .uri("/events/" + event.getId())
                    .timestamp(now)
                    .build();

            statsClient.addStats(requestDto);
        }
    }
}
