package ru.yandex.practicum.filmorate.storage;

import lombok.Data;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Data
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;

    public InMemoryUserStorage() {
        this.users = new HashMap<>();
    }

    @Override
    public Collection<User> findAll() {
        return users.values();
    }

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User newUser) {
        return users.put(newUser.getId(), newUser);
    }

    @Override
    public Set<User> getFriends(Long userId) {
        User user = users.get(userId);
        return users.entrySet().stream()
                .filter(entry -> user.getFriends().contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<User> getCommonFriends(Long userId, Long otherId) {
        Set<Long> commonFriends = users.get(userId).getFriends().stream()
                .filter(users.get(otherId).getFriends()::contains)
                .collect(Collectors.toSet());
        if (commonFriends.isEmpty()) {
            throw new NotFoundException("Общих друзей с пользователем " + otherId + " нет.");
        }
        return users.entrySet().stream()
                .filter(entry -> commonFriends.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean addFriend(Long userId, Long friendId) {
        if (users.get(userId).getFriends().add(friendId) && users.get(friendId).getFriends().add(userId)) {
            return true;
        } else {
            throw new DuplicatedDataException("Пользователь уже состоит в списке друзей.");
        }
    }

    @Override
    public boolean removeFriend(Long userId, Long friendId) {
        users.get(userId).getFriends().remove(friendId);
        return users.get(friendId).getFriends().remove(userId);
    }

    @Override
    public boolean isExist(Long userId) {
        return users.containsKey(userId);
    }
}
