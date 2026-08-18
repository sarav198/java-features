package com.krosum.vt.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.*;

public class RealTimeApiAggregator {

    // 1. Bundle URL and response body together in the return type
    record ApiResponse(String url, String body) {}

    private static final HttpClient client = HttpClient.newHttpClient();

    public static void main(String[] args) {
        List<String> endpoints = List.of(
            "https://postman-echo.com/delay/10",
            "https://postman-echo.com/delay/13",
            "https://jsonplaceholder.typicode.com/users/1"
        );

        ThreadFactory factory = Thread.ofVirtual()
                .name("order-worker-", 0)
                .factory();

        try (var executor = Executors.newThreadPerTaskExecutor(factory)) {

            CompletionService<ApiResponse> completionService = new ExecutorCompletionService<>(executor);

            // 2. Submit all tasks
            for (String url : endpoints) {
                completionService.submit(new ApiCaller1(url));
            }

            // 3. Take completed tasks (fastest first, URL stays accurately paired)
            for (int i = 0; i < endpoints.size(); i++) {
            	

            	try {
            		Future<ApiResponse> completedFuture = completionService.take(); //Blocking. If nothing is finished yet, it pauses and waits indefinitely until any task finishes.
            		//Future<ApiResponse> completedFuture = completionService.poll(2, TimeUnit.SECONDS); // Timed wait. Waits up to 5 seconds for a task to finish before giving up and returning null.
            	    // Returns the value if successful, or throws ExecutionException if the task failed
            		
            		// Check for timeout
                    if (completedFuture == null) {
                        System.out.println("\n[TIMEOUT] No response within 2 seconds. Aborting remaining tasks...");
                        
                        // Immediately interrupt all active virtual threads!
                        executor.shutdownNow();
                        break; 
                    }
                    
            	    ApiResponse result = completedFuture.get(); 
            	    System.out.println("Success from: " + result.url());
            	} catch (ExecutionException e) {
            	    // The background task threw an exception (e.g. timeout, network failure)
            	    System.err.println("API call failed: " + e.getCause().getMessage());
            	} catch (InterruptedException e) {
            	    Thread.currentThread().interrupt();
            	}
            }
        }
    }

    // 4. Callable returns ApiResponse instead of plain String
    static class ApiCaller implements Callable<ApiResponse> {
        private final String url;

        ApiCaller(String url) {
            this.url = url;
        }

        @Override
        public ApiResponse call() throws Exception {
            System.out.println("[START] " + Thread.currentThread().getName() + " fetching -> " + url);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("[DONE]  " + Thread.currentThread().getName() + " finished -> " + url);
            return new ApiResponse(url, response.body());
        }
    }
    
    static class ApiCaller1 implements Callable<ApiResponse> {
        private final String url;

        ApiCaller1(String url) {
            this.url = url;
        }

        @Override
        public ApiResponse call() throws Exception {
            System.out.println("[START] " + Thread.currentThread().getName() + " fetching -> " + url);

            if (url.contains("delay/10")) {
                Thread.sleep(Duration.ofSeconds(10));
            } else if (url.contains("delay/13")) {
                Thread.sleep(Duration.ofSeconds(13));
            } else {
                Thread.sleep(Duration.ofMillis(200));
            }

            System.out.println("[DONE]  " + Thread.currentThread().getName() + " finished -> " + url);
            return new ApiResponse(url, "Simulated payload");
        }
    }
    
    
}