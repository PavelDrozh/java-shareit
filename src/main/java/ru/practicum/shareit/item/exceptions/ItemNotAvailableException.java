package ru.practicum.shareit.item.exceptions;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
