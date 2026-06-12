package com.data;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;



class Worker{
	private int id;

	public Worker(int id) {
		super();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Worker [id=" + id + "]";
	}
	
}

public class ComparatorIsUseful {

	public static void main(String[] args) {
		Set<Worker> workers = new TreeSet<>();
		//Set<Worker> workers = new TreeSet<>(Comparator.comparing(Worker::getId));
		workers.add(new Worker(10));
		workers.add(new Worker(11));
		workers.add(new Worker(5));
		workers.add(new Worker(1));
		System.out.println(workers);

	}

}
