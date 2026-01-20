package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DAO.RolJPADAOImplementation;
import com.PokeApi.PokeApi.DAO.UsuarioDAOJPAImplementation;
import com.PokeApi.PokeApi.DTO.PokemonPerfilDTO;
import com.PokeApi.PokeApi.JPA.FavoritosJPA;
import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.RolJPA;
import com.PokeApi.PokeApi.JPA.UsuarioDetails;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import com.PokeApi.PokeApi.JWT.JwtUtils;
import com.PokeApi.PokeApi.JWT.LoginRequest;
import com.PokeApi.PokeApi.Service.EmailService;
import com.PokeApi.PokeApi.Service.FavoritosService;
import com.PokeApi.PokeApi.Service.PokeService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pokedex")
public class PokeApiController {

    @Autowired
    private UsuarioDAOJPAImplementation usuarioDAOJPAImplementation;

    @Autowired
    private RolJPADAOImplementation rolJPADAOImplementation;

    @Autowired
    private FavoritosService favoritosService;

    @Autowired
    private EmailService emailService;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private final PokeService pokeService;

    public PokeApiController(PokeService pokeService) {
        this.pokeService = pokeService;
    }

    @GetMapping
    public String getAll() {
        return "PokemonGetAll";
    }

    // AÑADIDO: GetAllUsuarios
    @GetMapping("/usuarios")
    public String GetAllUsuarios(Model model) {
        Result result = usuarioDAOJPAImplementation.GetAllUsuarios();
        if (result.correct && result.objects != null) {
            List<UsuarioJPA> usuarios = (List<UsuarioJPA>) (List<?>) result.objects;
            model.addAttribute("usuarios", usuarios);
        } else {
            model.addAttribute("error", "No se pudieron obtener los usuarios: " + result.errorMessage);
        }
        return "UsuariosGetAll";
    }

    @GetMapping("/atrapaBallas")
    public String Minijuego1() {
        return "AtrapaBallas";
    }
    
    @GetMapping("/select-pokemon")
    public String minijuegoPeleas() {
        return "select";
    }
    
    @GetMapping("/battle")
    public String pelea() {
        return "battle";
    }
    //----------IR A PERFIL----------//

    @GetMapping("/detail/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("pokemonId", id);
        return "DetailPokeApi";
    }

    // ---------- FAVORITOS ADD ----------
    @PostMapping("/addFavorito")
    @ResponseBody
    public Result agregarFavorito(
            @RequestParam int idPokemon,
            @RequestParam String nombrePokemon,
            Authentication authentication
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            Result result = new Result();
            result.correct = false;
            result.errorMessage = "Debes iniciar sesion";
            return result;
        }

        UsuarioDetails userDetails
                = (UsuarioDetails) authentication.getPrincipal();

        int idUsuario = userDetails.getId();

