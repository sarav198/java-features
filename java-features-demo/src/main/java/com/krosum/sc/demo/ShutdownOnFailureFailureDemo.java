package com.krosum.sc.demo;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

public class ShutdownOnFailureFailureDemo {

    record UserProfile(String name) {}
    record OrderHistory(int orderCount) {}
    record Dashboard(UserProfile user, OrderHistory orders) {}

    public static Dashboard getDashboardData(String userId) throws Exception {
        long start = System.currentTimeMillis();

        // 1. Open the fail-fast scope
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

            // 2. Fork both tasks in parallel virtual threads
            Subtask<UserProfile> userTask = scope.fork(() -> fetchUserProfile(userId));
            Subtask<OrderHistory> ordersTask = scope.fork(() -> fetchOrderHistory(userId));

            System.out.println("[MAIN] Waiting for tasks to complete or fail...");

            // 3. Waits until all succeed OR until ANY task throws an exception
            scope.join();

            // 4. If ANY subtask failed, this throws that exact exception immediately
            scope.throwIfFailed();

            // 5. This line will NEVER be reached if either subtask fails
            return new Dashboard(userTask.get(), ordersTask.get());

        } catch (Exception e) {
            long total = System.currentTimeMillis() - start;
            System.err.println("\n[MAIN CAUGHT FAILURE] Operation aborted in " + total + " ms!");
            System.err.println("Cause of failure: " + e.getMessage());
            throw e;
        }
    }

    // A slow task: Takes 5 seconds
    private static UserProfile fetchUserProfile(String userId) throws Exception {
        System.out.println("[START] Fetching User Profile (Takes 5s)...");
        try {
            Thread.sleep(Duration.ofSeconds(5));
            System.out.println("[DONE] User Profile fetched successfully.");
            return new UserProfile("Alice");
        } catch (InterruptedException e) {
            // When Order Service fails, ShutdownOnFailure interrupts this thread immediately!
            System.out.println("[CANCELLED] User Profile thread was INTERRUPTED and killed early!");
            throw e;
        }
    }

    // A failing task: Fails quickly after 200 ms
    private static OrderHistory fetchOrderHistory(String userId) throws Exception {
        System.out.println("[START] Fetching Order History (Will fail in 200ms)...");
        Thread.sleep(Duration.ofMillis(200));

        // Simulate database outage / HTTP 500 error
        System.out.println("[ERROR] Order database connection timeout!");
        throw new RuntimeException("DatabaseDownException: Unable to reach Order Database");
    }

    public static void main(String[] args) {
        try {
            getDashboardData("user_101");
        } catch (Exception e) {
            System.out.println("\n[HANDLER] Handled gracefully in main method.");
        }
    }
}