package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@EqualsAndHashCode
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private Rating rating;
    private Set<Long> likes;
    private Set<Genre> genres;
    private Set<Director> directors;

    public Film() {
        genres = new HashSet<>();
        likes = new HashSet<>();
        directors = new HashSet<>();
    }
}
