package com.PokeApi.PokeApi.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
    @Column(name = "idPokemon")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int IdPokemon;

    @ManyToOne
    @JoinColumn(name = "idUsuario")
    private UsuarioJPA UsuarioJPA;

    @Column(name = "nombrePokemon")
    private String NombrePokemon;

    public int getIdPokemon() {
        return IdPokemon;
    }

    public void setIdPokemon(int IdPokemon) {
        this.IdPokemon = IdPokemon;
    }

    public UsuarioJPA getUsuarioJPA() {
        return UsuarioJPA;
    }

    public void setUsuarioJPA(UsuarioJPA UsuarioJPA) {
        this.UsuarioJPA = UsuarioJPA;
    }

    public String getNombrePokemon() {
        return NombrePokemon;
    }

    public void setNombrePokemon(String NombrePokemon) {
        this.NombrePokemon = NombrePokemon;
    }

}

