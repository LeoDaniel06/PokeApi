package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.JWT.LoginRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pokedex")
public class PokeApiController {

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
}
