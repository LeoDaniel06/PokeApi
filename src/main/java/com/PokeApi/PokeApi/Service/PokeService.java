package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.DTO.PokemonDTO;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Service
public class PokeService {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final List<PokemonDTO> cachePokemon = new ArrayList<>();
    private final RestTemplate restTemplate;
    
    public PokeService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void cargarPokemon() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://pokeapi.co/api/v2/pokemon?limit=1350"))
                    .GET()
                    .build();

            String json = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();

            JsonNode root = mapper.readTree(json);
            JsonNode results = root.get("results");

            cachePokemon.clear();

            for (JsonNode node : results) {
                cachePokemon.add(
                        new PokemonDTO(
                                node.get("name").asText(),
                                node.get("url").asText()
                        )
                );
            }

            System.out.println("Pokémon cargados: " + cachePokemon.size());

        } catch (Exception e) {
            throw new RuntimeException("Error cargando Pokémon", e);
        }
    }

    public List<PokemonDTO> obtenerPokemones() {
        return cachePokemon;
    }
    
    @Cacheable(value = "pokemon", key = "#id")
    public String getPokemon(int id){
        return restTemplate.getForObject("http://pokeapi.co/v2/pokemon/"+id,String.class);
    }
    
    @Cacheable(value = "pokemonSpecies", key = "#id")
    public String getPokemonSpecies(int id) {
        return restTemplate.getForObject(
            "https://pokeapi.co/api/v2/pokemon-species/" + id,
            String.class
        );
    }
}


