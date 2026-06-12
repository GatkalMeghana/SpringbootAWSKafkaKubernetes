package com.data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UseLambdaAsRunnable {

	public static void main(String[] args) {
		Thread t = new Thread(() -> System.out.println("run() " + Thread.currentThread().getName()));
		//t.start();
		t.run();
		System.out.println("main() " + Thread.currentThread().getName());
		
		int cntProcessor = Runtime.getRuntime().availableProcessors();
		System.out.println(cntProcessor);
		ExecutorService ex = Executors.newFixedThreadPool(cntProcessor);
		String result="test";
		ex.submit(t);
		System.out.println(result);
	}

}
