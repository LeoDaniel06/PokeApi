
package com.PokeApi.PokeApi.Controller;

import com.PokeApi.PokeApi.DTO.PokemonDTO;
import com.PokeApi.PokeApi.Service.PokeService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pokemon")
public class ThreadRestController {

    private final PokeService pokeService;
    
    public ThreadRestController(PokeService pokeService){
        this.pokeService = pokeService;
    }
    
    @GetMapping
    public List<PokemonDTO> obtenerPokemones(){
        return pokeService.obtenerPokemones();
    }
}
