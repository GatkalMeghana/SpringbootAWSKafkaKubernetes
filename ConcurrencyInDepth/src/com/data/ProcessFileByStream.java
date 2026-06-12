package com.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class ProcessFileByStream {

	public static void main(String[] args) {
		
		ProcessFileByStream stream = new ProcessFileByStream();
		List<Cat> cats = stream.loadCats(".cats.txt");
		cats.forEach(System.out::println);
	}
	
	private static List<Cat> loadCats(String fileName){
	List<Cat> cats = new ArrayList<>();	
	List<Cat> newcats = new ArrayList<>();	
	try(Stream<String> stream = Files.lines(Paths.get(fileName))) {
		
		stream.forEach(line -> {
			String[] catsArr = line.split("/");
			cats.add(new Cat(catsArr[0],catsArr[1]));
			newcats.addAll(cats);
		});
		
	}catch(IOException e) {
		e.printStackTrace();
	}
	return cats;
	}

}

class Cat {
	private String name;
	private String color;

	public Cat() {
	}

	public Cat(String name, String color) {
		super();
		this.name = name;
		this.color = color;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

}
