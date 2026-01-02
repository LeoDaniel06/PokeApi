
package com.PokeApi.PokeApi.JWT;


public class LoginRequest {

    private String username;
    private String Password;

    public LoginRequest (){
        
    }
    
    public LoginRequest(String username, String Password){
        this.username = username;
        this.Password = Password;
    }
    
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String Password) {
        this.Password = Password;
    }
    
    
}