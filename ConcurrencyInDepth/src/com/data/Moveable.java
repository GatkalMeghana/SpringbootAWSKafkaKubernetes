package com.data;

public interface Moveable<T> {
void move(T t);
}

class MoveFeline implements Moveable<Cat>{

	@Override
	public void move(Cat t) {
		
	}
	
}
class Caline implements Moveable<Dog>{

	@Override
	public void move(Dog t) {
		
	}
	
}
class SomeMoveable<U> implements Moveable<U>{

	@Override
	public void move(U t) {
		
	}
	
}
