package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import java.time.LocalDate;

/**
 * Film.
 */
@Getter
@Setter
@EqualsAndHashCode
public class Film {
    private Long id;

    @NotBlank
    private String name;

    @Length(max = 200, message = "Описание длиннее 200 знаков.")
    private String description;

    private LocalDate releaseDate;
    private Integer duration;
}
