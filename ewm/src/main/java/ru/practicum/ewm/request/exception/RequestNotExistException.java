package ru.practicum.ewm.request.exception;

public class RequestNotExistException extends RuntimeException {

    public RequestNotExistException(String message) {
        super(message);
    }
}
