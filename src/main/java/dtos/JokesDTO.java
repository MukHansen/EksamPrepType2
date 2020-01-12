/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Mkhansen;
 */
public class JokesDTO {
    private List<JokeDTO> jokes = new ArrayList();
    private String reference;

    public JokesDTO() {
    }

    public JokesDTO(String reference) {
        this.reference = reference;
    }

    public List<JokeDTO> getJokes() {
        return jokes;
    }

    public void setJokes(List<JokeDTO> jokes) {
        this.jokes = jokes;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
    
}
