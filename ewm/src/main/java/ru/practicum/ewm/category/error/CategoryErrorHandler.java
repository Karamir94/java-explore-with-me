package ru.practicum.ewm.category.error;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.category.exception.CategoryNotEmptyException;
import ru.practicum.ewm.category.exception.CategoryNotExistException;
import ru.practicum.ewm.error.entity.Error;
import ru.practicum.ewm.utils.Patterns;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class CategoryErrorHandler {

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleCategoryNotEmptyException(final CategoryNotEmptyException exception) {
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Conditions are wrong")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public Error handleCategoryNotExistException(final CategoryNotExistException exception) {
        return Error.builder()
                .status(NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason(("This category does not exist"))
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }
}
