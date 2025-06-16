package ru.practicum.shareit.exception;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException ex) {
        log.error("Не найден параметр: {}", ex.getMessage());
        return Map.of("notFound", ex.getMessage());
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handlerValidation(final ValidationException ex) {
        log.error("Параметр не прошел проверку: {}", ex.getMessage());
        return Map.of("error validation", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleAllExceptions(final Exception ex) {
        log.error("Внутренняя ошибка сервера: {}", ex.getMessage(), ex);
        return Map.of("Произошла непредвиденная ошибка", ex.getMessage());
    }
}
