package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {
    private Long id;
    private Set<Long> friends;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public User() {
        friends = new HashSet<>();
    }
}
