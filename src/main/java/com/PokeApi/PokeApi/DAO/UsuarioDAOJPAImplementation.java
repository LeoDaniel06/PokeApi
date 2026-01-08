package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.RolJPA;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

@Repository
public class UsuarioDAOJPAImplementation implements IUsuarioJPA {
    
    @Autowired
    private EntityManager entityManager;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    @Transactional
    public Result Add(UsuarioJPA usuario) {
        Result result = new Result();
        try {
            if (usuario == null) {
                result.correct = false;
                result.errorMessage = "EL USsuario no puede ser nulo";
                result.status = 400;
                return result;
            }
            
            if (usuario.getPassword() != null && !usuario.getPassword().isEmpty()) {
                if (!usuario.getPassword().startsWith("$2a$")) {
                    usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
                    
                }
                
            }
            
            try {
            RolJPA rolAdmin = entityManager.createQuery(
                "SELECT r FROM RolJPA r WHERE r.NombreRol = :nombreRol", RolJPA.class)
                .setParameter("nombreRol", "Entrenador")
                .getSingleResult();
            
            usuario.setRolJPA(rolAdmin);
            
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = "Rol 'Admin' no encontrado en la base de datos";
            result.status = 404;
            return result;
        }
            
            entityManager.persist(usuario);
            entityManager.flush();
            
            result.correct = true;
            result.status = 201;
            result.object = usuario;
            
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = "No se Resgistro el Usuario";
            result.ex = ex;
            result.status = 500;
        }
        
        return result;
    }
    
}
