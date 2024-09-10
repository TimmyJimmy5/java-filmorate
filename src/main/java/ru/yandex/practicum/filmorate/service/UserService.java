package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.BadInputException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public boolean addFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new RuntimeException("Нельзя добавить самого себя в друзья");
        }
        checkId(userId);
        checkId(friendId);
        return userStorage.addFriend(userId, friendId);
    }

    public boolean removeFriend(Long userId, Long friendId) {
        if (userId.equals(friendId)) {
            throw new RuntimeException("Нельзя удалить самого себя из друзей");
        }
        checkId(userId);
        checkId(friendId);
        return userStorage.removeFriend(userId, friendId);
    }

    public Set<User> getCommonFriends(Long userId, Long otherId) {
        checkId(userId);
        checkId(otherId);
        if (userStorage.getUsers().get(userId).getFriends().isEmpty()) {
            throw new NotFoundException("У пользователя " + userId + " список друзей пока пуст");
        }
        if (userStorage.getUsers().get(otherId).getFriends().isEmpty()) {
            throw new NotFoundException("У пользователя " + otherId + " список друзей пока пуст");
        }
        return userStorage.getCommonFriends(userId, otherId);
    }

    public Set<User> getFriends(Long userId) {
        checkId(userId);
        if (userStorage.getFriends(userId).isEmpty()) {
            throw new NotFoundException("У пользователя " + userId + " пока нет друзей.");
        }
        return userStorage.getFriends(userId);
    }

    public void checkId(Long id) {
        if (!userStorage.getUsers().containsKey(id)) {
            throw new NotFoundException("Объекта с ID " + id + " не существует");
        }
    }

    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    public User create(User user) {
        log.debug("Beginning new user data validation.");
        userNullCheck(user);
        userEmailValidation(user);
        userEmailTakenCheck(user);
        userLoginValidation(user);
        userBirthdayValidation(user);
        userLoginToEmptyName(user);
        log.debug("All new user data validations passed. Setting new id for this user.");
        user.setId(getNextId());
        log.debug("New user's id is {}. Putting into memory.", user.getId());
        userStorage.create(user);
        log.debug("New user {} successfully added.", user.getId());
        return user;
    }

    public User update(User newUser) {
        userNullCheck(newUser);
        userNullIdCheck(newUser);
        if (userStorage.getUsers().containsKey(newUser.getId())) {
            log.debug("Beginning user data validation.");
            User oldUser = userStorage.getUsers().get(newUser.getId());
            updateNewUserFieldsFromOldUser(newUser, oldUser);
            userEmailValidation(newUser);
            userEmailTakenCheck(newUser);
            userLoginToEmptyName(newUser);
            userLoginValidation(newUser);
            userBirthdayValidation(newUser);
            log.debug("All validations passed. Putting new user data {} into memory.", newUser.getId());
            userStorage.update(newUser);
            log.debug("User {} successfully updated.", newUser.getId());
            return newUser;
        }
        log.error("Error: Пользователь {} не найден.", newUser.getId());
        throw new NotFoundException("Пользователь не найден.");
    }

    private long getNextId() {
        long currentMaxId = userStorage.getUsers().keySet()
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
        if (newUser.getEmail().equals(oldUser.getEmail())) {
            log.debug("Email is the same as before. Filling it with the same Email value.");
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
            throw new BadInputException("Id должен быть указан.");
        }
    }

    private void userNullCheck(User user) {
        if (user == null) {
            log.error("Тело запроса не может быть пустым.");
            throw new BadInputException("Тело запроса не может быть пустым.");
        }
    }

    private void userBirthdayValidation(User user) {
        if (user.getBirthday() == null) {
            log.error("Error: Дата рождения не указана.");
            throw new BadInputException("Дата рождения не указана.");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Error: Дата рождения не может быть в будущем.");
            throw new BadInputException("Дата рождения не может быть в будущем");
        }
    }

    private void userLoginValidation(User user) {
        if (user.getLogin() == null || user.getLogin().contains(" ")) {
            log.error("Error: Login не должен быть пустым или содержать пробелы.");
            throw new BadInputException("Login не должен быть пустым или содержать пробелы.");
        }
    }

    private void userEmailTakenCheck(User user) {
        if (userStorage.getUsers().containsValue(user)) {
            log.error("Error: Этот электронный адрес уже используется.");
            throw new DuplicatedDataException("Этот электронный адрес уже используется.");
        }
    }

    private void userEmailValidation(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Error: Email должен быть указан.");
            throw new BadInputException("Email должен быть указан.");
        }
        if (!emailValidation(user.getEmail())) {
            log.error("Error: Некорректный формат Email.");
            throw new BadInputException("Некорректный формат Email.");
        }
    }

    private boolean emailValidation(String email) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}
