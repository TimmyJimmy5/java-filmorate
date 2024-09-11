package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    List<Film> getTopFilms(int size);

    Film findFilmById(Long filmId);

    boolean putLike(Long id, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    Collection<Film> findAll();

    Film create(Film film);

    Film update(Film newFilm);
}
