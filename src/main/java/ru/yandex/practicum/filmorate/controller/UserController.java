package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
            userDataValidation(user);
            userLoginToEmptyName(user);
            user.setId(getNextId());
            users.put(user.getId(), user);
            return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        if (users.containsKey(newUser.getId())) {
            if (newUser.getId() == null) {
                throw new ConditionsNotMetException("Id должен быть указан.");
            } else if (users.containsValue(newUser)) {
                throw new DuplicatedDataException("Этот Email уже используется.");
            }
            if (newUser.getEmail() == null) {
                newUser.setEmail(users.get(newUser.getId()).getEmail());
            }
            if (newUser.getLogin() == null) {
                newUser.setLogin(users.get(newUser.getId()).getLogin());
            }
            if (newUser.getLogin().contains(" ")) {
                throw new ConditionsNotMetException("Логин не может содержать пробелы.");
            }
            if (newUser.getBirthday() != null) {
                userBirthdayValidation(newUser);
            }
            userLoginToEmptyName(newUser);
            users.put(newUser.getId(), newUser);
            return newUser;
        }
        throw new NotFoundException("Пользователь не найден.");
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    private void userLoginToEmptyName(User user) {
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
    }

    private void userDataValidation(User user) {
        userEmailValidation(user);
        userLoginValidation(user);
        userBirthdayValidation(user);
    }

    private void userBirthdayValidation(User user) {
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private void userLoginValidation(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            throw new ConditionsNotMetException("Login не должен быть пустым или содержать пробелы.");
        }
    }

    private void userEmailValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ConditionsNotMetException("Email должен быть указан.");
        }
        if (!emailValidation(user.getEmail())) {
            throw new ConditionsNotMetException("Некорректный формат Email.");
        }
        if (users.containsValue(user)) {
            throw new DuplicatedDataException("Этот Email уже используется.");
        }
    }

    private boolean emailValidation(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
