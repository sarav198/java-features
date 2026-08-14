package com.krosum.demo;

//1. THE STRICT PARENT (Sealed)
abstract sealed class PaymentMethod permits CreditCard, PayPal, Crypto {
 // Shared behavior available to ALL subclasses
 public void printReceipt() {
     System.out.println("-> Receipt printed for " + this.getClass().getSimpleName() + " transaction.");
 }
}

//2. THE DEAD ENDS (Final)
final class CreditCard extends PaymentMethod {
 public void swipe() {
     System.out.println("Swiping credit card...");
 }
}

final class PayPal extends PaymentMethod {
 public void loginAndPay() {
     System.out.println("Logging into PayPal...");
 }
}

//3. THE OPEN DOOR (Non-sealed)
non-sealed class Crypto extends PaymentMethod {
 public void validateBlockchain() {
     System.out.println("Validating on the blockchain...");
 }
}

//4. THE VALID EXTENSION
class Bitcoin extends Crypto {
 // Inherits everything from Crypto AND PaymentMethod
}

//--- MAIN EXECUTION ---
public class SealedDemo {
 public static void main(String[] args) {
     
     System.out.println("--- Processing Payments ---");
     
     PaymentMethod payment1 = new CreditCard();
     PaymentMethod payment2 = new Bitcoin(); // Valid extension of the non-sealed class
     
     process(payment1);
     System.out.println();
     process(payment2);
 }

 // Java 21 Pattern Matching Switch
 public static void process(PaymentMethod method) {
     // Notice: NO 'default' case! The compiler knows these are the only 3 permitted doors.
     switch (method) {
         case CreditCard cc -> {
             cc.swipe();
             cc.printReceipt();
         }
         case PayPal pp -> {
             pp.loginAndPay();
             pp.printReceipt();
         }
         case Crypto c -> {
             // Bitcoin gets cleanly caught right here
             System.out.println("Handling Crypto: " + c.getClass().getSimpleName());
             c.validateBlockchain(); 
             c.printReceipt();
         }
     }
 }
}