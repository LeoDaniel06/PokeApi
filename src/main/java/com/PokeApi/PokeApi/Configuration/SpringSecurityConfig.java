package com.PokeApi.PokeApi.Configuration;

import com.PokeApi.PokeApi.JWT.JwtAuthFilter;
import com.PokeApi.PokeApi.Service.UserDetailsJPAService;
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

    public SpringSecurityConfig(UserDetailsJPAService usuarioDetailsJPAService) {
        this.usuarioDetailsJPAService = usuarioDetailsJPAService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthFilter jwtAuthFilter) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // ← Importante: CSRF deshabilitado
                .authorizeHttpRequests(configurer -> configurer
                .requestMatchers("/fonts/**").permitAll()
                .requestMatchers("/login/**", "/LoginPokeApi").permitAll()
                .requestMatchers("/pokedex/registro").permitAll()
                .requestMatchers("/pokedex/**").permitAll()
                .anyRequest().authenticated())
                .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

//    @Bean
//    public AuthenticationProvider authenticationProvider() {
//        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
//        provider.setUserDetailsService(usuarioDetailsJPAService);
//        provider.setPasswordEncoder(passwordEncoder()); 
//        return provider;
//    }
}
