/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dtos;

/**
 *
 * @author Mkhansen;
 */

public class JokeDTO {
    private String category;
    private String Joke;

    public JokeDTO() {
    }

    public JokeDTO(String category, String Joke) {
        this.category = category;
        this.Joke = Joke;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJoke() {
        return Joke;
    }

    public void setJoke(String Joke) {
        this.Joke = Joke;
    }
    
}
