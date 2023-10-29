package ru.practicum.ewm.request.entity;

import lombok.*;
import org.hibernate.Hibernate;
import ru.practicum.ewm.request.enums.RequestStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import static javax.persistence.EnumType.STRING;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "requests", schema = "public")
public class Request {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Enumerated(STRING)
    @Column(name = "status", length = 140)
    private RequestStatus status;

    @Column(name = "event")
    private Long event;

    @Column(name = "requester")
    private Long requester;

    @Column(name = "created")
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        var request = (Request) o;
        return getId() != null && Objects.equals(getId(), request.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
