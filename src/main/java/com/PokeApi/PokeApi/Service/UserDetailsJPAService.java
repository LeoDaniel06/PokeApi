
package com.PokeApi.PokeApi.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.PokeApi.PokeApi.DAO.IUsuarioJPARepository;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import java.util.List;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

@Service
public class UserDetailsJPAService implements UserDetailsService{

    private final IUsuarioJPARepository iUsuarioJPARepository;
    
    public UserDetailsJPAService(IUsuarioJPARepository iUsuarioJPARepository1){
        this.iUsuarioJPARepository = iUsuarioJPARepository1;
    }
            
    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        UsuarioJPA usuario = iUsuarioJPARepository.findByUserName(userName);
        
        List<GrantedAuthority> authorities = List.of(
        new SimpleGrantedAuthority("ROLE_" + usuario.getRolJPA().getNombreRol().toUpperCase()));
        
        return User.withUsername(usuario.getUserName())
                .password(usuario.getPassword())
                .authorities(authorities)
                .build();
    }

}
