package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;

public interface UserStorage {
    public Collection<User> findAll();

    public User create(User user);

    public User update(User newUser);

    public Map<Long, User> getUsers();
}
