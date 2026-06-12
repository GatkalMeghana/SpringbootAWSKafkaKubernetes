package com.data;

import java.nio.file.Path;
import java.nio.file.Paths;

public class ResolveExample {

	public static void main(String[] args) {
		Path absolute = Paths.get("C:\\Downloads\\Dummy");
		Path relative = Path.of("nio2");
		Path file = Path.of("name.txt");
		
		System.out.println("absolute resolve"+ absolute.resolve(relative));
		//System.out.println("absolute resolve file"+absolute.resolve(file));
		/*System.out.println("Relative resolve"+relative.resolve(file));
		
		//trying to resove absolute path without anything just return absolute path
		System.out.println("relative.resolve(absolute)"+relative.resolve(absolute));
		System.out.println("file.resolve(absolute)"+file.resolve(absolute));
		
		Path p1=Path.of("dir");
		Path p2=Path.of("sk.txt");
		System.out.println("dir.resolve(sk.txt)"+p1.resolve(p2));
		System.out.println("sk.txt.resove(dir)"+p2.resolve(p1));*/
		Path p1 = Paths.get("/Users/meghana/Documents");
		Path p2 = Paths.get("text2.txt");
		Path result1 = p1.resolve(p2);
		System.out.println(result1);
	}

}
