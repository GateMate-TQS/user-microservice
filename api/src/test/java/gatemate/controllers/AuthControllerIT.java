package gatemate.controllers;

import io.restassured.module.mockmvc.RestAssuredMockMvc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import gatemate.data.UserRole;
import gatemate.dtos.JwtDto;
import gatemate.dtos.SignInDto;
import gatemate.dtos.SignUpDto;
import gatemate.repositories.UserRepository;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import static org.hamcrest.Matchers.emptyString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;

@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@SpringBootTest
class AuthControllerIT {
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  void setUp() {
    RestAssuredMockMvc.mockMvc(mockMvc);

    SignUpDto data = new SignUpDto("user", "password", UserRole.USER);

    RestAssuredMockMvc.given()
        .contentType("application/json")
        .body(data)
        .when()
        .post("/signup")
        .then()
        .statusCode(HttpStatus.CREATED.value());
  }

  @AfterEach
  void clearDatabase() {
    userRepository.deleteAll();
  }

  @Test
  @DisplayName("POST /signup with valid data should return 201 Created")
  void signUpWithValidDataShouldReturnCreated() {
    SignUpDto data = new SignUpDto("user1", "password", UserRole.USER);

    RestAssuredMockMvc.given()
        .contentType("application/json")
        .body(data)
        .when()
        .post("/signup")
        .then()
        .statusCode(HttpStatus.CREATED.value());
  }

  @Test
  @DisplayName("POST /signup with repeated login should return 400 Bad Request")
  void signUpWithRepeatedLoginShouldReturnBadRequest() {
    SignUpDto data = new SignUpDto("user", "password", UserRole.USER);

    RestAssuredMockMvc.given()
        .contentType("application/json")
        .body(data)
        .when()
        .post("/signup")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value());
  }

  @Test
  @DisplayName("POST /login with valid credentials should return JWT token")
  void signInWithValidCredentialsShouldReturnJwt() {
    SignInDto signInData = new SignInDto("user", "password");

    RestAssuredMockMvc.given()
        .contentType("application/json")
        .body(signInData)
        .when()
        .post("/login")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("accessToken", is(not(emptyString())));
    ;
  }

  @Test
  @DisplayName("POST /login with invalid credentials should return 400 Bad Request")
  void signInWithInvalidCredentialsShouldReturnUnauthorized() {
    SignInDto signInData = new SignInDto("user2", "password");

    RestAssuredMockMvc.given()
        .contentType("application/json")
        .body(signInData)
        .when()
        .post("/login")
        .then()
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
        .body("message", equalTo(null));
  }

  @Test
  @DisplayName("POST /user with valid token should return user details")
  void getUserWithValidTokenShouldReturnUserDetails() {
    SignInDto signInData = new SignInDto("user", "password");

    String token = RestAssuredMockMvc.given()
        .contentType("application/json")
        .body(signInData)
        .when()
        .post("/login")
        .then()
        .extract()
        .path("accessToken");

    JwtDto jwt = new JwtDto(token);

    RestAssuredMockMvc.given()
        .contentType("application/json")
        .body(jwt)
        .when()
        .post("/user")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("login", equalTo("user"));
  }

}
