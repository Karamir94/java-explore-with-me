package ru.practicum.ewm.compilation.error;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.ewm.compilation.exception.CompilationNotExistException;
import ru.practicum.ewm.error.entity.Error;
import ru.practicum.ewm.utils.Patterns;

import static java.time.LocalTime.now;
import static java.time.format.DateTimeFormatter.ofPattern;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class CompilationErrorHandler {

    @ResponseBody
    @ExceptionHandler
    @ResponseStatus(NOT_FOUND)
    public Error handleCompilationNotExistException(final CompilationNotExistException exception) {
        return Error.builder()
                .status(NOT_FOUND.getReasonPhrase().toUpperCase())
                .reason(("This compilation does not exist"))
                .message(exception.getMessage())
                .timestamp(now().format(ofPattern(Patterns.DATE_PATTERN)))
                .build();
    }
}
