package com.data;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunnableTest {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		
		ExecutorService ex = Executors.newSingleThreadExecutor();
		ex.execute(() -> System.out.println("RunnableTest example"));
		ex.shutdown();
	}

}
