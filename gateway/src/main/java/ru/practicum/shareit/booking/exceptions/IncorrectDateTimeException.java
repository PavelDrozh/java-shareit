package ru.practicum.shareit.booking.exceptions;

public class IncorrectDateTimeException extends RuntimeException {
    public IncorrectDateTimeException(String message) {
        super(message);
    }
}
