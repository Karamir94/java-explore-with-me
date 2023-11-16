package ru.practicum.ewm.event.exception;


public class EventPublishedException extends RuntimeException {

    public EventPublishedException(String message) {
        super(message);
    }
}
