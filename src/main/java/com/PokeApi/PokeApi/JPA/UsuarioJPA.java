package com.PokeApi.PokeApi.JPA;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name = "USUARIO")
public class UsuarioJPA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario")
    private int idUsuario;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "username")
    private String userName;

    @Column(name = "password")
    private String password;

    @Column(name = "sexo")
    private String sexo;
    
    @Column(name = "correo")
    private String correo;
    
    @Column(name = "isverified")
    public int isverified;

    @ManyToOne
    @JoinColumn(name = "idrol")
    private RolJPA RolJPA;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoritosJPA> favoritos;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public int getIsverified() {
        return isverified;
    }

    public void setIsverified(int isverified) {
        this.isverified = isverified;
    }
    
    

    public RolJPA getRolJPA() {
        return RolJPA;
    }

    public void setRolJPA(RolJPA RolJPA) {
        this.RolJPA = RolJPA;
    }

    public List<FavoritosJPA> getFavoritos() {
        return favoritos;
    }

    public void setFavoritos(List<FavoritosJPA> favoritos) {
        this.favoritos = favoritos;
    }
}