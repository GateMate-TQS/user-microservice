package gatemate.controllers;

import org.junit.jupiter.api.BeforeEach;
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
import gatemate.repositories.UserRepository;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
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
    void signUpWithValidDataShouldReturnCreated() {
        // Given
        SignUpDto data = new SignUpDto("user", "password", UserRole.USER);

        // When
        when(service.signUp(data)).thenReturn(createMockUser());

        // Then
        given()
                .contentType("application/json")
                .body(data)
                .when()
                .post("/signup")
                .then()
                .statusCode(HttpStatus.CREATED.value());

        // Verify that the signUp method of service is called once
        verify(service, times(1)).signUp(data);
    }

    private UserDetails createMockUser() {
        return new User("user", "password", UserRole.USER);
    }

    @Test
    void signInWithValidCredentialsShouldReturnJwt() throws Exception {
        // Given
        SignInDto signInData = new SignInDto("user", "password"); // Correct credentials

        User mockUser = new User("user", "password", UserRole.USER);
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                signInData.login(), signInData.password());

        String expectedToken = "generatedJwtToken";

        // When
        when(authenticationManager.authenticate(authenticationToken))
                .thenReturn(new TestingAuthenticationToken(mockUser, null));
        when(tokenService.generateAccessToken(mockUser)).thenReturn(expectedToken);

        // Then
        given()
                .contentType("application/json")
                .body(signInData)
                .when()
                .post("/login")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("accessToken", equalTo(expectedToken));

        // Verify
        verify(authenticationManager, times(1)).authenticate(authenticationToken);
        verify(tokenService, times(1)).generateAccessToken(mockUser);
    }

    @Test
    void getUserWithValidTokenShouldReturnUserDetails(){
        // Given
        String validToken = "someValidJWTToken";
        UserDetails mockUserDetails = createMockUser();

        JwtDto jwtDto = new JwtDto(validToken); // Create a JwtDto with the valid token

        // When
        when(tokenService.getUserFromToken(validToken)).thenReturn(mockUserDetails);

        // Then
        given()
                .contentType("application/json")
                .body(jwtDto) // Send the JwtDto in the request body
                .when()
                .post("/user")
                .then()
                .statusCode(HttpStatus.OK.value())
                .body("login", equalTo("user"))
                .body("role", equalTo("USER")); // Add more assertions as needed

        // Verify that the getUserFromToken method is called once with the correct token
        verify(tokenService, times(1)).getUserFromToken(validToken);
    }

}
