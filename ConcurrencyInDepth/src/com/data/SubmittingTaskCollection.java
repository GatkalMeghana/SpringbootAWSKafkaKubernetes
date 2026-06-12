package com.data;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SubmittingTaskCollection {
	private static ExecutorService es = Executors.newSingleThreadExecutor();
	private static List<Callable<String>> callables = new ArrayList<>();

	public static void main(String[] args) {
		callables.add(() -> "A");
		callables.add(() -> "B");
		callables.add(() -> "C");
		callables.add(() -> "D");
		try {
			List<Future<String>> result = es.invokeAll(callables);
			System.out.println(result.get(0).get());
			System.out.println(result.get(1).get());
			System.out.println(result.get(2).get());
			System.out.println(result.get(3).get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		es.shutdown();
	}

}
