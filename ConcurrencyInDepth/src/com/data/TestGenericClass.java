package com.data;


class MyGeneric<T> {
	T instance;

	public MyGeneric(T instance) {
		super();
		this.instance = instance;
	}
	
	T getT() {
		return instance;
	}
}
public class TestGenericClass {

	public static void main(String[] args) {
		MyGeneric<String> my = new MyGeneric<>("SK");
		System.out.println(my.getT());
		
		MyGeneric<Integer> g2 = new MyGeneric<>(1);
		System.out.println(g2.getT());

	}

}
