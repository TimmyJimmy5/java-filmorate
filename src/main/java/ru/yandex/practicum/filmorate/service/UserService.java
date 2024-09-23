package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Set;

public interface UserService {
    boolean addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    Set<User> getCommonFriends(Long userId, Long otherId);

    Set<User> getFriends(Long userId);

    Collection<User> findAll();

    User create(User user);

    User update(User newUser);
}
