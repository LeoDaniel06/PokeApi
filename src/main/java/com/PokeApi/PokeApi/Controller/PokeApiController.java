package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DAO.RolJPADAOImplementation;
import com.PokeApi.PokeApi.DAO.UsuarioDAOJPAImplementation;
import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.RolJPA;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import com.PokeApi.PokeApi.JWT.JwtUtils;
import com.PokeApi.PokeApi.JWT.LoginRequest;
import com.PokeApi.PokeApi.JWT.LoginResponse;
import com.PokeApi.PokeApi.Service.FavoritosService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    AuthenticationManager authenticationManager;
    
    @Autowired
    JwtUtils jwtUtils;
            

    @GetMapping
    public String getAll() {
        return "PokemonGetAll";
    }

    @GetMapping("/detail/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("pokemonId", id);
        return "DetailPokeApi";
    }

    // ---------- FAVORITOS ADD ----------
    @PostMapping("/addFavorito")
    @ResponseBody
    public Result agregarFavorito(@RequestParam int idPokemon,
            @RequestParam String nombrePokemon,
            HttpSession session) {

        UsuarioJPA usuario = (UsuarioJPA) session.getAttribute("usuario");

//        if (usuario == null) {
//            Result result = new Result();
//            result.correct = false;
//            result.errorMessage = "Usuario no autenticado";
//            return result;
//        }
        if (usuario == null) {
            usuario = new UsuarioJPA();
            usuario.setIdUsuario(1); // usuario de prueba en BD
        }

        return favoritosService.addFavorito(
                idPokemon,
                usuario.getIdUsuario(),
                nombrePokemon
        );
    }

    // ---------- FAVORITOS DELETE ----------
    @GetMapping("/deleteFavorito")
    public Result deleteFavorito(@RequestParam int idPokemon, HttpSession session) {
        UsuarioJPA usuario = (UsuarioJPA) session.getAttribute("usuario");
        Result result = new Result();
        if (usuario == null) {
            result.correct = false;
            result.errorMessage = "Usuario no autenticado";
            return result;
        }
//        if (usuario == null) {
//            usuario = new UsuarioJPA();
//            usuario.setIdUsuario(1); // usuario de prueba en BD
//        }

        return favoritosService.eliminarFavoritos(idPokemon, usuario.getIdUsuario());
    }

    // ---------- LOGIN ----------
    @GetMapping("/login")
    public String login(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "LoginPokeApi";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest loginRequest, HttpServletRequest request, Model model) {
                
        try{
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                 loginRequest.getPassword()
                )
        );
        
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        String rol = userDetails.getAuthorities().iterator().next().getAuthority();
        
       if(rol.startsWith("ROLE_")){
           rol = rol.substring(5);
       }
                
        request.getSession().setAttribute("RolUsuario", rol);
        if(rol.equals("ADMIN")){
            return "redirect:/pokedex";
        }else if(rol.equals("USUARIO")){
            return "redirect:/pokedex/detail";
        }else{
            return "redirect:/pokedex/login";
        }
        
        }catch(Exception ex){
            model.addAttribute("Error", "Credenciales Incorrectas");
            return "LoginPokeApi";
        }
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
            @RequestParam("rol") int idRol,
            RedirectAttributes redirectAttributes) {

        RolJPA rol = new RolJPA();
        rol.setIdRol(idRol);
        usuario.setRolJPA(rol);

        Result result = usuarioDAOJPAImplementation.Add(usuario);
        if (result.correct) {
            redirectAttributes.addFlashAttribute("mensaje", "El Usuario se Registro Correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/login";
        } else {
            redirectAttributes.addFlashAttribute("error", result.errorMessage);
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/pokedex/registro";
        }
    }
}
