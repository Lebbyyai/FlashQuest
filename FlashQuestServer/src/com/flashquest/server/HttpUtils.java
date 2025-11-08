package com.flashquest.server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpUtils {

    private static final HttpClient client = HttpClient.newHttpClient();

    public static String sendPostRequest(String apiKey, String modelName, String jsonBody) {
        try {
           
            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return response.body();
            } else {
                System.err.println("AI Error - Status Code: " + response.statusCode());
                System.err.println("AI Error - Response: " + response.body());
                return null; 
            }
        } catch (Exception e) {
            System.err.println("AI Request Failed: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}