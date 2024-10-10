package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
/*
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.dal.repository.UserRepository;
import ru.yandex.practicum.filmorate.model.User;
import java.time.LocalDate;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
*/
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmoRateApplicationTests {
	/*
	private final UserRepository userRepository;

	@Test
	public void testCreateUser() {
		User newUser = new User();
		newUser.setEmail("test@mail.ru");
		newUser.setLogin("test");
		newUser.setName("test");
		newUser.setBirthday(LocalDate.parse("2002-03-04"));

		User userCreated = userRepository.save(newUser);

		assertThat(newUser.getEmail(), containsString(userCreated.getEmail()));
	}

	@Test
	public void testFindUserById() {

		Optional<User> userOptional = userRepository.findById(1L);

		assertThat(userOptional)
				.isPresent()
				.hasValueSatisfying(user ->
						assertThat(user).hasFieldOrPropertyWithValue("id", 1)
				);
	}

 */
}
