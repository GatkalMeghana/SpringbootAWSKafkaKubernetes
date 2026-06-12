package com.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class ComputingPrimeUnfair {
	static int MAX_INT = 100000000;
	static int CONCURRENCY = 10;
	
	static AtomicInteger totalPrimeNumber = new AtomicInteger(0);
	public static void main(String[] args) throws InterruptedException {
		ExecutorService executor = Executors.newFixedThreadPool(CONCURRENCY);
		int batchSize = MAX_INT/CONCURRENCY;
		List<Future<Long>> futures = new ArrayList<>();
		int start=3;
		long end=0;
		for(int i=0;i< CONCURRENCY-1;i++) {
			long startNum=i==0 ? start : end;
			long endNum=startNum+batchSize;
				Callable<Long> task = () -> doBatch(startNum,endNum, Thread.currentThread().getName());
				futures.add(executor.submit(task));
				end=endNum;
		}

		long startTime = System.currentTimeMillis();
		long cnt = doBatch(3,MAX_INT, Thread.currentThread().getName());	
		long endTime = System.currentTimeMillis();
		System.out.println("time taken-" + (endTime - startTime) +"count-"+ cnt);

	}

	private static boolean checkPrime(long x) {
		if (x == 1) {
			return false;
		}
		for (long i = 3; i <= Math.sqrt(x); i++) {
			if (x % i == 0) {
				return false;
			}
		}
		return true;
	}
	
	private static long doBatch(long start, long end, String name) {
		long startTime = System.currentTimeMillis();
		long primeCount=0;
		for(long i= start; i<= end; i++) {
			if(checkPrime(i))
				primeCount++;
			
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Thread "+name +"for Numbers "+start+" -"+end+" time taken-" + (endTime - startTime));
		return primeCount;
	}

}
