package com.data;

import java.util.Comparator;
import java.util.stream.Stream;

public class ComparatorJava8 {

	public static void main(String[] args) {
		Customer john = new Customer("John", 25);
		Customer mery = new Customer("Mery", 23);
		
		Stream.of(john,mery).sorted(Comparator.comparing(Customer::getAge)).forEach(System.out::println);

	}

}

class Customer{
	private String name;
	private  int age;
	
	@Override
	public String toString() {
		return "Customer [name=" + name + ", age=" + age + "]";
	}
	public Customer(String name, int age) {
		super();
		this.name = name;
		this.age = age;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getAge() {
		return age;
	}
	public void setAge(int age) {
		this.age = age;
	}
	
}
