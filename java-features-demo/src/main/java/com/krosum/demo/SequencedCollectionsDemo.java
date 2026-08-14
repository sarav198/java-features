package com.krosum.demo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.SequencedMap;

public class SequencedCollectionsDemo {
    public static void main(String[] args) {
        
        // --- SEQUENCED COLLECTION (List) ---
        List<String> list = new ArrayList<>(List.of("B", "C", "D"));
        
        // Add elements to the ends (Note: List supports addFirst/addLast in Java 21)
        list.addFirst("A"); 
        list.addLast("E");  
        
        System.out.println("First element: " + list.getFirst()); // A
        System.out.println("Last element: " + list.getLast());   // E
        
        // The reversed() method creates a lightweight, memory-efficient view
        System.out.println("Reversed view: " + list.reversed()); // [E, D, C, B, A]


        // --- SEQUENCED MAP (LinkedHashMap) ---
        SequencedMap<Integer, String> map = new LinkedHashMap<>();
        map.put(1, "One");
        map.put(2, "Two");
        map.put(3, "Three");

        System.out.println("\nFirst Entry: " + map.firstEntry()); // 1=One
        System.out.println("Last Entry: " + map.lastEntry());   // 3=Three
        
        // You can now easily iterate through a map in reverse
        System.out.println("Reversed Map:");
        map.reversed().forEach((k, v) -> System.out.println(k + ": " + v));
        // Output:
        // 3: Three
        // 2: Two
        // 1: One
    }
}