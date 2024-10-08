package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.repository.FilmRepository;
import ru.yandex.practicum.filmorate.dal.repository.RatingRepository;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.FilmRequest;
import ru.yandex.practicum.filmorate.exception.BadInputExceptionParametered;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Rating;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class InMemoryFilmService implements FilmService {
    private final FilmRepository filmRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;

    @Override
    public List<FilmDto> getTopFilms(int size) {
        return filmRepository.getTopFilms(size).stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto findFilmById(Long filmId) {
        return filmRepository.findById(filmId)
                .map(FilmMapper::mapToFilmDto)
                .orElseThrow(() -> new NotFoundException("Фильм с ID " + filmId + " не найден"));
    }

    @Override
    public boolean putLike(Long id, Long userId) {
        checkFilmId(filmRepository, id);
        checkUserId(userRepository, userId);
        if (filmRepository.findLike(id, userId)) {
            return false;
        }
        return filmRepository.putLike(id, userId);
    }

    @Override
    public boolean deleteLike(Long filmId, Long userId) {
        checkFilmId(filmRepository, filmId);
        checkUserId(userRepository, userId);
        return filmRepository.deleteLike(filmId, userId);
    }

    @Override
    public Collection<FilmDto> findAll() {
        return filmRepository.getAll().stream()
                .map(FilmMapper::mapToFilmDto)
                .collect(Collectors.toList());
    }

    @Override
    public FilmDto create(FilmRequest filmRequest) {
        Film film = FilmMapper.mapToFilm(filmRequest);
        addRating(film, filmRequest);
        film = filmRepository.save(film);
        addGenres(film, filmRequest);
        addDirector(film, filmRequest);
        return FilmMapper.mapToFilmDto(film);
    }

    @Override
    public boolean delete(Long id) {
        return filmRepository.delete(id);
    }

    @Override
    public FilmDto update(FilmRequest filmRequest) {
        if (filmRequest.getId() == null) {
            throw new BadInputExceptionParametered("ID", "ID фильма не может быть пустым.");
        }

        Film updatedFilm = filmRepository.findById(filmRequest.getId())
                .map(film -> FilmMapper.updateFilmFields(film, filmRequest))
                .orElseThrow(() -> new NotFoundException(filmRequest.getId() + " фильм с таким ИД не найден"));
        addRating(updatedFilm, filmRequest);
        addGenres(updatedFilm, filmRequest);
        addDirector(updatedFilm, filmRequest);
        updatedFilm = filmRepository.update(updatedFilm);
        return FilmMapper.mapToFilmDto(updatedFilm);
    }

    public void checkFilmId(FilmRepository filmRepository, Long... ids) {
        for (Long id : ids) {
            if (filmRepository.findById(id).isEmpty()) {
                throw new NotFoundException("Фильма с ID " + id + " не существует");
            }
        }
    }

    public void checkUserId(UserRepository userRepository, Long... ids) {
        for (Long id : ids) {
            if (userRepository.findById(id).isEmpty()) {
                throw new NotFoundException("Юзера с ID " + id + " не существует");
            }
        }
    }

    private void addRating(Film film, FilmRequest filmRequest) {
        Long ratingId = filmRequest.getRating().getId();
        Rating rating = ratingRepository.get(ratingId)
                .orElseThrow(() -> new BadInputExceptionParametered("ID", "Рейтинг с ID " + ratingId + " не найден"));
        film.setRating(rating);
    }

    private void addGenres(Film film, FilmRequest filmRequest) {
        filmRepository.deleteAllGenresForFilm(film.getId());
        if (filmRequest.getGenres() == null) {
            film.setGenres(new HashSet<>());
            return;
        }
    }

    private void addDirector(Film film, FilmRequest filmRequest) {
        filmRepository.deleteAllDirectorsForFilm(film.getId());
        if (filmRequest.getDirectors() == null) {
            film.setDirectors(new HashSet<>());
            return;
        }
    }
}
