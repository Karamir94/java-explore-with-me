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
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    private static final DateTimeFormatter formatter = ofPattern(Patterns.DATE_PATTERN);

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public Error handleMethodArgumentTypeMismatchException(final MethodArgumentTypeMismatchException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return Error.builder()
                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public Error handleMethodArgumentNotValidExceptionException(final MethodArgumentNotValidException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return Error.builder()
                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public Error handleMissingRequestValueExceptionException(final MissingRequestValueException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return Error.builder()
                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public Error handleBadParamExceptionException(final BadParamException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return Error.builder()
                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(BAD_REQUEST)
    public Error handleWrongTimeExceptionException(final WrongTimeException exception) {
        log.debug("Получен статус 400 BAD_REQUEST {}", exception.getMessage(), exception);
        return Error.builder()
                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
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

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleAlreadyExistExceptionException(final AlreadyExistException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Entity already exist")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleSQLIntegrityConstraintViolationExceptionException(final SQLIntegrityConstraintViolationException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Entity already exist")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleConstraintViolationExceptionException(final ConstraintViolationException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Entity already exist")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleDataIntegrityViolationExceptionException(final DataIntegrityViolationException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Entity already exist")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleRequestConfirmedException(final RequestConfirmedException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Conditions are wrong")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleRequestParticipantLimitExceptionException(final RequestParticipantLimitException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Conditions are wrong")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleCategoryNotEmptyExceptionException(final CategoryNotEmptyException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Conditions are wrong")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleEventNotPublishedExceptionException(final EventNotPublishedException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Conditions are wrong")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleEventPublishedExceptionException(final EventPublishedException exception) {
        log.debug("Получен статус 409 CONFLICT {}", exception.getMessage(), exception);
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Conditions are wrong")
                .message(exception.getMessage())
                .timestamp(now().format(formatter))
                .build();
    }

    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleEventCanceledExceptionException(final EventCanceledException exception) {
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
