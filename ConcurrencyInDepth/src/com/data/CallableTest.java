package com.data;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class CallableTest {

	public static void main(String[] args) {
		ExecutorService es = Executors.newSingleThreadExecutor();
		Future<Integer> future =  es.submit(() -> 5+6);
		try {
			int result = future.get(50000, TimeUnit.MILLISECONDS);
			System.out.println(result);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			e.printStackTrace();
		} 
		es.shutdown();

	}

}
