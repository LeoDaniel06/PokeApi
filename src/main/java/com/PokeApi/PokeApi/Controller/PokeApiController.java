package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.JPA.Result;
import com.PokeApi.PokeApi.JPA.UsuarioJPA;
import com.PokeApi.PokeApi.Service.FavoritosService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/pokedex")
public class PokeApiController {

    @Autowired
    private FavoritosService favoritosService;

    @GetMapping
    public String getAll() {
        return "PokemonGetAll";
    }

    @GetMapping("/detail/{id}")
    public String detalle(@PathVariable int id, Model model) {
        model.addAttribute("pokemonId", id);
        return "DetailPokeApi";
    }

    @PostMapping("/addFavorito")
    @ResponseBody
    public Result agregarFavorito(@RequestParam int idPokemon,
            @RequestParam String nombrePokemon,
            HttpSession session){

        UsuarioJPA usuario = (UsuarioJPA) session.getAttribute("usuario");

        if (usuario == null) {
            Result result = new Result();
            result.correct = false;
            result.errorMessage = "Usuario no autenticado";
            return result;
        }

        return favoritosService.addFavorito(
                idPokemon,
                usuario.getIdUsuario(),
                nombrePokemon
        );
    }
}
