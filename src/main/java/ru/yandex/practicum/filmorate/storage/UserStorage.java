package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public interface UserStorage {
    Collection<User> findAll();

    User create(User user);

    User update(User newUser);

    Map<Long, User> getUsers();

    Set<User> getCommonFriends(Long id, Long otherId);

    boolean addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    Set<User> getFriends(Long userId);
}
