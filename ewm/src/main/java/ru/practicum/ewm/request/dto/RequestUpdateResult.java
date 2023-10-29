package ru.practicum.ewm.request.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateResult {

    private List<RequestDto> confirmedRequests;
    private List<RequestDto> rejectedRequests;
}
