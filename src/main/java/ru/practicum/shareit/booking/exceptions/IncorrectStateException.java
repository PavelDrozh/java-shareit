package ru.practicum.shareit.booking.exceptions;

public class IncorrectStateException extends RuntimeException {
    public IncorrectStateException(String message) {
        super(message);
    }
}
