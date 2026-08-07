package com.consumer;

import com.provider.GreetingService; 

public class AppMain {
    public static void main(String[] args) {
        GreetingService service = new GreetingService();
        System.out.println(service.getGreeting());
    }
}