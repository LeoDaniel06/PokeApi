
package com.PokeApi.PokeApi.JWT;

import java.util.Date;


public class LoginResponse {

    private final String token;
    private final Date ExpiresAt;
    private final Date CreateAt;
    private String rol;
   
    
    public LoginResponse(String token, Date ExpiresAt, Date CreateAt, String rol){
        this.token = token;
        this.ExpiresAt = ExpiresAt;
        this.CreateAt = CreateAt;
        this.rol = rol;
    }
            
    public String getToken(){
        return token;
    }
    
    public Date getCreateAt(){
        return CreateAt;
    }
    
    public Date getExpiresAt(){
        return ExpiresAt;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}
