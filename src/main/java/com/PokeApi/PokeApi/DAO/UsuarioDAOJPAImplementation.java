package com.PokeApi.PokeApi.DAO;

import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.RolJPA;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import org.hibernate.Hibernate;
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
    public Result GetAllUsuarios() {
        Result result = new Result();
        try {
            TypedQuery<UsuarioJPA> queryUsuario
                    = entityManager.createQuery("FROM UsuarioJPA", UsuarioJPA.class);
            List<UsuarioJPA> usuarios = queryUsuario.getResultList();
            result.objects = (List<Object>) (List<?>) usuarios;
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }

        return result;
    }

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

            usuario.setIsverified(0);
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

    @Transactional
    public Result VerificarCuenta(int idUsuario) {
        Result result = new Result();
        try {
            UsuarioJPA usuario = entityManager.find(UsuarioJPA.class, idUsuario);

            if (usuario == null) {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado";
                result.status = 404;
                return result;
            }

            if (usuario.getIsverified() == 1) {
                result.correct = false;
                result.errorMessage = "El usuario ya está verificado";
                result.status = 400;
                return result;
            }

            usuario.setIsverified(1);
            entityManager.merge(usuario);

            result.correct = true;
            result.status = 200;
            return result;

        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = "Error al verificar" + ex.getLocalizedMessage();
            result.ex = ex;
            result.status = 500;
        }

        return result;
    }

    @Override
    @Transactional
    public Result GetById(int idUsuario) {
        Result result = new Result();
        try {
            UsuarioJPA usuarioJPA = entityManager.find(UsuarioJPA.class, idUsuario);
            if (usuarioJPA != null) {
                Hibernate.initialize(usuarioJPA.getRolJPA());
                result.object = usuarioJPA;
                result.correct = true;
            } else {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado";
            }
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
//----------UPDATE IMAGEN----------//

    @Override
    @Transactional
    public Result UpdateImagen(int idUsuario, String NuevaImagenB64) {
        Result result = new Result();
        try {
            UsuarioJPA usuarioJPA = entityManager.find(UsuarioJPA.class, idUsuario);
            if (usuarioJPA != null) {
                usuarioJPA.setImagen(NuevaImagenB64);
                result.correct = true;
            } else {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado";
            }
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }
//----------UPDATE DATOS----------//

    @Override
    @Transactional
    public Result updateUsuario(UsuarioJPA usuarioJPA) {
        Result result = new Result();
        try {
            UsuarioJPA usuarioBase = entityManager.find(UsuarioJPA.class, usuarioJPA.getIdUsuario());
            if (usuarioBase == null) {
                result.correct = false;
                result.errorMessage = "Usuario no encontrado";
                return result;
            }
            usuarioBase.setUserName(usuarioJPA.getUserName());
            usuarioBase.setNombre(usuarioJPA.getNombre());
            usuarioBase.setApellidoPaterno(usuarioJPA.getApellidoPaterno());
            usuarioBase.setApellidoMaterno(usuarioJPA.getApellidoMaterno());
            usuarioBase.setSexo(usuarioJPA.getSexo());
            usuarioBase.setCorreo(usuarioJPA.getCorreo());
            entityManager.merge(usuarioBase);
            entityManager.flush();
            result.correct = true;
        } catch (Exception ex) {
            result.correct = false;
            result.errorMessage = ex.getLocalizedMessage();
            result.ex = ex;
        }
        return result;
    }

    @Override
@Transactional
public Result GetByEmail(String correo) {
    Result result = new Result();

    try {
        UsuarioJPA usuario = entityManager
                .createQuery(
                        "SELECT u FROM UsuarioJPA u WHERE u.correo = :correo",
                        UsuarioJPA.class
                )
                .setParameter("correo", correo)
                .getSingleResult();

        result.correct = true;
        result.object = usuario;

    } catch (Exception ex) {
        result.correct = false;
        result.errorMessage = ex.getLocalizedMessage();
        result.ex = ex;
    }

    return result;
}


@Override
@Transactional
public Result UpdatePassword(int idUsuario, String password) {
    Result result = new Result();

    try {
        entityManager.createNativeQuery(
                "UPDATE USUARIO SET password = :password WHERE idusuario = :idUsuario")
                .setParameter("password", password)
                .setParameter("idUsuario", idUsuario)
                .executeUpdate();

        result.correct = true;
        result.status = 202;

    } catch (Exception ex) {
        result.correct = false;
        result.errorMessage = ex.getLocalizedMessage();
        result.ex = ex;
    }

    return result;
}


// ---------- DELETE USUARIO ----------
@Override
@Transactional
public Result DeleteUsuario(int idUsuario) {
    Result result = new Result();

    try {
        UsuarioJPA usuario = entityManager.find(UsuarioJPA.class, idUsuario);

        if (usuario == null) {
            result.correct = false;
            result.errorMessage = "El usuario no existe";
            return result;
        }

        entityManager.remove(usuario);
        result.correct = true;

    } catch (Exception ex) {
        result.correct = false;
        result.errorMessage = ex.getLocalizedMessage();
        result.ex = ex;
    }

    return result;
}
}
