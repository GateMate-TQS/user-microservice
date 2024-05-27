package gatemate.services;

import gatemate.data.User;
import gatemate.repositories.UserRepository;
import gatemate.dtos.SignUpDto;
import gatemate.exceptions.InvalidJwtException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService implements UserDetailsService {

    private UserRepository repository;

    @Autowired
    public AuthService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        UserDetails user = repository.findByLogin(username);
        if (user == null) {
            throw new InvalidJwtException("User not found");
        }

        return user;
    }

    public UserDetails signUp(SignUpDto data) throws InvalidJwtException {
        if (repository.findByLogin(data.login()) != null) {
            throw new InvalidJwtException("Username already exists");
        }
        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        User newUser = new User(data.login(), encryptedPassword, data.role());
        return repository.save(newUser);
    }

    public List<User> getAllUsers() {
        return repository.findAll();
    }

    public void deleteAllUsers() {
        repository.deleteAll();
    }
}
