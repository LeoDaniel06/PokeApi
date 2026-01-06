package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.RolJPA;
import com.PokeApi.PokeApi.JPA.Result;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RolJPADAOImplementation implements IRolJPA {

    @Autowired
    private EntityManager entityManager;

    @Override
    public Result GetAll() {
        Result result = new Result();
        try {
            TypedQuery<RolJPA> queryRol = entityManager.createQuery(
                    "SELECT r FROM RolJPA r", RolJPA.class);
            List<RolJPA> roles = queryRol.getResultList();

            System.out.println("Roles obtenidos: " + roles.size());

            result.correct = true;
            result.object = roles;
            result.status = 200;

        } catch (Exception ex) {
            System.out.println("Error en GetAll: " + ex.getMessage());
            ex.printStackTrace();
            result.correct = false;
            result.errorMessage = "No se encontró ningún Rol: " + ex.getMessage();
            result.status = 500;
            result.ex = ex;
        }

        return result;
    }
}
