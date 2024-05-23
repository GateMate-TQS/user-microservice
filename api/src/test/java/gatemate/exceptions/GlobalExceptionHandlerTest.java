package gatemate.exceptions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    @InjectMocks
    GlobalExceptionHandler globalExceptionHandler;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(globalExceptionHandler).build();
    }

    @Test
    void testHandleGeneralExceptions() {
        Exception exception = new Exception("Test exception");
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleGeneralExceptions(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(List.of("Test exception"), response.getBody().get("errors"));
    }

    @Test
    void testHandleRuntimeExceptions() {
        RuntimeException runtimeException = new RuntimeException("Test runtime exception");
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleRuntimeExceptions(runtimeException);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(List.of("Test runtime exception"), response.getBody().get("errors"));
    }

    @Test
    void testHandleJwtErrors() {
        InvalidJwtException jwtException = new InvalidJwtException("Test JWT exception");
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleJwtErrors(jwtException);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(List.of("Test JWT exception"), response.getBody().get("errors"));
    }

    @Test
    void testHandleBadCredentialsError() {
        BadCredentialsException badCredentialsException = new BadCredentialsException("Test credentials exception");
        ResponseEntity<Map<String, List<String>>> response = globalExceptionHandler.handleBadCredentialsError(badCredentialsException);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(List.of("Test credentials exception"), response.getBody().get("errors"));
    }

}