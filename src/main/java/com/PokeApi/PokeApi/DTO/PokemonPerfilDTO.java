
package com.PokeApi.PokeApi.DTO;

import java.util.List;


public class PokemonPerfilDTO {

    private int idPokemon;
    private String nombre;
    private String imagen;
    private int Nivel;
    private List<String> Movimientos;
    private List<String> tipos;
    
    public PokemonPerfilDTO(){
}

    public PokemonPerfilDTO(int idPokemon, String nombre, String imagen, int Nivel,List<String> Movimientos, List<String> tipos) {
        this.idPokemon = idPokemon;
        this.nombre = nombre;
        this.imagen = imagen;
        this.Nivel = Nivel;
        this.Movimientos = Movimientos;
        this.tipos = tipos;
    }

    public int getIdPokemon() {
        return idPokemon;
    }

    public void setIdPokemon(int idPokemon) {
        this.idPokemon = idPokemon;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public List<String> getMovimientos() {
        return Movimientos;
    }

    public void setMovimientos(List<String> Movimientos) {
        this.Movimientos = Movimientos;
    }

    public List<String> getTipos() {
        return tipos;
    }

    public void setTipos(List<String> tipos) {
        this.tipos = tipos;
    }

    public int getNivel() {
        return Nivel;
    }

    public void setNivel(int Nivel) {
        this.Nivel = Nivel;
    }
    
    
}
