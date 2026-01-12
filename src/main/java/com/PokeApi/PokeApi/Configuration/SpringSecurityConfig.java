package com.PokeApi.PokeApi.Configuration;

import com.PokeApi.PokeApi.JWT.JwtAuthFilter;
import com.PokeApi.PokeApi.Service.UserDetailsJPAService;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    private final UserDetailsJPAService usuarioDetailsJPAService;
    private final JwtAuthFilter jwtAuthFilter;

    public SpringSecurityConfig(UserDetailsJPAService usuarioDetailsJPAService,
            JwtAuthFilter jwtAuthFilter) {
        this.usuarioDetailsJPAService = usuarioDetailsJPAService;
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/pokedex/login",
                        "/pokedex/registro",
                        "/pokedex",
                        "/pokedex/detail/**",
                        "/fonts/**"
                ).permitAll()
                .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter.class)
                .formLogin(form -> form
                .loginPage("/pokedex/login")
                .loginProcessingUrl("/pokedex/login")
                .defaultSuccessUrl("/pokedex", true)
                .failureUrl("/pokedex/login?error=true")
                .permitAll()
                )
                .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/pokedex/login?logout")
                .addLogoutHandler((request, response, authentication) ->{
                    Cookie cookie = new Cookie("JWT_TOKEN", null);
                    cookie.setPath("/");
                    cookie.setHttpOnly(true);
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                })
                .invalidateHttpSession(true)
                .clearAuthentication(true)
                .deleteCookies("JSESSIONID","JWT_TOKEN")
                );

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(usuarioDetailsJPAService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
