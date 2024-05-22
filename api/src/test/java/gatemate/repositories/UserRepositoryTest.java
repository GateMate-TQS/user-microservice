package gatemate.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.core.userdetails.UserDetails;

import gatemate.data.User;
import gatemate.data.UserRole;


@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("Test findByLogin returns UserDetails when user exists")
    public void testFindByLogin() {
        // Given
        String login = "testuser";
        User user = new User(login, "password", UserRole.USER);
        userRepository.save(user);

        // When
        UserDetails userDetails = userRepository.findByLogin(login);

        // Then
        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo(login);
    }
    
}
