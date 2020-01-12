package facades;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import dtos.JokeDTO;
import dtos.JokesDTO;
import java.net.ProtocolException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import javax.ws.rs.WebApplicationException;

/**
 *
 */
public class ApiFacade {

    private static ApiFacade instance = new ApiFacade();

    //Private Constructor to ensure Singleton
    private ApiFacade() {
    }

    /**
     *
     * @param _emf
     * @return an instance of this facade class.
     */
    public static ApiFacade getApiFacade() {
        if (instance == null) {
            instance = new ApiFacade();
        }
        return instance;
    }

    private String getJokeFromApi(String urlApi) throws MalformedURLException, IOException {
        URL url = new URL(urlApi);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.addRequestProperty("User-Agent", "Mozilla/4.76;Chrome");
        String jsonStr;
        try (Scanner scan = new Scanner(con.getInputStream(), "UTF-8")) {
            jsonStr = null;
            if (scan.hasNext()) {
                jsonStr = scan.nextLine();
            }
        }
        return jsonStr;
    }

    public JokeDTO getJoke(String category) throws IOException, InterruptedException, ExecutionException {

        String joke = instance.getJokeFromApi("https://api.chucknorris.io/jokes/random?category=" + category);
        JokeDTO jDTO = new JokeDTO(category, joke);

        return jDTO;
    }

    public JokesDTO getListOfJokesMax4(ArrayList<String> categories) throws IOException, InterruptedException, ExecutionException {

        List<String> jokes = new ArrayList();
//        List<JokeDTO> jokes = new ArrayList();
//        JokesDTO jDTO = new JokesDTO("api.chucknorris.io");
        if (categories.size() <= 4) {
            for (int i = 0; i < categories.size(); i++) {
                jokes.add("https://api.chucknorris.io/jokes/random?category=" + categories.get(i));
            }
//            for (int i = 0; i < categories.size(); i++) {
//                jokes.add(instance.getJoke(categories.get(i)));
//            }
        } else {
            throw new WebApplicationException("A maximum of 4 categories must be chosen");
        }
//        jDTO.setJokes(jokes);
//        return jDTO;
        return instance.getDIZJoke(jokes);
    }

    public JokesDTO getDIZJoke(List<String> URLS) throws ProtocolException, IOException, InterruptedException, ExecutionException {
        JokesDTO jokesDTO = new JokesDTO("api.chucknorris.io");
        List<JokeDTO> listOfJokes = new ArrayList();
        
        Queue<Future<JsonObject>> queue = new ArrayBlockingQueue(URLS.size());
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
                .create();
        ExecutorService workingJack = Executors.newCachedThreadPool();
        for (String url : URLS) {
            Future<JsonObject> future;
            future = workingJack.submit(() -> {
                JsonObject jsonObject = new JsonParser().parse(getJokeFromApi(url)).getAsJsonObject();
                return jsonObject;
            });
            queue.add(future);
        }
        while (!queue.isEmpty()) {
            Future<JsonObject> cpo = queue.poll();
            if (cpo.isDone()) {
                try {
                    // CHANGE WHEN USING OTHER API
                    // USE OTHER DTO FOR WHAT YOU NEED TO EXTRACT
                    String joke = cpo.get().get("value").getAsString();

                    JsonArray jArray = cpo.get().get("categories").getAsJsonArray();
                    String category = jArray.get(0).getAsString();
                    
                    listOfJokes.add(new JokeDTO(category,joke));

                    System.out.println("jokes tilføjet");
                } catch (NullPointerException ex) {
                    System.out.println("NullPointerException: " + ex);
                }
            } else {
                queue.add(cpo);
            }
        }
        workingJack.shutdown();
        jokesDTO.setJokes(listOfJokes);
        return jokesDTO;
    }

//    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
//        ArrayList<String> list = new ArrayList();
//        list.add("food");
//        list.add("fashion");
//        list.add("history");
//
//        System.out.println(instance.getListOfJokesMax4(list));
//
////            System.out.println(instance.getListOfJokesMax4(list).getJokes());
//    }
}
