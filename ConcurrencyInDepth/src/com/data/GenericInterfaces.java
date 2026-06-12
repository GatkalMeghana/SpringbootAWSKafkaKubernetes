package com.data;

public class GenericInterfaces {

	public static void main(String[] args) {
		new Caline().move(new Dog());
		//new Caline().move(new Cat());
		
		new MoveFeline().move(new Cat());
		//new MoveFeline().move(new Dog());
		
		new SomeMoveable().move(new Dog());
		new SomeMoveable().move(new Cat());
	}

}
