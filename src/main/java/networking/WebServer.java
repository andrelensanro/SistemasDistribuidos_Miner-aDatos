/*
 *  MIT License
 *
 *  Copyright (c) 2019 Michael Pogrebinsky - Distributed Systems & Cloud Computing with Java
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package networking;


import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import model.frontend.FrontendSearchRequest;
import model.frontend.FrontendSearchResponse;
import java.math.BigInteger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.StringTokenizer;
import java.lang.Integer;
import java.util.concurrent.CompletableFuture;
import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.lang.String;
import java.util.Arrays.*;
//import networking.WebClient;


public class WebServer {
    private static final String STATUS_ENDPOINT = "/status";
    private static final String HOME_PAGE_ENDPOINT = "/";
    private static final String HOME_PAGE_UI_ASSETS_BASE_DIR = "/ui_assets/";
    private static final String ENDPOINT_PROCESS = "/procesar_datos";
    private static final String TASK_PRODUCT = "/prod";
    private static final String RESP_PRODUCT = "/resp";

    private final int port;
    private HttpServer server;
    private final ObjectMapper objectMapper;
    private List<Map<String, Integer>> libros;
    private List<String> titulos;
    private boolean [] disponible;

    public WebServer(int port) {
        this.port = port;
        this.objectMapper = new ObjectMapper();
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        this.disponible = new boolean[3];
        Arrays.fill(this.disponible, Boolean.TRUE);
    }

    

    public void startServer() throws IOException {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(ENDPOINT_PROCESS);

        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);

        // handle requests for resources
        HttpContext homePageContext = server.createContext(HOME_PAGE_ENDPOINT);
        homePageContext.setHandler(this::handleRequestForAsset);

        HttpContext prodContext = server.createContext(TASK_PRODUCT);
        prodContext.setHandler(this::handleRequestProduct);

        HttpContext respContext = server.createContext(RESP_PRODUCT);
        respContext.setHandler(this::handleRequestRespuesta);

        server.setExecutor(Executors.newFixedThreadPool(8));

        procesarLibros();

        

        System.out.println("Libros que hay en la lista de libros: " + libros.size());
        server.start();

    }

    private void procesarLibros() throws IOException {

        File folder = new File("/home/androb/Documents/Clase39/LIBROS_TXT");
        

        this.libros = new ArrayList<Map<String, Integer>>();
        this.titulos = new ArrayList<String>();

        for (File file : folder.listFiles()) {

            //System.out.println(file.getName());
            
            String libro = file.getName();
            titulos.add(libro);
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("/home/androb/Documents/Clase39/LIBROS_TXT/"+libro), "utf-8"));
            Scanner obj = new Scanner(in);
            Map<String, Integer> map = new HashMap<String, Integer>();

            // String WORD = new String(requestBytes, StandardCharsets.UTF_8);

            while (obj.hasNextLine()){

                String s = obj.nextLine();
                //System.out.println(s);
                s = s.trim();
                String[] list = s.split("\\ ");
                int i=0;
                for(String c : list){

                    // c = c.toLowerCase();

                    
                    if(c.contains(","))
                        c = c.replace(',', (char)32);
                    if(c.contains("."))
                        c = c.replace('.', (char)32);
                    if(c.contains(":"))
                        c = c.replace(':', (char)32);
                    if(c.contains("-"))
                        c = c.replace('-', (char)32);
                    if(c.contains("!"))
                        c = c.replace('!', (char)32);
                    if(c.contains("¡"))
                        c = c.replace('¡', (char)32);
                    if(c.contains("?"))
                        c = c.replace('?', (char)32);
                    if(c.contains("¿"))
                        c = c.replace('¿', (char)32);
                    if(c.contains("_"))
                        c = c.replace('_', (char)32);
                    if(c.contains("*"))
                        c = c.replace('*', (char)32);
                    if(c.contains("+"))
                        c = c.replace('+', (char)32);
                    if(c.contains("\\´"))
                        c = c.replace('´', (char)32);
                    if(c.contains("("))
                        c = c.replace('(', (char)32);
                    if(c.contains(")"))
                        c = c.replace(')', (char)32);
                    if(c.contains("{"))
                        c = c.replace('{', (char)32);
                    if(c.contains("}"))
                        c = c.replace('}', (char)32);
                    if(c.contains("["))
                        c = c.replace('[', (char)32);
                    if(c.contains("]"))
                        c = c.replace(']', (char)32);
                    if(c.contains("»"))
                        c = c.replace('»', (char)32);
                    if(c.contains("«"))
                        c = c.replace('«', (char)32);
                    if(c.contains("─"))
                        c = c.replace('─', (char)32);

                    c = c.trim();
                    
                    String [] l = c.split("\\ ");
                    for(String cc : l){

                        cc = cc.trim();

                        if(map.containsKey(cc)){
                            map.replace(cc, map.get(cc)+1);
                        }else{
                            map.put(cc,1);
                        }
                    }

                    if(map.containsKey(c)){
                        map.replace(c, map.get(c)+1);
                    }else{
                        map.put(c,1);
                    }
                }
            }

            libros.add(map);
        }
    }

    private void handleRequestForAsset(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        byte[] response;

        String asset = exchange.getRequestURI().getPath();

        if (asset.equals(HOME_PAGE_ENDPOINT)) {
            response = readUiAsset(HOME_PAGE_UI_ASSETS_BASE_DIR + "index.html");
        } else {
            response = readUiAsset(asset);
        }
        addContentType(asset, exchange);

        sendResponse(response, exchange);
    }



    //      localhost:9000/prod
    private void handleRequestProduct(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String asset = exchange.getRequestURI().toString();
        System.out.println("ASSET es: " + asset);
        WebClient client =  new WebClient();
        WebClient client2 =  new WebClient();
        WebClient client3 =  new WebClient();


        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        completableFuture.complete("Completado!");
        CompletableFuture<String> completableFuture2 = new CompletableFuture<>();
        completableFuture2.complete("Completado!");
        CompletableFuture<String> completableFuture3 = new CompletableFuture<>();
        completableFuture3.complete("Completado!");

        
        String s = "Nada";
        String s2 = "Nada";
        String s3 = "Nada";
         
        
        
        if(!disponible[0]){
            if(!disponible[1]){
                if(disponible[2]){
                    disponible[2] = false;
                    String url3 = "http://localhost:9003/resp" + asset;
                    completableFuture3 = client3.sendTask(url3);
                    System.out.println("Comunicandome... con el servidor de procesamiento 3");
                    System.out.println("Esta disponible el 9003");
                    s3 = completableFuture3.join().toString();
                    try{
                        Thread.sleep(1000);
                    }catch(Exception e){
                        System.out.println("Exception in the thread");
                    }
                    sendResponse(s3.getBytes(), exchange);
                    disponible[2] = true;
                }
            }else{
                disponible[1] = false;
                String url2 = "http://localhost:9002/resp" + asset;
                completableFuture2 = client2.sendTask(url2);
                System.out.println("Comunicandome... con el servidor de procesamiento 2");
                System.out.println("Esta disponible el 9002");
                s2 = completableFuture2.join().toString();
                try{
                    Thread.sleep(1000);
                }catch(Exception e){
                    System.out.println("Exception in the thread");
                }
                sendResponse(s2.getBytes(), exchange);
                disponible[1] = true;
            }
        }else{
            disponible[0] = false;
            String url = "http://localhost:9001/resp" + asset;
            completableFuture = client.sendTask(url);
            System.out.println("Comunicandome... con el servidor de procesamiento");
            System.out.println("Esta disponible el 9001");
            s = completableFuture.join().toString();
            try{
                Thread.sleep(2000);
            }catch(Exception e){
                System.out.println("Exception in the thread");
            }
            sendResponse(s.getBytes(), exchange);
            disponible[0]=true;
        }
        

        System.out.println("Enviado al cliente.");
    
        //sendResponse(s.getBytes(), exchange);
    }

    private void handleRequestRespuesta(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        System.out.println("¡Soy servidor de procesamiento!");
        
        String uri = exchange.getRequestURI().toString();
        
        String[] words = uri.split("\\=")[1].split("\\+");
        
        System.out.println("Buscando en los mapas...");
        
        String libro_ac = encontrarLibroMayorMatch(words);

        System.out.println("Se encontró un libro:" + libro_ac);

        sendResponse(libro_ac.getBytes(), exchange);
    }
    
    String encontrarLibroMayorMatch(String[] words) throws IOException{

    	// System.out.println("--------------------------------------------------------------");
        String libroResp = new String("Caperucita Roja");
        
        List<Integer> nLibrosExisto = new ArrayList<Integer>();
        int estoyen = 0; 
        int i=0, j=0; 
        // System.out.println("Numero de palabras por buscar: " + words.length);
        for(j=0; j<words.length; j++){
            // System.out.println("Palabra:" + words[j]);
            for(i=0; i<libros.size();i++){
                Map<String, Integer> l = new HashMap<String, Integer>(libros.get(i));
                if(l.get(words[j])!= null){
                    estoyen  += 1;
                }
            }
            nLibrosExisto.add(estoyen);
            // System.out.println("Estoy en " + estoyen + " libros");
            estoyen = 0;
        }


        List<Integer> PTL = new ArrayList<Integer>();
        Integer suma = 0;

        for(i=0; i<libros.size();i++) {
            Map<String, Integer> l = new HashMap(libros.get(i));
           
            // System.out.println("Libro NO. " + i + " con titulo " +  titulos.get(i) );
            for (Map.Entry<String, Integer> entry : l.entrySet()) {
            	
                suma += entry.getValue();
            }
            
            PTL.add(suma);
            suma = 0;
        }
        
        double PPL = 0;
        double tf, idf, extra, parcial = 0;
        
        for(i=0; i<libros.size(); i++){
            Map<String, Integer> l = new HashMap(libros.get(i));
            
            parcial = 0;
            for(j=0; j<words.length; j++){
               
            
                if(l.get(words[j])==null){
                    // System.out.println("No estoy en el libro " +  i);
                    tf = 0;
                }else{
                    // System.out.println("Si estoy en un libro");
                    tf = l.get(words[j])/(double)PTL.get(i);  
                }
                if(nLibrosExisto.get(j)==0){
                    // System.out.println("Palabra " + words[j] + " no existe en ningun libro");
                    extra = 0;
                }else{
                    extra = 46.0/nLibrosExisto.get(j);
                    // System.out.println("Existe la palabra " + words[j] + " en " + nLibrosExisto.get(j) +  " libros");
                    // System.out.println("extra es " + String.format("%.19f", extra));
                }

                if(extra == 0){
                	// System.out.println("extra es cero y hace que todo sea cero, IDF");
                    idf = 0;
                }
                else {
                    idf = Math.log10(extra);
                   	// System.out.println("el IDF" + String.format("%.19f", idf));
                }
                parcial +=  (tf*idf);
                // System.out.println("parcial " + String.format("%.19f", parcial));
            }
            if(PPL <= parcial){
                libroResp = titulos.get(i);
                PPL = parcial;
            }
            // parcial = 0;
            // PPL = Math.max(PPL, parcial);
        }
        // System.out.println("El ppl ganador es: " + String.format("%.19f", PPL));
    	// System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");


        return libroResp;
    }


    private byte[] readUiAsset(String asset) throws IOException {
        InputStream assetStream = getClass().getResourceAsStream(asset);
        if (assetStream == null) {
            return new byte[]{};
        }

        return assetStream.readAllBytes();
    }

    private static void addContentType(String asset, HttpExchange exchange) {
        String contentType = "text/html";
        if (asset.endsWith("js")) {
            contentType = "text/javascript";
        } else if (asset.endsWith("css")) {
            contentType = "text/css";
        }
        exchange.getResponseHeaders().add("Content-Type", contentType);
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        try {            
            FrontendSearchRequest frontendSearchRequest = objectMapper.readValue(exchange.getRequestBody().readAllBytes(), FrontendSearchRequest.class); 
            System.out.println("Los datos recibidos en el servidor web son:" + frontendSearchRequest.getSearchQuery());
            String frase = frontendSearchRequest.getSearchQuery();
            System.out.println("La frase es: " + frase);
            
            StringTokenizer st = new StringTokenizer(frase);
            FrontendSearchResponse frontendSearchResponse = new FrontendSearchResponse(frase, st.countTokens());
        
            byte[] responseBytes = objectMapper.writeValueAsBytes(frontendSearchResponse);
            sendResponse(responseBytes, exchange);

        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
    }

    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }

        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }

    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
    }
}
