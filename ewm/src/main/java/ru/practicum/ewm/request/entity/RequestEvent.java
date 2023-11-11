package ru.practicum.ewm.request.entity;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestEvent {

    private Long eventId;

    private Long count;
}