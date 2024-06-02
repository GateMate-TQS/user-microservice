package gatemate.controllers;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.userdetails.UserDetails;

import gatemate.data.User;
import gatemate.dtos.JwtDto;
import gatemate.dtos.SignInDto;
import gatemate.dtos.SignUpDto;
import gatemate.dtos.UserInfoDto;
import gatemate.exceptions.InvalidJwtException;
import gatemate.services.AuthService;
import gatemate.config.auth.TokenProvider;
import gatemate.repositories.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

@RestController
@RequestMapping("/")
public class AuthController {
    private AuthenticationManager authenticationManager;
    private AuthService service;
    private TokenProvider tokenService;
    private UserRepository userRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, AuthService service,
            TokenProvider tokenService, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.service = service;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    @Operation(summary = "Cadastrar um novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro ao criar usuário", content = @Content(schema = @Schema(implementation = String.class)))
    })
    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody @Valid SignUpDto data) {
        try {
            service.signUp(data);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (InvalidJwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error signing up: " + e.getMessage());
        }
    }

    @Operation(summary = "Autenticar um usuário e retornar um token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida", content = @Content(schema = @Schema(implementation = JwtDto.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas", content = @Content(schema = @Schema(implementation = JwtDto.class)))
    })
    @PostMapping("/login")
    public ResponseEntity<JwtDto> signIn(@RequestBody @Valid SignInDto data) {
        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var authUser = authenticationManager.authenticate(usernamePassword);
            var accessToken = tokenService.generateAccessToken((User) authUser.getPrincipal());
            return ResponseEntity.ok(new JwtDto(accessToken));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new JwtDto(""));
        }
    }

    @Operation(summary = "Obter detalhes do usuário a partir do token JWT")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes do usuário retornados com sucesso", content = @Content(schema = @Schema(implementation = UserDetails.class))),
            @ApiResponse(responseCode = "400", description = "Token JWT inválido", content = @Content)
    })
    @PostMapping("/user")
    public ResponseEntity<UserDetails> getUser(@RequestBody @Valid JwtDto token) {
        try {
            UserDetails user = tokenService.getUserFromToken(token.accessToken());
            return ResponseEntity.ok(user);
        } catch (InvalidJwtException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Operation(summary = "Obter detalhes do usuário a partir do login")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalhes do usuário retornados com sucesso", content = @Content(schema = @Schema(implementation = UserDetails.class))),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado", content = @Content)
    })
    @PostMapping("/userId")
    public ResponseEntity<UserDetails> getUser(@RequestBody @Valid UserInfoDto data) {
        UserDetails user = userRepository.findByLogin(data.login());

        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
