package com.data;

public class PrivateInterfaceTennis{
	public static void main(String[] args) {
		Tennis tennis = new ProfessionalTennis();
		//tennis.hit();
		tennis.backhand();
		Tennis.forehand();
	}
}
interface  InefficinientTeenis{
	static void forehand() {
		System.out.println("Move into position");
		System.out.println("Hitting a forehand");
		System.out.println("Move into position1");
	}
	
	default void backhand() {
		System.out.println("Move into position");
		System.out.println("Hitting a backend");
		System.out.println("Move into position2");
	}
	
	default void smash() {
		System.out.println("Move into position");
		System.out.println("Hitting a smash");
		System.out.println("Move into position3");
	}

}

interface Tennis{
	private static void hit(String stroke) {
		System.out.println("Move into position");
		System.out.println("Hitting a"+stroke);
		System.out.println("Move into position3");
	}
	
	static void forehand() {
		hit("Forehand");
	}
	default void backhand() {
		hit("backhand");
	}
	private void smash() {
		hit("smash");
	}
}

class ProfessionalTennis implements Tennis{
	
}
