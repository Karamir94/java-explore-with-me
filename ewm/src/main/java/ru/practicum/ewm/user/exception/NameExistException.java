package ru.practicum.ewm.user.exception;

public class NameExistException extends RuntimeException {

    public NameExistException(String message) {
        super(message);
    }
}