        return favoritosService.addFavorito(
                idPokemon,
                idUsuario,
                nombrePokemon
        );
    }

    // ---------- FAVORITOS DELETE ----------
    @PostMapping("/deleteFavorito")
    @ResponseBody
    public Result deleteFavorito(
            @RequestParam int idPokemon,
            Authentication authentication
    ) {

        if (authentication == null || !authentication.isAuthenticated()) {
            Result result = new Result();
            result.correct = false;
            result.errorMessage = "Debes iniciar sesión";
            return result;
        }

        UsuarioDetails userDetails
                = (UsuarioDetails) authentication.getPrincipal();

        int idUsuario = userDetails.getId();

        return favoritosService.eliminarFavoritos(idPokemon, idUsuario);
    }

    // ---------- LOGIN ----------
    @GetMapping("/login")
    public String login(
            @RequestParam(required = false) String error,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String mensaje,
            @RequestParam(required = false) String emailUsuario,
            Model model) {

        model.addAttribute("loginRequest", new LoginRequest());

        if (error != null && !error.isEmpty()) {
            model.addAttribute("error", error);
        }

        if (tipo != null && !tipo.isEmpty()) {
            model.addAttribute("tipo", tipo);
        }

        if (mensaje != null && !mensaje.isEmpty()) {
            model.addAttribute("mensaje", mensaje);
        }

        if (emailUsuario != null && !emailUsuario.isEmpty()) {
            model.addAttribute("emailUsuario", emailUsuario);
        }

        return "LoginPokeApi";
    }

    @PostMapping("/login")
    public String login(
            @ModelAttribute LoginRequest loginRequest,
            HttpServletResponse response,
            RedirectAttributes redirectAttributes,
            Model model) {

        try {
            Result resultUsuario = usuarioDAOJPAImplementation.GetByUsername(loginRequest.getUsername());

            if (!resultUsuario.correct) {
                System.out.println("Usuario NO encontrado");
                model.addAttribute("error", "Usuario no encontrado");
                model.addAttribute("tipo", "danger");
                model.addAttribute("loginRequest", loginRequest);
                return "LoginPokeApi";
            }

            UsuarioJPA usuario = (UsuarioJPA) resultUsuario.object;

            if (usuario.getIsverified() == 0) {
                model.addAttribute("error", "Tu cuenta NO está verificada. Revisa tu correo: " + usuario.getCorreo());
                model.addAttribute("tipo", "warning");
                model.addAttribute("emailUsuario", usuario.getCorreo());
                model.addAttribute("loginRequest", loginRequest);
                return "LoginPokeApi";
            }

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UsuarioDetails usuarioDetails = (UsuarioDetails) authentication.getPrincipal();

            String jwtToken = jwtUtils.generateToken(usuarioDetails);

            Cookie jwtCookie = new Cookie("JWT_TOKEN", jwtToken);
            jwtCookie.setHttpOnly(true);
            jwtCookie.setPath("/");
            jwtCookie.setMaxAge((int) (jwtUtils.getExpiration() / 1000));
            response.addCookie(jwtCookie);

            String rol = usuarioDetails.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

            if (rol.startsWith("ROLE_")) {
                rol = rol.substring(5);
            }

            System.out.println("Login exitoso, rol: " + rol);

            if (rol.equals("ADMIN") || rol.equals("ENTRENADOR")) {
                return "redirect:/pokedex";
            } else {
                return "redirect:/pokedex/login";
            }

        } catch (Exception ex) {
            System.out.println("ERROR en autenticación: " + ex.getMessage());
            ex.printStackTrace();
            model.addAttribute("error", "Credenciales Incorrectas");
            model.addAttribute("tipo", "danger");
            model.addAttribute("loginRequest", loginRequest);
            return "LoginPokeApi";
        }
    }

    // AÑADIDO: Reenviar verificación
    @PostMapping("/reenviarVerificacion")
    @ResponseBody
    public Result reenviarVerificacion(@RequestParam String correo) {
        Result result = new Result();

        try {
            Result usuarioResult = usuarioDAOJPAImplementation.GetByCorreo(correo);

            if (!usuarioResult.correct) {
                result.correct = false;
                result.errorMessage = "No se encontró ninguna cuenta con ese correo electrónico";
                System.out.println("Usuario NO encontrado");
                return result;
            }

            UsuarioJPA usuario = (UsuarioJPA) usuarioResult.object;
            System.out.println("Usuario encontrado: " + usuario.getUserName());
            System.out.println("IsVerified: " + usuario.getIsverified());

            if (usuario.getIsverified() == 1) {
                result.correct = false;
                result.errorMessage = "Esta cuenta ya está verificada";
                System.out.println("Cuenta ya verificada");
                return result;
            }

            emailService.enviarCorreoVerificacion(usuario);

            result.correct = true;
            result.errorMessage = "Correo de verificación enviado exitosamente a " + usuario.getCorreo();

        } catch (Exception e) {
            result.correct = false;
            result.errorMessage = "Error al enviar el correo: " + e.getMessage();
        }

        return result;
    }

    // ---------- REGISTRO ----------
    @GetMapping("/registro")
    public String ResgistroFormulario(Model model) {
        model.addAttribute("usuario", new UsuarioJPA());
        Result roles = rolJPADAOImplementation.GetAll();
        if (roles.correct && roles.object != null) {
            model.addAttribute("roles", roles.object);
        }
        return "RegistrarUsuarioPokeApi";
    }

    @PostMapping("/registro")
    public String ResgistrarUsuario(@ModelAttribute UsuarioJPA usuario,
            RedirectAttributes redirectAttributes) {

        Result result = usuarioDAOJPAImplementation.Add(usuario);
        if (result.correct) {
            try {
                emailService.enviarCorreoVerificacion((UsuarioJPA) result.object);

                redirectAttributes.addFlashAttribute("mensaje",
                        "¡Registro exitoso! Te hemos enviado un correo de verificación a " + usuario.getCorreo());
                redirectAttributes.addFlashAttribute("tipo", "success");

            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje",
                        "Usuario registrado pero hubo un error al enviar el correo.");
                redirectAttributes.addFlashAttribute("tipo", "warning");
            }

            return "redirect:/pokedex/login";
        } else {
            redirectAttributes.addFlashAttribute("error", result.errorMessage);
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/pokedex/registro";
        }
    }

    // ---------- VERIFICACION CUENTA/CORREO ----------
    @GetMapping("/verificar")
    public String VerificarCuenta(@RequestParam String token, Model model) {
        try {
            if (!jwtUtils.validateVerificationToken(token)) {
                model.addAttribute("error", "La verificacion expiro");
                return "VerificacionPokeApi";

            }

            Long idUsuario = jwtUtils.extractUserId(token);

            Result result = usuarioDAOJPAImplementation.VerificarCuenta(idUsuario.intValue());

            if (result.correct) {
                model.addAttribute("mensaje", "¡Cuenta verificada exitosamente! Ya puedes iniciar sesión");
                model.addAttribute("tipo", "success");
            } else {
                model.addAttribute("error", result.errorMessage);
                model.addAttribute("tipo", "danger");
            }

        } catch (Exception ex) {
            model.addAttribute("error", "Error al verificar la cuenta" + ex.getLocalizedMessage());
            model.addAttribute("tipo", "danger");
        }

        return "VerificacionPokeApi";
    }

    //----------GET FAVORITOS----------//
    @GetMapping("/favoritos")
    @ResponseBody
    public List<Integer> obtenerFavoritos(Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return List.of();
        }

        UsuarioDetails userDetails
                = (UsuarioDetails) authentication.getPrincipal();

        int idUsuario = userDetails.getId();

        Result result = favoritosService.getFavoritos(idUsuario);

        if (!result.correct || result.objects == null) {
            return List.of();
        }

        return result.objects.stream()
                .map(o -> (Integer) o)
                .toList();
    }

    //----------DetailUsuario----------//
    @GetMapping("/perfilUsuario/{idUsuario}")
    public String UsuarioByID(@PathVariable int idUsuario,
            Model model,
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/pokedex/login";
        }

        UsuarioDetails userDetails
                = (UsuarioDetails) authentication.getPrincipal();

        int idSesion = userDetails.getId();

        String rol = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        if (rol.startsWith("ROLE_")) {
            rol = rol.substring(5);
        }

        if (!rol.equals("ADMIN") && idSesion != idUsuario) {
            return "redirect:/pokedex/acceso-denegado";
        }

        Result result = usuarioDAOJPAImplementation.GetById(idUsuario);

        if (!result.correct) {
            model.addAttribute("error", result.errorMessage);
            return "error";
        }
        
        Result resultFaV = favoritosService.getFavoritos(idUsuario);
        
        List<PokemonPerfilDTO> equipo = new ArrayList<>();
        if(resultFaV.correct && resultFaV.objects != null){
            equipo = resultFaV.objects.stream()
                    .limit(6)
                    .map(o ->(Integer) o)
                    .map(idPokemon -> pokeService.obtenerPokemonPorId((int) idPokemon))
                    .toList();
        }

        model.addAttribute("usuario", (UsuarioJPA) result.object);
        model.addAttribute("equipo", equipo);
        model.addAttribute("esAdmin", rol.equals("ADMIN"));

        return "UsuarioDetails";
    }

    //----------UPDATE IMAGEN----------//
    @PostMapping("/update-imagen")
    public String updateImagen(@RequestParam("idUsuario") int idUsuario,
            @RequestParam("imagen") MultipartFile imagen,
            RedirectAttributes redirectAttributes) {
        try {
            if (imagen.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No se selecciono una imagen");
                return "redirect:/pokedex/perfilUsuario/" + idUsuario;
            }
            byte[] bytes = imagen.getBytes();
            String imagenBase64 = Base64.getEncoder().encodeToString(bytes);
            Result result = usuarioDAOJPAImplementation.UpdateImagen(idUsuario, imagenBase64);
            if (result.correct) {
                redirectAttributes.addFlashAttribute("error", result.errorMessage);
            } else {
                redirectAttributes.addFlashAttribute("success", "Imagen Actualizada");
            }
        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar la imagen");
        }
        return "redirect:/pokedex/perfilUsuario/" + idUsuario;
    }

    //----------UPDATE DATOS USUARIO----------//
    @PostMapping("/update-datos")
    public String UpdateDatosUsuario(@ModelAttribute UsuarioJPA usuario,
            RedirectAttributes redirectAttributes,
            Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/pokedex/login";
        }
        UsuarioDetails userDetails = (UsuarioDetails) authentication.getPrincipal();
        if (userDetails.getId() != usuario.getIdUsuario()) {
            return "redirect:/pokedex/login";
        }
        Result result = usuarioDAOJPAImplementation.updateUsuario(usuario);
        if (result.correct) {
            redirectAttributes.addFlashAttribute("Succes", "Datos Actualizados correctamente");
        } else {
            redirectAttributes.addFlashAttribute("error", result.errorMessage);
        }
        return "redirect:/pokedex/perfilUsuario/" + usuario.getIdUsuario();
    }

    // AÑADIDO: DELETE USUARIO
    @PostMapping("/delete")
    public String deleteUsuario(@RequestParam int idUsuario,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/pokedex/login";
        }

        UsuarioDetails userDetails
                = (UsuarioDetails) authentication.getPrincipal();

        String rol = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        if (rol.startsWith("ROLE_")) {
            rol = rol.substring(5);
        }

        if (!rol.equals("ADMIN")) {
            return "redirect:/pokedex/acceso-denegado";
        }

        if (userDetails.getId() == idUsuario) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "No puedes eliminar tu propia cuenta"
            );
            return "redirect:/pokedex/usuarios";
        }

        Result result = usuarioDAOJPAImplementation.DeleteUsuario(idUsuario);

        if (result.correct) {
            redirectAttributes.addFlashAttribute(
                    "success",
                    "Usuario eliminado correctamente"
            );
        } else {
            redirectAttributes.addFlashAttribute(
                    "error",
                    result.errorMessage
            );
        }

        return "redirect:/pokedex/usuarios";
    }

    // AÑADIDO: Recuperar Contraseña
    @GetMapping("/recuperarPass")
    public String RecuperarPassword() {
        return "FormPassword";
    }
}
