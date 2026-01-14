package com.PokeApi.PokeApi.JWT;

import com.PokeApi.PokeApi.JPA.UsuarioDetails;
import com.PokeApi.PokeApi.Service.UserDetailsJPAService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private final UserDetailsJPAService usuarioDetailsJPAService;
    private final JwtUtils jwtUtils;

    public JwtAuthFilter(UserDetailsJPAService usuarioDetailsJPAService,
                         JwtUtils jwtUtils) {
        this.usuarioDetailsJPAService = usuarioDetailsJPAService;
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        
        if (path.startsWith("/pokedex/login")
                || path.startsWith("/pokedex/registro")
                || path.startsWith("/fonts/")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/images/") 
                || path.startsWith("/api/thread")){

            filterChain.doFilter(request, response);
            return;
        }

        String jwt = getJwtFromRequest(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String username = jwtUtils.extractUsername(jwt);
            Long idUsuario = jwtUtils.extractUserId(jwt);

            if (username != null
                    && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails =
                        usuarioDetailsJPAService.loadUserByUsername(username);

                if (jwtUtils.isTokenValid(jwt, userDetails)) {

                    UsuarioDetails usuarioDetails = new UsuarioDetails(
                            idUsuario.intValue(),
                            userDetails.getUsername(),
                            userDetails.getPassword(),
                            userDetails.getAuthorities()
                    );

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    usuarioDetails,
                                    null,
                                    usuarioDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(request)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(authToken);
                }
            }

        } catch (Exception e) {
            
        }

        filterChain.doFilter(request, response);
    }

    private String getJwtFromRequest(HttpServletRequest request) {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("JWT_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }

        return null;
    }
}
