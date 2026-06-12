package com.data;

enum Device {LAPTOP, MOBILE};
public class CustomAnnotation {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

@interface Human{} //marker inetface no element

@interface onWeb{
	int startTime() default 9;
	int hoursPerDay();
	//as in interface vales are by default public static and final
	//constants are not considered element, so marker interface can have constants
	int PEAK_TIME_STAR=19;
	public static final int PEAK_TIME_END=22;
	
	//element type must be string, array, class, another annotation, an enum, primitive type
	//wrappers not allowed
	//Integer turnOff();
	Device consume() default Device.LAPTOP;
	Class HumanOrBoat() default Human.class;
	Human extraInfo() default @Human;
	String[] sites() default {"s","K"};
}

//Apply the annotation
@onWeb(hoursPerDay = 5)
@Human
class Student{
	
}
@onWeb(hoursPerDay = 6, startTime = 18)
class Worker1{
	
}