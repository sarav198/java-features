package com.krosum.vt.demo;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.stream.IntStream;

public class HighConcurrencyDemo {

    public static void main(String[] args) {
        int taskCount = 100;

        System.out.println("Starting " + taskCount + " virtual threads...");
        Instant start = Instant.now();
     // Creates a factory: names will be "order-worker-0", "order-worker-1", etc.
        ThreadFactory factory = Thread.ofVirtual()
                .name("order-worker-", 0) // prefix, start index
                .factory();
        
       // try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
        	try (var executor = Executors.newThreadPerTaskExecutor(factory)) {
        	
        	forLoop(taskCount, executor);
        	//intStream(taskCount, executor);
        		
        } // Executor.close() is called here, blocking until all tasks complete

        Instant end = Instant.now();
        System.out.println("Completed " + taskCount + " tasks in: " 
                + Duration.between(start, end).toMillis() + " ms");
    }

	private static void intStream(int taskCount, ExecutorService executor) {
		//IntStream.range(0, taskCount).forEach(i -> {
		IntStream.rangeClosed(0, taskCount).forEach(i -> {
		executor.submit(() -> {
			System.out.println("Running on for task id: " +i+ "  " + Thread.currentThread());
		    //Thread.sleep(Duration.ofMillis(500));
		});
         });
	}

	private static void forLoop(int taskCount, ExecutorService executor) {
		for (int i = 0; i < taskCount; i++) {
		    final int taskId = i;
		    executor.submit(() -> {
		    	System.out.println("Running on for task id: " +taskId+ "  " + Thread.currentThread());
		        //Thread.sleep(Duration.ofMillis(500));
		        //return taskId;
		    });
		}
		
	}
}



/*
 * Executors.newVirtualThreadPerTaskExecutor() creates an executor that starts a
 * fresh, lightweight virtual thread for every single task submitted to it.
 */