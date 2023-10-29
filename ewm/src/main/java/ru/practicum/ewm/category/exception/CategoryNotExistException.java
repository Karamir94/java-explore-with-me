package ru.practicum.ewm.category.exception;

public class CategoryNotExistException extends RuntimeException {

    public CategoryNotExistException(String message) {
        super(message);
    }
}
