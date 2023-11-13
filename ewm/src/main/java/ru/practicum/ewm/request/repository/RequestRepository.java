package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.entity.Request;
import ru.practicum.ewm.request.entity.RequestEvent;
import ru.practicum.ewm.request.enums.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    @Query("SELECT request FROM Request AS request " +
            "JOIN Event AS event ON request.event = event.id " +
            "WHERE request.event = :eventId AND event.initiator.id = :userId")
    List<Request> findAllByEventWithInitiator(@Param("userId") Long userId,
                                              @Param("eventId") Long eventId);

    Optional<Request> findByRequesterAndId(Long userId,
                                           Long requestId);

    Boolean existsByRequesterAndEvent(Long userId,
                                      Long eventId);

    List<Request> findAllByRequester(Long userId);

    List<Request> findAllByEvent(Long eventId);

    Long countAllByEventAndStatus(Long eventId, RequestStatus status);

    @Query("select new ru.practicum.ewm.request.entity.RequestEvent(r.event, count(r.id)) from Request as r " +
            "where r.event in :ids and r.status = 'CONFIRMED' " +
            "group by r.event " +
            "order by r.event ")
    List<RequestEvent> getConfirmedRequests(@Param("ids") List<Long> ids);
}
