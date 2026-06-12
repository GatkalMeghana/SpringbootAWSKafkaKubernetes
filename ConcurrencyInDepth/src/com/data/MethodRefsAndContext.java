package com.data;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class MethodRefsAndContext {

	public static void main(String[] args) {
		Supplier<Integer> lambda = () -> Person.howMany();
		Supplier<Integer> mr = Person::howMany;
		System.out.println(lambda.get());
		System.out.println(mr.get());
		
		Function<Person, Integer> lambda2 = person -> Person.howMany(person);
		Function<Person, Integer> mr2 = Person::howMany;
		System.out.println(lambda2.apply(new Person()));
		System.out.println(mr2.apply(new Person()));
		
		BiFunction<Person,Person,Integer> lambda3 = (p1,p2) -> Person.howMany(p1,p2);
		BiFunction<Person,Person,Integer> mr3 = Person::howMany;
		System.out.println(lambda3.apply(new Person(), new Person()));
		System.out.println(mr3.apply(new Person(), new Person()));
	}

}

class Person{
	
	public static Integer howMany(Person... persons ) {
		return persons.length;
	}
}
