package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.DAO.FavoritosJAPDAOImplementation;
import com.PokeApi.PokeApi.JPA.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FavoritosService {
    @Autowired
    private FavoritosJAPDAOImplementation favoritosJPADAOImplementation;
   
    
    public Result addFavorito(int idPokemon, int idUsuario, String nombrePokemon) {
        return favoritosJPADAOImplementation.ADDFavoritos(idPokemon, idUsuario, nombrePokemon);
    }
    
    public Result eliminarFavoritos(int idPokemon, int idUsuario){
        return favoritosJPADAOImplementation.DeleteFavoritos(idPokemon, idUsuario);
    }
    
    public Result getFavoritos(int idUsuario){
        return favoritosJPADAOImplementation.GetFavoritosByUsuario(idUsuario);
    }
}
