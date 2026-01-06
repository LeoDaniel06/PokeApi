package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DAO.RolJPADAOImplementation;
import com.PokeApi.PokeApi.DAO.UsuarioDAOJPAImplementation;
import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.RolJPA;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import com.PokeApi.PokeApi.JWT.LoginRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pokedex")
public class PokeApiController {

    @Autowired
    private UsuarioDAOJPAImplementation usuarioDAOJPAImplementation;
    
    @Autowired
    private RolJPADAOImplementation rolJPADAOImplementation;

    @GetMapping
    public String getAll() {
        return "PokemonGetAll";
    }

    @GetMapping("/detail/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("pokemonId", id);
        return "DetailPokeApi";
    }
    
    @GetMapping("/login")
    public String login(Model model){
        model.addAttribute("loginRequest", new LoginRequest());
        return "LoginPokeApi";
    }

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
    public String ResgistrarUsuario(@ModelAttribute UsuarioJPA usuario, @RequestParam("rol") int idRol, RedirectAttributes redirectAttributes) {

        RolJPA rol = new RolJPA();
        rol.setIdRol(idRol);
        usuario.setRolJPA(rol);

        Result result = usuarioDAOJPAImplementation.Add(usuario);
        if (result.correct) {
            redirectAttributes.addFlashAttribute("mensaje", "El Usuario se Registro Correctamente");
            redirectAttributes.addFlashAttribute("tipo", "success");
            return "redirect:/LoginPokeApi";
        } else {
            redirectAttributes.addFlashAttribute("error", result.errorMessage);
            redirectAttributes.addFlashAttribute("tipo", "danger");
            return "redirect:/pokedex/registro";
        }

    }

}
