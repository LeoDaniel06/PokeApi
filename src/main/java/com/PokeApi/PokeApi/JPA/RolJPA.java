package com.PokeApi.PokeApi.JPA;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ROL")
public class RolJPA {
   
    @Id
    @Column(name = "idrol")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int IdRol;
    
    @Column(name = "nombrerol")
    private String NombreRol;
    
    public int getIdRol() {
        return IdRol;
    }

    public void setIdRol(int IdRol) {
        this.IdRol = IdRol;
    }

    public String getNombreRol() {
        return NombreRol;
    }

    public void setNombreRol(String NombreRol) {
        this.NombreRol = NombreRol;
    }
    
}
