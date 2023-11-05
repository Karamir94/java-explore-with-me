package ru.practicum.ewm.user.error;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.error.entity.Error;
import ru.practicum.ewm.user.exception.NameExistException;
import ru.practicum.ewm.user.exception.UserNotExistException;
import ru.practicum.ewm.user.exception.WrongUserException;
import ru.practicum.ewm.utils.Patterns;

import static java.time.LocalDateTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class UserErrorHandler {

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleNameExistException(final NameExistException exception) {
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("This username is already exist")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(CONFLICT)
    public Error handleWrongUserException(final WrongUserException exception) {
        return Error.builder()
                .status(CONFLICT.getReasonPhrase().toUpperCase())
                .reason("Wrong user")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public Error handleUserNotExistException(final UserNotExistException exception) {
        return Error.builder()
                .status(NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason("This user does not exist")
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }
}
