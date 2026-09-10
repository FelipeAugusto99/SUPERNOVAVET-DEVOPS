package Novamonitor.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers(HttpMethod.GET, "/pets/**")
                        .hasAnyRole("ADMIN", "VETERINARIO")

                        .requestMatchers(HttpMethod.POST, "/pets/**")
                        .hasAnyRole("ADMIN", "VETERINARIO")

                        .requestMatchers(HttpMethod.PUT, "/pets/**")
                        .hasAnyRole("ADMIN", "VETERINARIO")

                        .requestMatchers(HttpMethod.DELETE, "/pets/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/tutores/**")
                        .hasRole("ADMIN")

                        .anyRequest()
                        .authenticated()
                )

                .httpBasic(basic -> {});

        return http.build();
    }
}