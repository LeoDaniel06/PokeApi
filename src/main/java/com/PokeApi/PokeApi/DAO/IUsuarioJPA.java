
package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;

public interface IUsuarioJPA {
    
    Result Add(UsuarioJPA usuario);
    Result VerificarCuenta(int idUsuario);
    
}
