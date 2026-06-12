package com.data;

public class PrimeNumberUtils {
	static int totalPrimeNumber =0;
	// Method to check if a number is prime
    public static boolean isPrime(int num) {
        // Check for numbers less than or equal to 1
        if (num <= 1) {
            return false;
        }
        
        // Check for numbers 2 and 3
        if (num <= 3) {
            return true;
        }
        
        // Check for even numbers and multiples of 3
        if (num % 2 == 0 || num % 3 == 0) {
            return false;
        }
        
        // Check for factors from 5 up to the square root of the number
        int sqrt = (int) Math.sqrt(num);
        for (int i = 5; i <= sqrt; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0) {
                return false;
            }
        }
        
        return true;
    }
    
    // Main method for demonstration
    public static void main(String[] args) {
        int[] testNumbers = {1, 2, 3, 4, 5, 16, 17, 18, 19, 20};
        int MAX_INT = 100000000;
        for(int num=3;num<MAX_INT;num++) {
        	if(isPrime(num)) {
        		totalPrimeNumber++;
        	}
        	
        }
        System.out.println(totalPrimeNumber);
    }
}
