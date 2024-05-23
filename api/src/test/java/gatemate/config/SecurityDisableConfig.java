package gatemate.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@TestConfiguration
public class SecurityDisableConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF using the new lambda DSL
            .authorizeHttpRequests(authz -> authz  // Use the lambda DSL for authorization
                .anyRequest().permitAll()
            );
        return http.build();
    }
}