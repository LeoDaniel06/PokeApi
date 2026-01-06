package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.Result;


public interface IFavoritosJPA {
    Result ADDFavoritos(int idPokemon, int idUsuario, String Nombrepokemon);
}
