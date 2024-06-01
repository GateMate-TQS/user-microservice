package gatemate.repositories;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.security.core.userdetails.UserDetails;
import static org.assertj.core.api.Assertions.assertThat;

import gatemate.data.User;
import gatemate.data.UserRole;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("When save user then user is persisted")
    void whenSaveUser_thenUserIsPersisted() {
        User user = new User("testuser", "password", UserRole.USER);

        User savedUser = userRepository.save(user);

        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    @DisplayName("When find user by existing id then return user")
    void whenFindUserByExistingId_thenReturnUser() {
        User user = new User("testuser", "password", UserRole.USER);
        entityManager.persistAndFlush(user);

        User found = userRepository.findById(user.getId()).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getLogin()).isEqualTo(user.getLogin());
    }

    @Test
    @DisplayName("When find user by invalid id then return null")
    void whenFindUserByInvalidId_thenReturnNull() {
        User found = userRepository.findById(1L).orElse(null);

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("When update user then user is updated")
    void whenDeleteUser_thenUserIsRemoved() {
        User user = new User("testuser", "password", UserRole.USER);
        entityManager.persistAndFlush(user);

        userRepository.deleteById(user.getId());

        assertThat(userRepository.findById(user.getId())).isEmpty();
    }

    @Test
    @DisplayName("When find user by existing login then return user")
    void whenFindUserByExistingLogin_thenReturnUser() {
        User user = new User("testuser", "password", UserRole.USER);
        entityManager.persistAndFlush(user);

        UserDetails found = userRepository.findByLogin(user.getLogin());

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo(user.getLogin());
    }

    @Test
    @DisplayName("When find user by invalid login then return null")
    void whenFindUserByInvalidLogin_thenReturnNull() {
        UserDetails found = userRepository.findByLogin("testuser");

        assertThat(found).isNull();
    }

    @Test
    @DisplayName("When user exists by id then return true")
    void whenUserExistsById_thenReturnTrue() {
        User user = new User("testuser", "password", UserRole.USER);
        entityManager.persistAndFlush(user);

        assertThat(userRepository.existsById(user.getId())).isTrue();
    }

    @Test
    @DisplayName("When user does not exist by id then return false")
    void whenUserDoesNotExistById_thenReturnFalse() {
        assertThat(userRepository.existsById(999L)).isFalse();
    }
}
