package com.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;

public class AttributeView {

	public static void main(String[] args) throws IOException {
		var path=Path.of("./src/com/data/TwoSum.java");
		System.out.println(path);
		BasicFileAttributes view = Files.readAttributes(path, BasicFileAttributes.class);
		System.out.println(view.isDirectory());
		System.out.println(view.isOther());
		System.out.println(view.isRegularFile());
		System.out.println(view.isSymbolicLink());
		System.out.println(view.lastModifiedTime());
		System.out.println(view.size());

	}

}
