package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@EqualsAndHashCode
public class Film {
    private Long id;
    private Set<Long> likes;

    @NotBlank
    private String name;

    @Length(max = 200, message = "Описание длиннее 200 знаков.")
    private String description;

    private LocalDate releaseDate;
    private Integer duration;
}
