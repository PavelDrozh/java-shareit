package ru.practicum.shareit.user.exceptions;

public class ExistingEmailException extends RuntimeException {

    public ExistingEmailException(String message) {
        super(message);
    }

}
