package com.PokeApi.PokeApi.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "FAVORITOS")
public class FavoritosJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name= "idfavorito")
    private int idFavorito;
    
    @Column(name = "idpokemon")
    private int idPokemon;

    @Column(name = "nombrepokemon")
    private String nombrePokemon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusuario")
    private UsuarioJPA usuario;

    public int getIdFavorito() {
        return idFavorito;
    }

    public void setIdFavorito(int idFavorito) {
        this.idFavorito = idFavorito;
    }
    
    public int getIdPokemon() {
        return idPokemon;
    }

    public void setIdPokemon(int idPokemon) {
        this.idPokemon = idPokemon;
    }

    public String getNombrePokemon() {
        return nombrePokemon;
    }

    public void setNombrePokemon(String nombrePokemon) {
        this.nombrePokemon = nombrePokemon;
    }

    public UsuarioJPA getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioJPA usuario) {
        this.usuario = usuario;
    }
}
