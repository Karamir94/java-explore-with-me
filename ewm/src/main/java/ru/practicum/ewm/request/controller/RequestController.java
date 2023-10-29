package ru.practicum.ewm.request.controller;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
public class RequestController {

    private final RequestService requestService;

    @PostMapping
    @ResponseStatus(CREATED)
    public RequestDto saveRequest(@RequestParam Long eventId,
                                  @PathVariable Long userId) {
        return requestService.saveRequest(userId, eventId);
    }

    @GetMapping
    public List<RequestDto> getCurrentUserRequests(@PathVariable Long userId) {
        return requestService.getCurrentUserRequests(userId);
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long requestId,
                                    @PathVariable Long userId) {
        return requestService.cancelRequest(userId, requestId);
    }
}
