package ru.practicum.ewm.error.controller;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.ewm.error.entity.Error;
import ru.practicum.ewm.utils.Patterns;

import javax.validation.ConstraintViolationException;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class ErrorHandler {

    @ResponseBody
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(BAD_REQUEST)
    public Error handleMethodArgumentTypeMismatchException(final RuntimeException exception) {
        return Error.builder()
                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
                .reason("Incorrectly made request")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }

//    @ResponseBody
//    @ExceptionHandler
//    @ResponseStatus(BAD_REQUEST)
//    public Error handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception) {
//        return Error.builder()
//                .status(BAD_REQUEST.getReasonPhrase().toUpperCase())
//                .reason("Incorrectly made request")
//                .message(exception.getMessage())
//                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
//                .build();
//    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public Error handleEmptyResultDataAccessException(final EmptyResultDataAccessException exception) {
        return Error.builder()
                .status(NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason("Empty result data")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }
}
