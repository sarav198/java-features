package com.krosum.demo;

public class SwitchCaseDemo {
	public static void main(String[] args) {
		String membershipTier = "SILVER";

		int discountPercentage = switch (membershipTier) {
		    // 1. Multiple labels with a simple single-line return
		    case "NEW", "BRONZE" -> 0;
		    
		    // 2. Single label with a simple return
		    case "SILVER" -> 5;
		    
		    // 3. Complex block requiring 'yield'
		    case "GOLD", "PLATINUM" -> {
		        System.out.println("VIP member detected! Applying maximum discount.");
		        yield 15; // Returns 15 to the discountPercentage variable
		    }
		    
		    // 4. Default case is required because Strings have infinite possibilities
		    default -> {
		        System.out.println("Error: Unrecognized tier. Defaulting to 0%.");
		        yield 0; 
		    }
		};

		System.out.println("Discount applied: " + discountPercentage + "%");
	}
}
