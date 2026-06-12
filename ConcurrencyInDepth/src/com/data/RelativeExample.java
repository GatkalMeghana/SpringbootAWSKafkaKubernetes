package com.data;

import java.nio.file.Path;
import java.nio.file.Paths;

public class RelativeExample {

	public static void main(String[] args) {

		Path p1 = Paths.get("cattle.txt");
		Path p2 = Path.of("farm/horses.txt");
		System.out.println(p1.relativize(p2));
		System.out.println(p2.relativize(p1));
		
		Path p3=Paths.get("C:\\cattle.txt");
		Path p4= Path.of("C:\\Users\\horses.txt");
		System.out.println(p3.relativize(p4));
		
		Path p5=Paths.get("cattle.txt");
		Path p6=Paths.get("C:\\cattle.txt");
		System.out.println(p5.relativize(p6));
	}

}
