package ru.practicum.ewm.category.exception;

public class CategoryNotEmptyException extends RuntimeException {

    public CategoryNotEmptyException(String message) {
        super(message);
    }
}
