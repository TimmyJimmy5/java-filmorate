package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.dto.rating.RatingDto;

import java.util.List;

public interface RatingService {
    RatingDto get(Long id);

    List<RatingDto> getAll();
}
