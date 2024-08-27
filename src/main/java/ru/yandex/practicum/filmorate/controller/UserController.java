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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) {
            log.debug("Beginning new user data validation.");
            userNullCheck(user);
            userEmailTakenCheck(user);
            userEmailValidation(user);
            userLoginValidation(user);
            userBirthdayValidation(user);
            userLoginToEmptyName(user);
            log.debug("All new user data validations passed. Setting new id for this user.");
            user.setId(getNextId());
            log.debug("New user's id is {}. Putting into memory.", user.getId());
            users.put(user.getId(), user);
            log.debug("New user {} successfully added.", user.getId());
            return user;
    }

    @PutMapping
    public User update(@RequestBody User newUser) {
        userNullCheck(newUser);
        if (users.containsKey(newUser.getId())) {
            log.debug("Beginning user data validation.");
            userNullIdCheck(newUser);
            userEmailTakenCheck(newUser);
            User oldUser = users.get(newUser.getId());
            updateNewUserFieldsFromOldUser(newUser, oldUser);
            userLoginToEmptyName(newUser);
            userEmailValidation(newUser);
            userLoginValidation(newUser);
            userBirthdayValidation(newUser);
            log.debug("All validations passed. Putting new user data {} into memory.", newUser.getId());
            users.put(newUser.getId(), newUser);
            log.debug("User {} successfully updated.", newUser.getId());
            return newUser;
        }
        log.error("Error: Пользователь {} не найден.", newUser.getId());
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
            log.debug("Username is empty. Filling it with login value.");
            user.setName(user.getLogin());
        }
    }

    private void updateNewUserFieldsFromOldUser(User newUser, User oldUser) {
        if (newUser.getEmail() == null) {
            log.debug("Email is empty. Filling it with previous Email value.");
            newUser.setEmail(oldUser.getEmail());
        }
        if (newUser.getLogin() == null) {
            log.debug("Login is empty. Filling it with previous Login value.");
            newUser.setLogin(oldUser.getLogin());
        }
        if (newUser.getBirthday() == null) {
            log.debug("Birthday is empty. Filling it with previous Birthday value.");
            newUser.setBirthday(oldUser.getBirthday());
        }
    }

    private void userNullIdCheck(User user) {
        if (user.getId() == null) {
            log.error("Error: Id должен быть указан.");
            throw new ConditionsNotMetException("Id должен быть указан.");
        }
    }

    private void userNullCheck(User user) {
        if (user == null) {
            log.error("Тело запроса не может быть пустым.");
            throw new ConditionsNotMetException("Тело запроса не может быть пустым.");
        }
    }

    private void userBirthdayValidation(User user) {
        if (user.getBirthday() == null) {
            log.error("Error: Дата рождения не указана.");
            throw new ConditionsNotMetException("Дата рождения не указана.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Error: Дата рождения не может быть в будущем.");
            throw new ConditionsNotMetException("Дата рождения не может быть в будущем");
        }
    }

    private void userLoginValidation(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.error("Error: Login не должен быть пустым или содержать пробелы.");
            throw new ConditionsNotMetException("Login не должен быть пустым или содержать пробелы.");
        }
    }
    private void userEmailTakenCheck(User user) {
        if (users.containsValue(user)) {
            log.error("Error: Этот электронный адрес уже используется.");
            throw new DuplicatedDataException("Этот электронный адрес уже используется.");
        }
    }

    private void userEmailValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Error: Email должен быть указан.");
            throw new ConditionsNotMetException("Email должен быть указан.");
        }
        if (!emailValidation(user.getEmail())) {
            log.error("Error: Некорректный формат Email.");
            throw new ConditionsNotMetException("Некорректный формат Email.");
        }
    }

    private boolean emailValidation(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
