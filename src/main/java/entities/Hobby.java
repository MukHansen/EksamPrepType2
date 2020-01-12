/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

/**
 *
 * @author Mkhansen
 */
@Entity
@NamedQuery(name = "Hobby.deleteAllRows", query = "DELETE from Hobby")
public class Hobby implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    private String name;
    private String description;
    @ManyToMany(mappedBy = "hobbies")
    private List<Person> persons = new ArrayList<>();

    public Hobby() {
    }

    public Hobby(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Person> getPersons() {
        return persons;
    }

    public void setPerson(Person person) {
        this.persons.add(person);
    }

    @Override
    public String toString() {
        return "Hobby{" + "name=" + name + ", description=" + description + ", persons=" + persons + '}';
    }
    

    
}
