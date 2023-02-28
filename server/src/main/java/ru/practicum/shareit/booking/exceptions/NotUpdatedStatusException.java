package ru.practicum.shareit.booking.exceptions;

public class NotUpdatedStatusException extends RuntimeException {
    public NotUpdatedStatusException(String message) {
        super(message);
    }
}
