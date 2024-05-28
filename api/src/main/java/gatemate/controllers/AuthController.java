package gatemate.controllers;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestParam;

import gatemate.data.User;
import gatemate.dtos.JwtDto;
import gatemate.dtos.SignInDto;
import gatemate.dtos.SignUpDto;
import gatemate.dtos.UserInfoDto;
import gatemate.services.AuthService;
import gatemate.config.auth.TokenProvider;
import gatemate.repositories.UserRepository;

@RestController
@RequestMapping("/")
public class AuthController {
    private AuthenticationManager authenticationManager;

    private AuthService service;

    private TokenProvider tokenService;

    private AuthService authService;

    private UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, AuthService service,
            TokenProvider tokenService, AuthService authService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.service = service;
        this.tokenService = tokenService;
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/signup")
    public ResponseEntity<Void> signUp(@RequestBody @Valid SignUpDto data) {
        service.signUp(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<JwtDto> signIn(@RequestBody @Valid SignInDto data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var authUser = authenticationManager.authenticate(usernamePassword);
        var accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());

        return ResponseEntity.ok(new JwtDto(accessToken));
    }

    @PostMapping("/user")
    public ResponseEntity<UserDetails> getUser(@RequestBody @Valid JwtDto token) {
        UserDetails user = tokenService.getUserFromToken(token.accessToken());
        return ResponseEntity.ok(user);
    }

    @GetMapping("/user")
    public ResponseEntity<UserDetails> getUser(@RequestBody @Valid UserInfoDto data) {
        UserDetails user = userRepository.findByLogin(data.login());

        return ResponseEntity.ok(user);
    }

}