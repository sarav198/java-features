// File: src/com/consumer/AppMain.java
package com.consumer;

// We can import this ONLY because we 'required' the module 
// AND the provider 'exported' this specific package.
import com.provider.GreetingService; 

public class AppMain {
    public static void main(String[] args) {
        GreetingService service = new GreetingService();
        System.out.println(service.getGreeting());
    }
}