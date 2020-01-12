/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package facades;

import com.google.gson.Gson;
import dtos.PersonDTO;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import jdk.nashorn.internal.parser.JSONParser;
import net.minidev.json.JSONObject;

/**
 *
 * @author Mkhansen
 */
public class DataFromSwappi {

    public DataFromSwappi() {
    }

    public List<PersonDTO> getPeople() throws InterruptedException, ExecutionException {
        final Integer[] hostID = {1, 2, 3, 4, 5};
        final ExecutorService threadpool = Executors.newCachedThreadPool();
        Queue<Future<String>> futures = new ArrayBlockingQueue(hostID.length);
        List<PersonDTO> listDTO = new ArrayList<PersonDTO>();

        for (int i = 0;
                i < hostID.length;
                i++) {
            final int ID = hostID[i];
            Future<String> future = threadpool.submit(new Callable() {
                @Override
                public String call() throws Exception {
                    return getSwappiData(ID);
                }
            });
            futures.add(future);
        }

        while (!futures.isEmpty()) {
            Future<String> fut = futures.poll();
            if (fut.isDone()) {
                Gson g = new Gson();
                PersonDTO p = g.fromJson(fut.get(), PersonDTO.class);
                listDTO.add(p);
            } else {
                futures.add(fut);
            }

        }

        threadpool.shutdown();

        return listDTO;
    }

    public String getSwappiData(int id) throws MalformedURLException, IOException {
        URL url = new URL("https://swapi.co/api/people/" + id);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Accept", "application/json;charset=UTF-8");
        con.setRequestProperty("User-Agent", "server"); //remember if you are using SWAPI
        Scanner scan = new Scanner(con.getInputStream());
        String jsonStr = null;
        if (scan.hasNext()) {
            jsonStr = scan.nextLine();
        }
        scan.close();
        return jsonStr;
    }

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        DataFromSwappi swap = new DataFromSwappi();
        
        System.out.println(swap.getPeople());
    }

}
