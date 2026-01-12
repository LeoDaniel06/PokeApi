package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.FavoritosJPA;
import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class FavoritosJAPDAOImplementation implements IFavoritosJPA {

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public Result ADDFavoritos(int idPokemon, int idUsuario, String nombrepokemon) {
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
                    + "FROM FavoritosJPA f "
                    + "WHERE f.idPokemon = :idPokemon "
                    + "AND f.usuario.idUsuario = :idUsuario",
                    Long.class
            )
                    .setParameter("idPokemon", idPokemon)
                    .setParameter("idUsuario", idUsuario)
                    .getSingleResult();
            if (existe > 0) {
                result.correct = false;
                result.errorMessage = "El pokemon ya esta en favoritos";
                return result;
            }
            FavoritosJPA favorito = new FavoritosJPA();
            favorito.setIdPokemon(idPokemon);
            favorito.setNombrePokemon(nombrepokemon);
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
//-----------------------------------DELETE FAVORITO-----------------------------------------------

    @Override
    @Transactional
    public Result DeleteFavoritos(int idPokemon, int idUsuario) {
        Result result = new Result();
        try {
            int rows = entityManager.createQuery(
                    "DELETE FROM FavoritosJPA f"
                    + "WHERE f.idPokemon = :idPokemon"
                    + "AND f.usuario.idUsuario = :idUsuario")
                    .setParameter("idPokemon", idPokemon)
                    .setParameter("idUsuario", idUsuario)
                    .executeUpdate();
            result.correct = rows > 0;
            if (result.correct) {
                result.errorMessage = "No se encontro el Pokemon favorito a eliminar";
            }
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
//----------------------------------------------GET FAVORITOS------------------------------------------

    @Override
    @Transactional
    public Result GetFavoritosByUsuario(int idUsuario) {
        Result result = new Result();
        try {
            List<Integer> favoritos = entityManager.createQuery(""
                    + "SELECT f.idPokemon "
                    + "FROM FavoritosJPA f "
                    + "WHERE f.usuario.idUsuario = :idUsuario",
                    Integer.class)
                    .setParameter("idUsuario", idUsuario)
                    .getResultList();
            result.objects = new ArrayList<>();
            for (Integer id : favoritos) {
                result.objects.add(id);
            }
            result.correct = true;
        } catch (Exception ex) {
            result.correct = true;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
}
