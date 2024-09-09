package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class BadInputException extends IllegalArgumentException {
    private final String parameter;
    private final String reason;

    public BadInputException(String parameter, String reason) {
        this.parameter = parameter;
        this.reason = reason;
    }
}
