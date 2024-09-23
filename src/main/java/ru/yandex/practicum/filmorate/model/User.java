package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = {"email"})
public class User {
    private Long id;
    private Set<Long> friends;

    @NotBlank
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    public User() {
        friends = new HashSet<>();
    }
}
