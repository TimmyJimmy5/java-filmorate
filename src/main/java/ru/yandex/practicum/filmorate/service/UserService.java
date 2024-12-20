package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.dto.user.UserRequest;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Service
public interface UserService {
    boolean addFriend(Long userId, Long friendId);

    boolean removeFriend(Long userId, Long friendId);

    Set<UserDto> getCommonFriends(Long userId, Long otherId);

    List<UserDto> getFriends(Long userId);

    Collection<UserDto> findAll();

    UserDto create(UserRequest userRequest);

    UserDto update(UserRequest userRequest);

    UserDto get(Long id);
}
