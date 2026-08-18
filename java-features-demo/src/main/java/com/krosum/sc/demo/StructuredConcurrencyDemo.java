package com.krosum.sc.demo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class StructuredConcurrencyDemo {

    record UserProfile(String name) {}
    record OrderHistory(int orderCount) {}
    record Dashboard(UserProfile user, OrderHistory orders) {}

    private static final HttpClient client = HttpClient.newHttpClient();

    public static Dashboard getDashboardData(String userId) throws Exception {
        // 1. Open the structured scope
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            // 2. Fork subtasks (each runs in its own Virtual Thread automatically)
            Subtask<UserProfile> userTask = scope.fork(() -> fetchUserProfile(userId));
            Subtask<OrderHistory> ordersTask = scope.fork(() -> fetchOrderHistory(userId));

            // 3. Wait for all subtasks to complete or for ANY subtask to fail
            scope.join();

            // 4. If any subtask threw an exception, cancel others and rethrow here
            scope.throwIfFailed();

            // 5. Both tasks succeeded; safely extract results using .get()
            return new Dashboard(userTask.get(), ordersTask.get());
            
        } // 6. Scope automatically cleans up and terminates all virtual threads on exit
    }

    private static UserProfile fetchUserProfile(String userId) throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://jsonplaceholder.typicode.com/users/1"))
                .timeout(Duration.ofSeconds(3))
                .GET()
                .build();
        client.send(request, HttpResponse.BodyHandlers.ofString());
        return new UserProfile("Alice");
    }

    private static OrderHistory fetchOrderHistory(String userId) throws Exception {
        // Simulate database lookup latency
        Thread.sleep(Duration.ofMillis(3000));
        return new OrderHistory(5);
    }

    public static void main(String[] args) throws Exception {
        Dashboard dashboard = getDashboardData("user_101");
        System.out.println("Loaded Dashboard: " + dashboard);
    }
}