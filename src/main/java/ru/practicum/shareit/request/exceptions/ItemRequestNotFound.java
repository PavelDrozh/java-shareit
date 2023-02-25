package ru.practicum.shareit.request.exceptions;

public class ItemRequestNotFound extends RuntimeException {
    public ItemRequestNotFound(String message) {
        super(message);
    }
}
