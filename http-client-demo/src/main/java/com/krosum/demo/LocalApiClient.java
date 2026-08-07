package com.krosum.demo;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// Import Jackson classes
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class LocalApiClient {

    private static final String LOGIN_URL = "http://localhost:8083/api/users/login";
    private static final String USER_VALIDATE_URL = "http://localhost:8083/api/demo/user";
    
    // Create a single, reusable instance of ObjectMapper
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();

        System.out.println("--- 1. Executing POST Request (Login) ---");
        String token = executeLogin(client);

        if (token != null && !token.isBlank()) {
            System.out.println("\n--- 2. Executing GET Request (Validate User) ---");
            validateUser(client, token);
        } else {
            System.out.println("\nSkipping validation because no token was retrieved.");
        }
    }

    private static String executeLogin(HttpClient client) {
        String jsonPayload = "{\"username\": \"john_doe112\", \"password\": \"password123\"}";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(LOGIN_URL))
                .header("Content-Type", "application/json") 
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Login Status Code: " + response.statusCode());
            
            if (response.statusCode() == 200) {
                // Parse the JSON response using Jackson
                JsonNode rootNode = objectMapper.readTree(response.body());
                
                // Extract the value of the "token" field. 
                // NOTE: Change "token" to match the actual key in your API's JSON response (e.g., "accessToken", "jwt")
                if (rootNode.has("token")) {
                    String extractedToken = rootNode.get("token").asText();
                    System.out.println("Successfully extracted token!");
                    return extractedToken;
                } else {
                    System.err.println("Login succeeded, but the 'token' field was not found in the response.");
                    System.err.println("Actual response: " + response.body());
                }
            } else {
                System.err.println("Login failed. Response: " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("Login POST request failed: " + e.getMessage());
        }
        return null;
    }

    private static void validateUser(HttpClient client, String token) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(USER_VALIDATE_URL))
                .header("Authorization", "Bearer " + token)
                .GET() 
                .build();

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Validate Status Code: " + response.statusCode());
            System.out.println("Validate Response Body: " + response.body());
        } catch (Exception e) {
            System.err.println("Validation GET request failed: " + e.getMessage());
        }
    }
}