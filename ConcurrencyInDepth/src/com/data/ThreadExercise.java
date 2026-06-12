package com.data;

public class ThreadExercise implements Runnable{

	public static void main(String[] args) {
		Thread t = new Thread();

	}

	@Override
	public void run() {
		System.out.println("Running"+ Thread.currentThread().getName());
		
	}

}
