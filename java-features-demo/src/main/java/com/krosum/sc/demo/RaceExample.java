package com.krosum.sc.demo;

import java.time.Duration;
import java.util.concurrent.StructuredTaskScope;

public class RaceExample {

    record Weather(String provider, String temperature) {}

    public static Weather getFastestWeather() throws Exception {
        // Scope completes as soon as the first subtask succeeds
        try (var scope = new StructuredTaskScope.ShutdownOnSuccess<Weather>()) {

            scope.fork(() -> queryPrimaryServer());
            scope.fork(() -> queryBackupServer());
            scope.fork(() -> queryThirdPartyServer());

            // Wait until one subtask finishes successfully
            scope.join();

            // Automatically cancels the remaining 2 slower/running requests!
            return scope.result();
        }
    }

    private static Weather queryPrimaryServer() throws Exception {
        Thread.sleep(Duration.ofMillis(1000));
        return new Weather("Primary", "24°C");
    }

    private static Weather queryBackupServer() throws Exception {
        Thread.sleep(Duration.ofMillis(900)); // Fastest
        return new Weather("Backup", "24°C");
    }

    private static Weather queryThirdPartyServer() throws Exception {
        Thread.sleep(Duration.ofMillis(1200));
        return new Weather("ThirdParty", "24°C");
    }

    public static void main(String[] args) throws Exception {
        Weather fastest = getFastestWeather();
        System.out.println("Winner: " + fastest.provider() + " -> " + fastest.temperature());
    }
}