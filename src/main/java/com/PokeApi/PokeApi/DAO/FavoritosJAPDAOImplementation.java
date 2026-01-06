package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.FavoritosJPA;
import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FavoritosJAPDAOImplementation implements IFavoritosJPA {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public Result ADDFavoritos(int idPokemon, int idUsuario, String Nombrepokemon) {
        Result result = new Result();
        try {
            UsuarioJPA usuario = entityManager.find(UsuarioJPA.class, idUsuario);
            if (usuario == null) {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado";
                return result;
            }
            long existe = entityManager.createQuery(
            "SELECT COUNT(f) "
                    + "FROM FavoritodJPA f WHERE f.idPokemon = idPokemon "
                    + "AND f.usuario.idUsuario = :idUsuario",
                    Long.class)
                    .setParameter("idPokemon", idPokemon)
                    .setParameter("idUsuario",idUsuario)
                    .getSingleResult();
            if (existe > 0) {
                result.correct = false;
                result.errorMessage = "El pokemon ya esta en favoritos";
                return result;
            }
            FavoritosJPA favorito = new FavoritosJPA();
            favorito.setIdPokemon(idPokemon);
            favorito.setNombrePokemon(Nombrepokemon);
            favorito.setUsuario(usuario);
            entityManager.persist(favorito);
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }
}
