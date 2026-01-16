
package com.PokeApi.PokeApi.JPA;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


public class UsuarioDetails implements UserDetails {

    private final int id;
    private final String username;
    private final String password;
    private final int isVerified;
    private final Collection<? extends GrantedAuthority> authorities;

    public UsuarioDetails(int id, String username, String password, int isVerified,
                          Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.isVerified = isVerified;
        this.authorities = authorities;
    }

    public int getId() {
        return id;
    }
    
    public int getIsVerified() {
        return isVerified;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return isVerified == 1; }
}

