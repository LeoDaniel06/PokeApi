package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DAO.UsuarioDAOJPAImplementation;
import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import com.PokeApi.PokeApi.Service.EmailService;
import com.PokeApi.PokeApi.Service.PokeService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recuperarPass")
public class RecuperarPasswordRestController {

    @Autowired
    private UsuarioDAOJPAImplementation usuarioDAO;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PokeService cacheCodigo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @PostMapping("/enviarCodigo")
    public ResponseEntity<Result> enviarCodigo(@RequestParam("correo") String correo) throws MessagingException {
        Result result = usuarioDAO.GetByEmail(correo);

        System.out.println("Correo recibido: " + correo);
        if (!result.correct || result.object == null) {
            result.correct = false;
            result.errorMessage = "Usuario no encontrado";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        UsuarioJPA usuario = (UsuarioJPA) result.object;

        String codigo = cacheCodigo.generarCodigo(correo);
        emailService.enviarCorreoCodigo(usuario, codigo);

        result.correct = true;
        result.object = "Código enviado correctamente";
        return ResponseEntity.ok(result);
    }


    @PostMapping("/actualizarPass")
    public ResponseEntity<Result> actualizarPassword(
            @RequestParam("correo") String correo,
            @RequestParam("password") String password,
            @RequestParam("codigo") String codigoIngresado) {

        Result result = usuarioDAO.GetByEmail(correo);

        if (!result.correct || result.object == null) {
            result.correct = false;
            result.errorMessage = "Usuario no encontrado";
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(result);
        }

        boolean codigoValido = cacheCodigo.validarCodigo(correo, codigoIngresado);

        if (!codigoValido) {
            result.correct = false;
            result.errorMessage = "Código inválido o expirado";
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        cacheCodigo.eliminarCodigo(correo);

        UsuarioJPA usuario = (UsuarioJPA) result.object;

        String passwordEncry = passwordEncoder.encode(password);
        usuarioDAO.UpdatePassword(usuario.getIdUsuario(), passwordEncry);

        result.correct = true;
        result.object = "Contraseña actualizada correctamente";
        return ResponseEntity.ok(result);
    }
}
