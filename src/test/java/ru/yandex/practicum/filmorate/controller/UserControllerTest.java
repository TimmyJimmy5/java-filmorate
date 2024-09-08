package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;
import java.io.IOException;
import java.time.LocalDate;

public class UserControllerTest {
    UserController userController = new UserController();

    @Test
    public void userCreateSuccess() throws IOException {
        User user = new User();
        user.setName("Test");
        user.setLogin("Login");
        user.setEmail("abc@abc.com");
        user.setBirthday(LocalDate.of(1996, 3, 3));
        userController.create(user);
        Assertions.assertEquals(1, userController.findAll().size());
    }

    @Test
    public void assertErrorUponEmptyUserCreate() throws IOException {
        ConditionsNotMetException thrown = Assertions.assertThrows(ConditionsNotMetException.class, () -> userController.create(null));
        Assertions.assertEquals("Тело запроса не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void assertErrorUponEmptyUserUpdate() throws IOException {
        ConditionsNotMetException thrown = Assertions.assertThrows(ConditionsNotMetException.class, () -> userController.update(null));
        Assertions.assertEquals("Тело запроса не может быть пустым.", thrown.getMessage());
    }

    @Test
    public void assertErrorUponEmptySpacesLoginCreate() throws IOException {
        User user = new User();
        user.setName("Test");
        user.setLogin("Log in");
        user.setEmail("abc@abc.com");
        user.setBirthday(LocalDate.of(1996, 3, 3));
        ConditionsNotMetException thrown = Assertions.assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        Assertions.assertEquals("Login не должен быть пустым или содержать пробелы.", thrown.getMessage());
    }

    @Test
    public void assertErrorUponEmptyLoginCreate() throws IOException {
        User user = new User();
        user.setName("Test");
        user.setEmail("abc@abc.com");
        user.setBirthday(LocalDate.of(1996, 3, 3));
        ConditionsNotMetException thrown = Assertions.assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        Assertions.assertEquals("Login не должен быть пустым или содержать пробелы.", thrown.getMessage());
    }

    @Test
    public void assertErrorUponEmptyEmailCreate() throws IOException {
        User user = new User();
        user.setName("Test");
        user.setLogin("Login");
        user.setBirthday(LocalDate.of(1996, 3, 3));
        ConditionsNotMetException thrown = Assertions.assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        Assertions.assertEquals("Email должен быть указан.", thrown.getMessage());
    }

    @Test
    public void assertErrorUponIncorrectEmailCreate() throws IOException {
        User user = new User();
        user.setName("Test");
        user.setLogin("Login");
        user.setEmail("abcabc.com@");
        user.setBirthday(LocalDate.of(1996, 3, 3));
        ConditionsNotMetException thrown = Assertions.assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        Assertions.assertEquals("Некорректный формат Email.", thrown.getMessage());
    }

    @Test
    public void assertErrorUponCreateUserWithFutureBirthday() throws IOException {
        User user = new User();
        user.setName("Test");
        user.setLogin("Login");
        user.setEmail("abc@abc.com");
        user.setBirthday(LocalDate.of(2996, 3, 3));
        ConditionsNotMetException thrown = Assertions.assertThrows(ConditionsNotMetException.class, () -> userController.create(user));
        Assertions.assertEquals("Дата рождения не может быть в будущем", thrown.getMessage());
    }

}
