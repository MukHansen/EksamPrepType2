/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import facades.FacadeExample;
import facades.PersonFacade;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import utils.EMF_Creator;

/**
 *
 * @author Mkhansen
 */
@Path("person")
public class PersonResource {

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/EksamPrepType1",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final PersonFacade FACADE = PersonFacade.getPersonFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @GET
    @Path("id/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public PersonDTO getPersonByID(@PathParam("id") int id) {
        return FACADE.getPersonById(id);
    }

    @GET
    @Path("phone/{phoneNumber}")
    @Produces(MediaType.APPLICATION_JSON)
    public PersonDTO getPersonByPhone(@PathParam("phoneNumber") String phoneNumber) {
        return FACADE.getPersonByPhone(phoneNumber);
    }

    @GET
    @Path("email/{email}")
    @Produces(MediaType.APPLICATION_JSON)
    public PersonDTO getPersonByEmail(@PathParam("email") String email) {
        return FACADE.getPersonByEmail(email);
    }

    @GET
    @Path("hobby/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public List<PersonDTO> getAllPersonByHobby(@PathParam("name") String name) {
        return FACADE.getAllPersonsByHobby(name);
    }

    @GET
    @Path("allhobbies")
    @Produces(MediaType.APPLICATION_JSON)
    public List<HobbyDTO> getAllHobbies() {
        return FACADE.getAllHobbies();
    }

    @POST
    @Path("createperson")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public PersonDTO createPerson(PersonDTO pDTO) {
        return FACADE.createPerson(pDTO);
    }

    @DELETE
    @Path("/deleteperson/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public PersonDTO deletePerson(@PathParam("id") int id) {
        return FACADE.deletePerson(id);
    }

    @PUT
    @Path("/editperson/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public PersonDTO editPerson(@PathParam("id") int id, PersonDTO pDTO) {
        return FACADE.editPerson(id, pDTO);
    }

    @POST
    @Path("createhobby")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HobbyDTO createHobby(HobbyDTO hDTO) {
        return FACADE.createHobby(hDTO);
    }

    @DELETE
    @Path("/deletehobby/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HobbyDTO deleteHobby(@PathParam("name") String name) {
        return FACADE.removeHobby(name);
    }

    @PUT
    @Path("/edithobby")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public HobbyDTO editHobby(HobbyDTO hDTO) {
        return FACADE.editHobby(hDTO);
    }

    @GET
    @Path("fill")
    @Produces(MediaType.APPLICATION_JSON)
    public String getFilling() {
        return FACADE.fillUp();
    }
}
