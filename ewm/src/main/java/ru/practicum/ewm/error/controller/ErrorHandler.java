package ru.practicum.ewm.error.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.category.exception.CategoryNotEmptyException;
import ru.practicum.ewm.error.entity.Error;
import ru.practicum.ewm.error.exception.AlreadyExistException;
import ru.practicum.ewm.error.exception.BadParamException;
import ru.practicum.ewm.error.exception.NotExistException;
import ru.practicum.ewm.error.exception.WrongTimeException;
import ru.practicum.ewm.event.exception.EventCanceledException;
import ru.practicum.ewm.event.exception.EventNotPublishedException;
import ru.practicum.ewm.event.exception.EventPublishedException;
import ru.practicum.ewm.request.exception.RequestConfirmedException;
import ru.practicum.ewm.request.exception.RequestParticipantLimitException;
import ru.practicum.ewm.user.exception.WrongUserException;
import ru.practicum.ewm.utils.Patterns;

import java.sql.SQLIntegrityConstraintViolationException;
import java.time.format.DateTimeFormatter;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Patterns.DATE_PATTERN);

    @ExceptionHandler({MethodArgumentTypeMismatchException.class,
            MethodArgumentNotValidException.class,
            BadParamException.class, WrongTimeException.class, MissingRequestValueException.class})
    @ResponseStatus(BAD_REQUEST)
    public Error handleMethodArgumentTypeMismatchException(final RuntimeException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return Error.builder()
                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public Error handleEmptyResultDataAccessException(final EmptyResultDataAccessException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return Error.builder()
                .status(NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason("Empty result data")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public Error handleCategoryNotExistException(final NotExistException exception) {
        log.debug("Получен статус 404 Not found {}", exception.getMessage(), exception);
        return Error.builder()
                .status(NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason(("This entity does not exist"))
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleWrongUserException(final WrongUserException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Wrong user")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler({AlreadyExistException.class, SQLIntegrityConstraintViolationException.class,
            ConstraintViolationException.class, DataIntegrityViolationException.class})
    @ResponseStatus(CONFLICT)
    public Error handleRequestExistException(final RuntimeException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Entity already exist")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler({RequestConfirmedException.class,
            RequestParticipantLimitException.class,
            CategoryNotEmptyException.class,
            EventNotPublishedException.class,
            EventPublishedException.class,
            EventCanceledException.class})
    @ResponseStatus(CONFLICT)
    public Error handleRequestConfirmedException(final RuntimeException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Conditions are wrong")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public Error handleEmptyResultDataAccessException(final Throwable exception) {
        log.info("Получен статус 500 INTERNAL_SERVER_ERROR {}", exception.getMessage(), exception);
        return Error.builder()
                .status(INTERNAL_SERVER_ERROR.getReasonPhrase().toUpperCase())
                .reason("Servers error")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }
}
