

package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUsuarioJPARepository extends JpaRepository<UsuarioJPA, Integer>{
    UsuarioJPA findByUserName(String userName);
}
