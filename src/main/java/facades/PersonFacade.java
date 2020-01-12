/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import dtos.HobbyDTO;
import dtos.PersonDTO;
import entities.Address;
import entities.Hobby;
import entities.Person;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import utils.EMF_Creator;

/**
 *
 * @author Mkhansen
 */
public class PersonFacade {

    private static PersonFacade instance;
    private static EntityManagerFactory emf;

    //Private Constructor to ensure Singleton
    private PersonFacade() {
    }

    public static PersonFacade getPersonFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new PersonFacade();
        }
        return instance;
    }

    public List<PersonDTO> getAllPersons() {
        EntityManager em = emf.createEntityManager();
        List<PersonDTO> listDTO = new ArrayList<>();
        try {
            List<Person> list = em.createQuery("SELECT p FROM Person p").getResultList();
            for (Person person : list) {
                listDTO.add(new PersonDTO(person));
            }
            return listDTO;
        } finally {
            em.close();
        }
    }

    public List<HobbyDTO> getAllHobbies() {
        EntityManager em = emf.createEntityManager();
        List<HobbyDTO> listDTO = new ArrayList<>();
        try {
            List<Hobby> list = em.createQuery("SELECT h FROM Hobby h", Hobby.class).getResultList();
            for (Hobby hobby : list) {
                listDTO.add(new HobbyDTO(hobby));
            }
            return listDTO;
        } finally {
            em.close();
        }
    }

    public PersonDTO getPersonById(int id) {
        EntityManager em = emf.createEntityManager();
        PersonDTO pDTO;
        try {
            TypedQuery<Person> query = em.createQuery(
                    "SELECT p FROM Person p JOIN p.hobbies h JOIN p.address a WHERE p.id = :id", Person.class);
            Person person = query.setParameter("id", id).getSingleResult();
            pDTO = new PersonDTO(person);

            return pDTO;
        } finally {
            em.close();
        }
    }

    public PersonDTO getPersonByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        PersonDTO pDTO;
        try {
            TypedQuery<Person> query = em.createQuery(
                    "SELECT p FROM Person p WHERE p.email = :email", Person.class);
            Person person = query.setParameter("email", email).getSingleResult();
            pDTO = new PersonDTO(person);

            return pDTO;
        } finally {
            em.close();
        }
    }

    public PersonDTO getPersonByPhone(String phonenumber) {
        EntityManager em = emf.createEntityManager();
        PersonDTO pDTO;
        try {
            TypedQuery<Person> query = em.createQuery(
                    "SELECT p FROM Person p WHERE p.phone = :phoneNumber", Person.class);
            Person person = query.setParameter("phoneNumber", phonenumber).getSingleResult();
            pDTO = new PersonDTO(person);

            return pDTO;
        } finally {
            em.close();
        }
    }

    public List<PersonDTO> getAllPersonsByHobby(String name) {
        EntityManager em = emf.createEntityManager();
        List<PersonDTO> listDTO = new ArrayList<>();
        try {
            TypedQuery<Person> query = em.createQuery("SELECT p FROM Person p JOIN p.hobbies h JOIN p.address a WHERE h.name = :hobbyName", Person.class);

            List<Person> list = query.setParameter("hobbyName", name).getResultList();

            for (Person person : list) {
                listDTO.add(new PersonDTO(person));
            }

            return listDTO;
        } finally {
            em.close();
        }
    }

    public PersonDTO deletePerson(int id) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);

        try {
            em.getTransaction().begin();

            long count = (long) em.createQuery("SELECT COUNT(r) FROM Person p JOIN p.address a WHERE a.id = :id").setParameter("id", person.getAddress().getId()).getSingleResult();

            if (count == 1) {
                em.remove(em.find(Address.class, person.getAddress().getId()));
            }

            em.remove(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(person);
    }

    public PersonDTO createPerson(PersonDTO person) {
        EntityManager em = emf.createEntityManager();
        Person p = new Person(person.getfName(), person.getlName(), person.getPhone(), person.getEmail());

        Address address = new Address(person.getAddress().getStreet(), person.getAddress().getCity(), person.getAddress().getZip());
        p.setAddress(address);

        for (HobbyDTO h : person.getHobbies()) {
            Hobby hobby = new Hobby();

            try {
                hobby = em.createQuery("select h from Hobby h where h.name = :name", Hobby.class).setParameter("name", h.getName()).getSingleResult();
            } catch (Exception e) {
                hobby.setName(h.getName());
                hobby.setDescription(h.getDescription());
            }

            p.setHobby(hobby);
        }

        try {
            em.getTransaction().begin();
            em.persist(p);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new PersonDTO(p);
    }

    public PersonDTO editPerson(int id, PersonDTO pDTO) {
        EntityManager em = emf.createEntityManager();
        Person person = em.find(Person.class, id);
        person.setFirstName(pDTO.getfName());
        person.setLastName(pDTO.getfName());
        person.setPhone(pDTO.getPhone());
        person.setEmail(pDTO.getEmail());

        Address address = new Address(pDTO.getAddress().getStreet(), pDTO.getAddress().getCity(), pDTO.getAddress().getZip());
        person.setAddress(address);

        for (HobbyDTO h : pDTO.getHobbies()) {
            Hobby hobby = new Hobby(h.getName(), h.getDescription());
            person.setHobby(hobby);
        }

        try {
            em.getTransaction().begin();
            em.merge(person);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return pDTO;
    }

    public HobbyDTO createHobby(HobbyDTO h) {
        EntityManager em = emf.createEntityManager();
        Hobby hobby = new Hobby(h.getName(), h.getDescription());
        try {
            em.getTransaction().begin();
            em.persist(hobby);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new HobbyDTO(hobby);
    }

    public HobbyDTO removeHobby(String name) {
        EntityManager em = emf.createEntityManager();
        Hobby hobby = em.find(Hobby.class, name);
        try {
            em.getTransaction().begin();
            em.remove(hobby);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return new HobbyDTO(hobby);
    }

    public HobbyDTO editHobby(HobbyDTO h) {
        EntityManager em = emf.createEntityManager();

        Hobby hobby = new Hobby(h.getName(), h.getDescription());
        try {
            em.getTransaction().begin();
            em.merge(hobby);
            em.getTransaction().commit();

        } finally {
            em.close();
        }
        return new HobbyDTO(hobby);
    }

    public String fillUp() {
        EntityManager em = emf.createEntityManager();
        Person p1, p2, p3, p4, p5, p6, p7, p8, p9;
        Hobby hobby1, hobby2, hobby3, hobby4, hobby5, hobby6, hobby7, hobby8, hobby9; 
        Address address1, address2, address3, address4, address5, address6, address7, address8, address9;

        hobby1 = new Hobby("Cykling", "Cykling på hold");
        hobby2 = new Hobby("Fodbold", "Spark til bold");
        hobby3 = new Hobby("Håndbold", "Kast med bold");
        hobby4 = new Hobby("Ski", "Noget med sne");
        hobby5 = new Hobby("Snowboard", "Stå på et bræt");
        hobby6 = new Hobby("Mountainbike", "Cykling i skoven");
        hobby7 = new Hobby("Rollespil", "Noget med papsværd");
        hobby8 = new Hobby("Gocart", "4 hjul og 1 motor");

        address1 = new Address("BalladeStræde", "Balladerup", "2750");
        address2 = new Address("Herlevhovedgade", "Herlev", "1234");
        address3 = new Address("BageStræde", "Albertslun", "2647");
        address4 = new Address("Tivoligade", "København", "4574");
        address5 = new Address("Århusvej", "Århus", "8356");
        address6 = new Address("Herninggade", "Herning", "9764");
        address7 = new Address("Roskildevej", "Roskilde", "9674");
        address8 = new Address("Hvidovrevej", "Hvidovre", "4584");
        address9 = new Address("Skovlundevej", "Skovlunde", "3585");
        
        p1 = new Person("Gurli", "Mogensen", "44556677", "email@email.com");
        p2 = new Person("Gunnar", "Hjorth", "11223344", "mail@email.com");
        p3 = new Person("Peter", "Petersen", "22337755", "1234@email.com");
        p4 = new Person("Pernille", "Pernillesen", "12345678", "todo@email.com");
        p5 = new Person("Karin", "Karinsen", "88774422", "what@eemail.com");
        p6 = new Person("Morten", "Mortensen", "44227755", "ever@email.com");
        p7 = new Person("John", "Johnsen", "99664422", "john@eemail.com");
        p8 = new Person("Jonna", "jonse", "11447788", "yoyo@email.com");
        p9 = new Person("Bjørn", "Jernside", "66449922", "anotherEmail@eemail.com");
        
        p1.setHobby(hobby1);
        p1.setHobby(hobby3);
        p1.setHobby(hobby5);
        
        p2.setHobby(hobby2);
        p2.setHobby(hobby4);
        p2.setHobby(hobby6);
        
        p3.setHobby(hobby3);
        p3.setHobby(hobby5);
        p3.setHobby(hobby7);
        
        p4.setHobby(hobby4);
        p4.setHobby(hobby6);
        p4.setHobby(hobby8);
        
        p5.setHobby(hobby1);
        p5.setHobby(hobby3);
        p5.setHobby(hobby5);
        
        p6.setHobby(hobby2);
        p6.setHobby(hobby4);
        p6.setHobby(hobby6);
        
        p7.setHobby(hobby3);
        p7.setHobby(hobby5);
        p7.setHobby(hobby7);
        
        p8.setHobby(hobby4);
        p8.setHobby(hobby6);
        p8.setHobby(hobby8);

        p9.setHobby(hobby4);
        p9.setHobby(hobby6);
        p9.setHobby(hobby8);
       
        p1.setAddress(address1);
        p2.setAddress(address2);
        p3.setAddress(address3);
        p4.setAddress(address4);
        p5.setAddress(address5);
        p6.setAddress(address6);
        p7.setAddress(address7);
        p8.setAddress(address8);
        p9.setAddress(address9);
        
        try {

            em.getTransaction().begin();
            em.persist(p1);
            em.persist(p2);
            em.persist(p3);
            em.persist(p4);
            em.persist(p5);
            em.persist(p6);
            em.persist(p7);
            em.persist(p8);
            em.persist(p9);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        return "{\"status\":\"filled\"}";
    }
    
//        public static void main(String[] args) {
//        EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
//        PersonFacade pf = PersonFacade.getPersonFacade(emf);
//        pf.fillUp();
////            System.out.println(pf.getPersonByEmail("email@email.com"));
////            System.out.println(pf.getPersonByPhone("11223344"));
//    }
}
