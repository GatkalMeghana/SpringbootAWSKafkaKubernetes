package com.data;

import java.util.concurrent.atomic.AtomicInteger;

public class CheckPrimeNumber {
 static int MAX_INT = 100000000;
 static int CONCURRENCY=10;

 
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		  AtomicInteger totalPrimeNumber = new AtomicInteger(0);

		for(int i=3;i<MAX_INT;i++) {
			if(checkPrime(i))
				totalPrimeNumber.getAndAdd(1);
		}
		long endTime=System.currentTimeMillis();
		System.out.println("time taken-"+(endTime-startTime));
		System.out.println("totalPrimeNumber-"+totalPrimeNumber);
		

	}
	private static boolean checkPrime(int x) {
		if(x==1) {
			return false;
		}
		for(int i=3;i<= Math.sqrt(x);i++) {
			if(x%i ==0) {
				return false;
			}
		}
		return true;
	}
	

}
