package gatemate.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.userdetails.UserDetails;

import gatemate.data.User;
import gatemate.data.UserRole;

@DataJpaTest
class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Find user by id")
    void whenFindUserByExistingId_thenReturnUser() {
        User user = new User("testuser", "password", UserRole.USER);
        entityManager.persistAndFlush(user);

        User found = userRepository.findById(user.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    @DisplayName("Find user by invalid id")
    void whenFindUserByInvalidId_thenReturnNull() {
        User found = userRepository.findById(1L).orElse(null);

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("Find user by login")
    void whenFindUserByExistingLogin_thenReturnUser() {
        User user = new User("testuser", "password", UserRole.USER);
        entityManager.persistAndFlush(user);

        UserDetails found = userRepository.findByLogin(user.getLogin());

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo(user.getLogin());
    }

    @Test
    @DisplayName("Find user by invalid login")
    void whenFindUserByInvalidLogin_thenReturnNull() {
        UserDetails found = userRepository.findByLogin("testuser");

        assertThat(found).isNull();
    }
}
