package ru.practicum.ewm.user.exception;

public class WrongUserException extends RuntimeException {

    public WrongUserException(String message) {
        super(message);
    }
}