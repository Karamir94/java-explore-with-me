package ru.practicum.ewm.event.exception;

public class EventCanceledException extends RuntimeException {

    public EventCanceledException(String message) {
        super(message);
    }
}
