package ru.practicum.shareit.exception;

public class UnavailableActionError extends RuntimeException {

    public UnavailableActionError(String message) {
        super(message);
    }
}
