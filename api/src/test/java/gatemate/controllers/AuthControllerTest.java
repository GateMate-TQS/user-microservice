package gatemate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.MockMvc;

import io.restassured.module.mockmvc.RestAssuredMockMvc;
import gatemate.config.auth.TokenProvider;
import gatemate.data.User;
import gatemate.data.UserRole;
import gatemate.services.AuthService;
import gatemate.dtos.JwtDto;
import gatemate.dtos.SignInDto;
import gatemate.dtos.SignUpDto;
import gatemate.exceptions.InvalidJwtException;
import gatemate.repositories.UserRepository;
import gatemate.dtos.UserInfoDto;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import gatemate.config.SecurityDisableConfig;

@Import(SecurityDisableConfig.class)
@WebMvcTest(AuthController.class)
class AuthControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private AuthService service;

        @MockBean
        private TokenProvider tokenService;

        @MockBean
        private UserRepository userRepository;

        @MockBean
        private AuthenticationManager authenticationManager;

        @BeforeEach
        void setUp() {
                RestAssuredMockMvc.mockMvc(mockMvc);
        }

        @Test
        @DisplayName("POST /signup with valid data should return 201 Created")
        void signUpWithValidDataShouldReturnCreated() {
                SignUpDto data = new SignUpDto("user", "password", UserRole.USER);

                when(service.signUp(data)).thenReturn(createMockUser());

                RestAssuredMockMvc.given()
                                .contentType("application/json")
                                .body(data)
                                .when()
                                .post("/signup")
                                .then()
                                .statusCode(HttpStatus.CREATED.value());

                // Verify that the signUp method of service is called once
                verify(service, times(1)).signUp(data);
        }

        @Test
        @DisplayName("POST /signup with repeated login should return 400 Bad Request")
        void signUpWithRepeatedLoginShouldReturnBadRequest() {
                SignUpDto data = new SignUpDto("user", "password", UserRole.USER);

                when(service.signUp(data)).thenThrow(new InvalidJwtException("Username already exists"));

                RestAssuredMockMvc.given()
                                .contentType("application/json")
                                .body(data)
                                .when()
                                .post("/signup")
                                .then()
                                .statusCode(HttpStatus.BAD_REQUEST.value());

                verify(service, times(1)).signUp(data);
        }

        @Test
        @DisplayName("POST /login with valid credentials should return JWT token")
        void signInWithValidCredentialsShouldReturnJwt() throws Exception {
                SignInDto signInData = new SignInDto("user", "password"); // Correct credentials

                User mockUser = new User("user", "password", UserRole.USER);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                                signInData.login(), signInData.password());

                String expectedToken = "generatedJwtToken";

                when(authenticationManager.authenticate(authenticationToken))
                                .thenReturn(new TestingAuthenticationToken(mockUser, null));
                when(tokenService.generateAccessToken(mockUser)).thenReturn(expectedToken);

                RestAssuredMockMvc.given()
                                .contentType("application/json")
                                .body(signInData)
                                .when()
                                .post("/login")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .body("accessToken", equalTo(expectedToken));

                verify(authenticationManager, times(1)).authenticate(authenticationToken);
                verify(tokenService, times(1)).generateAccessToken(mockUser);
        }

        @Test
        @DisplayName("POST /login with invalid credentials should return 400 Bad Request")
        void signInWithInvalidCredentialsShouldReturnBadRequest() throws Exception {
                SignInDto signInData = new SignInDto("user", "password"); // Incorrect credentials

                when(authenticationManager
                                .authenticate(new UsernamePasswordAuthenticationToken(signInData.login(),
                                                signInData.password())))
                                .thenThrow(new InvalidJwtException("Invalid credentials"));

                RestAssuredMockMvc.given()
                                .contentType("application/json")
                                .body(signInData)
                                .when()
                                .post("/login")
                                .then()
                                .statusCode(HttpStatus.BAD_REQUEST.value());

                verify(authenticationManager, times(1))
                                .authenticate(new UsernamePasswordAuthenticationToken(signInData.login(),
                                                signInData.password()));
        }

        @Test
        @DisplayName("POST /user with valid token should return user details")
        void getUserWithValidTokenShouldReturnUserDetails() {
                String validToken = "someValidJWTToken";
                UserDetails mockUserDetails = createMockUser();

                JwtDto jwtDto = new JwtDto(validToken);

                when(tokenService.getUserFromToken(validToken)).thenReturn(mockUserDetails);

                RestAssuredMockMvc.given()
                                .contentType("application/json")
                                .body(jwtDto)
                                .when()
                                .post("/user")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .body("login", equalTo("user"))
                                .body("role", equalTo("USER"));

                verify(tokenService, times(1)).getUserFromToken(validToken);
        }

        @Test
        @DisplayName("GET /user with valid login should return user details")
        void getUserWithValidLoginShouldReturnUserDetails() {
                UserDetails mockUserDetails = createMockUser();
                UserInfoDto userInfoDto = new UserInfoDto("user");

                when(userRepository.findByLogin("user")).thenReturn(mockUserDetails);

                RestAssuredMockMvc.given()
                                .contentType("application/json")
                                .body(userInfoDto)
                                .when()
                                .post("/userId")
                                .then()
                                .statusCode(HttpStatus.OK.value())
                                .body("login", equalTo("user"))
                                .body("role", equalTo("USER"));

                verify(userRepository, times(1)).findByLogin("user");
        }

        private UserDetails createMockUser() {
                return new User("user", "password", UserRole.USER);
        }
}
