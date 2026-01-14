package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.DTO.PokemonDTO;
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

    private List<PokemonDTO> cachePokemon;

    public synchronized List<PokemonDTO> obtenerPokemones() {

        if (cachePokemon != null) {
            return cachePokemon;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://pokeapi.co/api/v2/pokemon?limit=386"))
                    .GET()
                    .build();

            String json = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();

            JsonNode results = mapper.readTree(json).get("results");

            List<PokemonDTO> lista = new ArrayList<>();

            for (JsonNode node : results) {
                lista.add(new PokemonDTO(
                        node.get("name").asText(),
                        node.get("url").asText()
                ));
            }

            cachePokemon = lista;
            return cachePokemon;

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

