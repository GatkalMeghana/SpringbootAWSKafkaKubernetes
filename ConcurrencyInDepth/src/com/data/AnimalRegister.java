package com.data;

class Register<T, U, V> {
	private T type;
	private T name;
	private T age;
	public Register(T type, T name, T age) {
		this.type = type;
		this.name = name;
		this.age = age;
	}
	public T getType() {
		return type;
	}
	public T getName() {
		return name;
	}
	public T getAge() {
		return age;
	}
	

}

public class AnimalRegister {

	public static void main(String[] args) {
		new Register(new Dog(),"Bruno",8);
		new Register(new Cat(),"Kitty",9);
	}

}
