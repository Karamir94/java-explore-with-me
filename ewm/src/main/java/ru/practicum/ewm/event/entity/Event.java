package ru.practicum.ewm.event.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.Hibernate;
import ru.practicum.ewm.category.entity.Category;
import ru.practicum.ewm.event.enums.EventState;
import ru.practicum.ewm.user.entity.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static java.util.Objects.requireNonNullElse;
import static java.util.Objects.requireNonNullElseGet;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;
import static ru.practicum.ewm.event.enums.EventState.PENDING;

@Entity
@Getter
@Setter
@Table(name = "events", schema = "public")
@NoArgsConstructor(access = PROTECTED)
public class Event {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "annotation", length = 2000)
    private String annotation;

    @Column(name = "created_on")
    private LocalDateTime createdOn;

    @Column(name = "description", length = 7000)
    private String description;

    @OneToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    @Column(name = "event_date")
    private LocalDateTime eventDate;

    @OneToOne
    @JoinColumn(name = "initiator_id", referencedColumnName = "id")
    private User initiator;

    @OneToOne(cascade = ALL)
    @JoinColumn(name = "location_id", referencedColumnName = "id")
    private Location location;

    @Column(name = "paid")
    private Boolean paid;

    @Column(name = "participant_limit")
    private int participantLimit;

    @Column(name = "published_on")
    private LocalDateTime publishedOn;

    @Column(name = "request_moderation")
    private Boolean requestModeration;

    @Enumerated(STRING)
    @Column(name = "state", length = 30)
    private EventState state;

    @Column(name = "title", length = 120)
    private String title;

    public Event(Long id,
                 String annotation,
                 Category category,
                 LocalDateTime createdOn,
                 String description,
                 LocalDateTime eventDate,
                 User initiator,
                 Location location,
                 Boolean paid,
                 int participantLimit,
                 LocalDateTime publishedOn,
                 Boolean requestModeration,
                 EventState eventState,
                 String title) {
        this.requestModeration = requireNonNullElse(requestModeration, true);
        this.createdOn = requireNonNullElseGet(createdOn, LocalDateTime::now);
        this.state = requireNonNullElse(eventState, PENDING);
        this.participantLimit = participantLimit;
        this.description = description;
        this.annotation = annotation;
        this.publishedOn = publishedOn;
        this.eventDate = eventDate;
        this.initiator = initiator;
        this.category = category;
        this.location = location;
        this.title = title;
        this.paid = paid;
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var event = (Event) o;
        return getId() != null && Objects.equals(getId(), event.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
