package ru.practicum.ewm.user.exception;

public class UserNotExistException extends RuntimeException {

    public UserNotExistException(String message) {
        super(message);
    }
}
