
package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;

public interface IUsuarioJPA {
    
    Result GetAllUsuarios();
    Result DeleteUsuario(int idUsuario);
    Result Add(UsuarioJPA usuario);
    Result VerificarCuenta(int idUsuario);
    Result GetById(int idUsuario);
    Result UpdateImagen(int idUsuario, String NuevaImagenB64);
    Result updateUsuario(UsuarioJPA usuarioJPA);
    Result GetByUsername(String username);
    Result GetByCorreo(String correo);
    Result GetByEmail(String correo);
    Result UpdatePassword(int IdUsuario, String password);
}
