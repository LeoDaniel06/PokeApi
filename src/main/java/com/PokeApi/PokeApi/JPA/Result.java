package com.PokeApi.PokeApi.JPA;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Result {

    public boolean correct;
    public String errorMessage;
    public Object object;
    public List<Object> objects;

    @JsonIgnore
    public Exception ex;

    public int status;
}