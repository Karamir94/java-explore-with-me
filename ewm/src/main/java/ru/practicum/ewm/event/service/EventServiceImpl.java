package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.error.exception.NotExistException;
import ru.practicum.ewm.error.exception.WrongTimeException;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.entity.Event;
import ru.practicum.ewm.event.entity.EventDtoViewsComparator;
import ru.practicum.ewm.event.entity.Location;
import ru.practicum.ewm.event.entity.QEvent;
import ru.practicum.ewm.event.enums.EventSort;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.event.exception.EventCanceledException;
import ru.practicum.ewm.event.exception.EventPublishedException;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.mapper.LocationMapper;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.request.entity.RequestEvent;
import ru.practicum.ewm.request.enums.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.dto.ShortUserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.utils.Patterns;
import ru.practicum.stats.client.StatsClient;
import ru.practicum.stats.dto.HitDto;
import ru.practicum.stats.dto.ViewStatsDto;

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

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    private final LocationMapper locationMapper;
    private final CategoryMapper categoryMapper;
    private final UserMapper userMapper;
    ApplicationContext context = new AnnotationConfigApplicationContext("ru.practicum.stats.client");
    private final StatsClient statsClient = context.getBean(StatsClient.class);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN);

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
        LongEventDto eventDto = eventMapper.toLongEventDto(event);
        addConfirmedRequest(eventDto);
        eventDto.setViews(view);
        sendStats(event, request);

        return eventDto;
    }

    @Override
    public List<LongEventDto> getEventsWithParamsByAdmin(List<Long> users,
                                                         List<EventState> states,
                                                         List<Long> categories,
                                                         String rangeStart,
                                                         String rangeEnd,
                                                         Integer from,
                                                         Integer size) {

        var start = rangeStart == null ? null : parse(rangeStart, formatter);
        var end = rangeEnd == null ? null : parse(rangeEnd, formatter);
        if (start != null && end != null && start.isAfter(end)) {
            throw new WrongTimeException("Start must be before end");
        }

        BooleanExpression filter = buildConditionsForEventsByAdmin(users, states, categories, start, end);
        Pageable pageable = PageRequest.of(from / size, size);
        Page<Event> events = eventRepository.findAll(filter, pageable);

        if (events.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Long, Long> views = getViews(events.toList());
        Map<Long, Long> requests = getConfirmedRequests(events.toList());
        List<LongEventDto> result = new ArrayList<>();

        for (Event event : events) {
            LongEventDto response = eventMapper.toLongEventDto(event);
            response.setConfirmedRequests(
                    requests.getOrDefault(event.getId(), 0L));
            response.setViews(
                    views.getOrDefault(event.getId(), 0L));
            result.add(response);
        }
        return result;
    }

    @Override
    public List<ShortEventDto> getEventsWithParamsByUser(String text,
                                                         List<Long> categories,
                                                         Boolean paid,
                                                         String rangeStart,
                                                         String rangeEnd,
                                                         Boolean available,
                                                         String sort,
                                                         Integer from,
                                                         Integer size,
                                                         HttpServletRequest request) {

        LocalDateTime start = rangeStart == null ? null : parse(rangeStart, formatter);
        LocalDateTime end = rangeEnd == null ? null : parse(rangeEnd, formatter);

        if (start != null && end != null && start.isAfter(end)) {
            throw new WrongTimeException("Start must be before end");
        }

        BooleanExpression filter = buildConditionsForEventsPublic(text, categories, paid, start, end, available);
        Sort doSort;
        if (StringUtils.isNotBlank(sort) && sort.equals(EventSort.EVENT_DATE.toString())) {
            doSort = Sort.by(EventSort.EVENT_DATE.getTitle());
        } else {
            doSort = Sort.by(EventSort.ID.getTitle());
        }
        Pageable pageable = PageRequest.of(from / size, size, doSort);
        Page<Event> events = eventRepository.findAll(filter, pageable);
        List<ShortEventDto> result = buildEventDtoResponseShort(events.toList());
        if (StringUtils.isNotBlank(sort) && sort.equals(EventSort.VIEWS.toString())) {
            Comparator<ShortEventDto> comparator = new EventDtoViewsComparator();
            result.sort(comparator);
        }

        sendStats(events.toList(), request);
        return result;
    }

    private BooleanExpression buildConditionsForEventsByAdmin(List<Long> users,
                                                              List<EventState> states,
                                                              List<Long> categories,
                                                              LocalDateTime start,
                                                              LocalDateTime end) {

        List<BooleanExpression> conditions = new ArrayList<>();
        if (users != null && !users.isEmpty()) {
            BooleanExpression condition = QEvent.event.initiator.id.in(users);
            condition.and(condition);
        }
        if (states != null && !states.isEmpty()) {
            BooleanExpression condition = QEvent.event.state.in(states);
            conditions.add(condition);
        }
        if (categories != null && !categories.isEmpty()) {
            BooleanExpression condition = QEvent.event.category.id.in(categories);
            conditions.add(condition);
        }
        if (start != null) {
            BooleanExpression condition = QEvent.event.eventDate.after(start);
            conditions.add(condition);
        }
        if (end != null) {
            BooleanExpression condition = QEvent.event.eventDate.before(end);
            conditions.add(condition);
        }
        if (conditions.isEmpty()) {
            return Expressions.TRUE.isTrue();
        } else {
            return conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
        }
    }

    public List<ShortEventDto> buildEventDtoResponseShort(List<Event> events) {
        List<ShortEventDto> result = new ArrayList<>();
        Map<Long, Long> views = getViews(events);
        Map<Long, Long> requests = getConfirmedRequests(events);
        for (Event event : events) {
            CategoryDto categoryDto = categoryMapper.toCategoryDto(event.getCategory());
            ShortUserDto userDto = userMapper.toShortUserDto(event.getInitiator());
            ShortEventDto response = eventMapper.toShortEvent(event);
            response.setConfirmedRequests(
                    requests.getOrDefault(event.getId(), 0L));
            response.setViews(
                    views.getOrDefault(event.getId(), 0L));
            response.setCategory(categoryDto);
            response.setInitiator(userDto);
            result.add(response);
        }
        return result;
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        List<Long> ids = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        List<RequestEvent> req = requestRepository.getConfirmedRequests(ids);
        if (req.isEmpty()) {
            return new HashMap<>();
        }
        Map<Long, Long> result = new HashMap<>();
        for (RequestEvent ev : req) {
            result.put(ev.getEventId(), ev.getCount().longValue());
        }
        return result;
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
        Map<Long, Long> result = new HashMap<>();

        for (Event event : events) {
            result.put(event.getId(), getView(event));
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

    private BooleanExpression buildConditionsForEventsPublic(String text,
                                                             List<Long> categories,
                                                             Boolean paid,
                                                             LocalDateTime start,
                                                             LocalDateTime end,
                                                             Boolean available) {

        List<BooleanExpression> conditions = new ArrayList<>();
        if (StringUtils.isNotBlank(text)) {
            BooleanExpression condition = QEvent.event.annotation.containsIgnoreCase(text)
                    .or(QEvent.event.description.containsIgnoreCase(text));
            conditions.add(condition);
        }
        if (categories != null && !categories.isEmpty()) {
            BooleanExpression condition = QEvent.event.category.id.in(categories);
            conditions.add(condition);
        }
        if (paid != null) {
            BooleanExpression condition = QEvent.event.paid.eq(paid);
            conditions.add(condition);
        }
        if (start != null) {
            BooleanExpression condition = QEvent.event.eventDate.after(start);
            conditions.add(condition);
        }
        if (end != null) {
            BooleanExpression condition = QEvent.event.eventDate.before(end);
            conditions.add(condition);
        }
        if (available != null) {
            BooleanExpression condition = QEvent.event.participantLimit.goe(0);
            conditions.add(condition);
        }
        BooleanExpression condition = QEvent.event.state.eq(EventState.PUBLISHED);
        conditions.add(condition);

        return conditions.stream()
                .reduce(BooleanExpression::and)
                .get();
    }
}
