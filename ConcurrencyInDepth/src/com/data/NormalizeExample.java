package com.data;

import java.nio.file.Path;
import java.nio.file.Paths;

public class NormalizeExample {

	public static void main(String[] args) {
		Path p1=Path.of("./x/y/../sean.txt");
		System.out.println(p1.normalize());
		
		Path p2=Path.of("/x/y/../z");
		System.out.println(p2.normalize());
		
		Path p3=Path.of(".\\a");
		System.out.println(p3.normalize());
		
		Path p4=Path.of("../x/y");
		System.out.println(p4.normalize()); // no directory before .. so can't be simplified
		
		//using normalize to compare different path results
		Path p5=Paths.get("/a/b/../../x.y");
		Path p6=Paths.get("/x.y");
		System.out.println(p5.normalize());
		System.out.println(p6.normalize());
		System.out.println(p5.equals(p6));
		System.out.println(p5.normalize().equals(p6.normalize()));

	}

}
