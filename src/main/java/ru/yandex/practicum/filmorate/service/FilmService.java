package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    List<FilmDto> getTopFilms(int size);

    FilmDto findFilmById(Long filmId);

    boolean putLike(Long id, Long userId);

    boolean deleteLike(Long filmId, Long userId);

    Collection<FilmDto> findAll();

    FilmDto create(FilmRequest filmRequest);

    FilmDto update(FilmRequest filmRequest);

    boolean delete(Long id);
}
