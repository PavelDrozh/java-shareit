package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exceptions.IllegalUserException;
import ru.practicum.shareit.item.exceptions.ItemNotFoundException;
import ru.practicum.shareit.user.exceptions.ExistingEmailException;
import ru.practicum.shareit.user.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> argumentValidationExceptionHandler(MethodArgumentNotValidException exception) {
        log.error("Аргумент метода не прошел валидацию", exception);
        return exception
                .getFieldErrors()
                .stream()
                .map(error -> new ErrorResponse(error.getDefaultMessage()))
                .collect(Collectors.toList());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse headerExceptionHandler(MissingRequestHeaderException e) {
        log.error("Отсутствует заголовок запроса", e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserNotFoundException(final UserNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleExistingEmailException(final ExistingEmailException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalUserException(final IllegalUserException e) {
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.info(e.getMessage(), e);
        return new ErrorResponse("Произошла непредвиденная ошибка.");
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFoundException(final ItemNotFoundException e) {
        return new ErrorResponse(e.getMessage());
    }
}
