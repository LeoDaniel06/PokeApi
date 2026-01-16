package com.PokeApi.PokeApi.Service;

import com.PokeApi.PokeApi.DTO.PokemonDTO;
import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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

    public synchronized List<PokemonDTO> obtenerPokemones() {

        if (!cachePokemon.isEmpty()) {
            return cachePokemon;
        }

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://pokeapi.co/api/v2/pokemon?limit=1375"))
                    .GET()
                    .build();

            String json = httpClient
                    .send(request, HttpResponse.BodyHandlers.ofString())
                    .body();

            JsonNode results = mapper.readTree(json).get("results");

            for (JsonNode node : results) {
                cachePokemon.add(new PokemonDTO(
                        node.get("name").asText(),
                        node.get("url").asText()
                ));
            }

            return cachePokemon;

        } catch (Exception e) {
            throw new RuntimeException("Error cargando Pokémon", e);
        }
    }

    @Cacheable(value = "pokemon", key = "#id")
    public String getPokemon(int id) {
        return restTemplate.getForObject("http://pokeapi.co/v2/pokemon/" + id, String.class);
    }

    @Cacheable(value = "pokemonSpecies", key = "#id")
    public String getPokemonSpecies(int id) {
        return restTemplate.getForObject(
                "https://pokeapi.co/api/v2/pokemon-species/" + id,
                String.class
        );
    }

    private static class CodigoCache {

        String codigo;
        long expiracion;

        CodigoCache(String codigo, long expiracion) {
            this.codigo = codigo;
            this.expiracion = expiracion;
        }
    }

    private final Map<String, CodigoCache> cache = new ConcurrentHashMap<>();

    private static final long TIEMPO_EXPIRACION = 10 * 60 * 1000;

    public String generarCodigo(String correo) {
        String codigo = String.valueOf((int) (100000 + Math.random() * 900000));
        long expiracion = System.currentTimeMillis() + TIEMPO_EXPIRACION;

        cache.put(correo, new CodigoCache(codigo, expiracion));
        return codigo;
    }

    public boolean validarCodigo(String correo, String codigoIngresado) {
        CodigoCache data = cache.get(correo);
        if (data == null) {
            return false;
        }
        if (System.currentTimeMillis() > data.expiracion) {
            cache.remove(correo);
            return false;
        }
        return data.codigo.equals(codigoIngresado);
    }

    public void eliminarCodigo(String correo) {
        cache.remove(correo);
    }

}
