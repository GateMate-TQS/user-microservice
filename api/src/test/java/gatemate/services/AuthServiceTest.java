package gatemate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import gatemate.data.User;
import gatemate.data.UserRole;
import gatemate.dtos.SignUpDto;
import gatemate.exceptions.InvalidJwtException;
import gatemate.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository repository;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(repository);
    }

    @Test
    @DisplayName("When load user by username then return user")
    void testLoadUserByUsername_UserExists() {
        User mockUser = new User("testuser", "password", UserRole.USER);
        when(repository.findByLogin("testuser")).thenReturn(mockUser);

        UserDetails userDetails = authService.loadUserByUsername("testuser");

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("testuser");
        verify(repository, times(1)).findByLogin("testuser");
    }

    @Test
    @DisplayName("When load user by username and user does not exist then throw exception")
    void testLoadUserByUsername_UserDoesNotExist() {
        when(repository.findByLogin("testuser")).thenReturn(null);

        assertThatThrownBy(() -> authService.loadUserByUsername("testuser"))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessage("User not found");

        verify(repository, times(1)).findByLogin("testuser");
    }

    @Test
    @DisplayName("When sign up with new user then return user details")
    void testSignUp_Success() throws InvalidJwtException {
        SignUpDto signUpDto = new SignUpDto("newuser", "newpassword", UserRole.USER);
        when(repository.findByLogin("newuser")).thenReturn(null);
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDetails userDetails = authService.signUp(signUpDto);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("newuser");
        verify(repository, times(1)).findByLogin("newuser");
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("When sign up with existing username then throw exception")
    void testSignUp_UsernameExists() {
        SignUpDto signUpDto = new SignUpDto("existinguser", "password", UserRole.USER);
        User existingUser = new User("existinguser", "password", UserRole.USER);
        when(repository.findByLogin("existinguser")).thenReturn(existingUser);

        assertThatThrownBy(() -> authService.signUp(signUpDto))
                .isInstanceOf(InvalidJwtException.class)
                .hasMessage("Username already exists");

        verify(repository, times(1)).findByLogin("existinguser");
        verify(repository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("When get all users then return all users")
    void testGetAllUsers() {
        User user1 = new User("user1", "password1", UserRole.USER);
        User user2 = new User("user2", "password2", UserRole.USER);
        List<User> users = Arrays.asList(user1, user2);
        when(repository.findAll()).thenReturn(users);

        List<User> result = authService.getAllUsers();

        assertThat(result)
                .isNotNull()
                .hasSize(2)
                .contains(user1, user2);
        verify(repository, times(1)).findAll();
    }

    @Test
    @DisplayName("When delete all users then delete all users")
    void testDeleteAllUsers() {
        doNothing().when(repository).deleteAll();

        authService.deleteAllUsers();

        verify(repository, times(1)).deleteAll();
    }

    @Test
    @DisplayName("When sign up with new user then return user details with encrypted password")
    void testSignUp_Success_EncryptedPassword() throws InvalidJwtException {
        SignUpDto signUpDto = new SignUpDto("newuser", "newpassword", UserRole.USER);
        when(repository.findByLogin("newuser")).thenReturn(null);
        when(repository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDetails userDetails = authService.signUp(signUpDto);

        assertThat(userDetails).isNotNull();
        assertThat(userDetails.getUsername()).isEqualTo("newuser");
        assertThat(userDetails.getPassword()).startsWith("$2a$");
        verify(repository, times(1)).findByLogin("newuser");
        verify(repository, times(1)).save(any(User.class));
    }
}