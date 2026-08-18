package com.krosum.vt.demo;

public class VirtualThreadDemo {
    public static void main(String[] args) throws InterruptedException {
        // Option 1: Start a single virtual thread
        Thread vThread = Thread.ofVirtual()
                .name("user-worker-", 1)
                .start(() -> {
                    System.out.println("Running on: " + Thread.currentThread());
                });

        vThread.join(); // main thread pauses and waits for the virtual thread to complete.

        // Option 2: Using the static factory
        Thread unstartedThread = Thread.ofVirtual().unstarted(() -> {
            System.out.println("Started later!");
        });
        unstartedThread.start();
        unstartedThread.join();
    }
}

