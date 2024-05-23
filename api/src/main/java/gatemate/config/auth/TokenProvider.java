package gatemate.config.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.JWTCreationException;

import gatemate.Generated;
import gatemate.data.User;
import gatemate.repositories.UserRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;


@Generated
@Service
public class TokenProvider {
    @Value("${security.jwt.token.secret-key}")
    private String JWTSECRET;

    private UserRepository userRepository;

    @Autowired
    public TokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String generateAccessToken(User user) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWTSECRET);
            return JWT.create()
                    .withSubject(user.getUsername())
                    .withClaim("username", user.getUsername())
                    .withClaim("role", user.getUserRole().getValue())
                    .withExpiresAt(genAccessExpirationDate())
                    .sign(algorithm);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Error while generating token", exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWTSECRET);
            return JWT.require(algorithm)
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while validating token", exception);
        }
    }

    public UserDetails getUserFromToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(JWTSECRET);
            var decoded = JWT.require(algorithm)
                    .build()
                    .verify(token);
            UserDetails user = userRepository.findByLogin(decoded.getClaim("username").asString());

            if (user == null) {
                throw new JWTVerificationException("User not found");
            }

            return user;
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Error while getting user from token", exception);
        }
    }

    private Instant genAccessExpirationDate() {
        return LocalDateTime.now().plusHours(63).toInstant(ZoneOffset.of("-03:00"));
    }
}