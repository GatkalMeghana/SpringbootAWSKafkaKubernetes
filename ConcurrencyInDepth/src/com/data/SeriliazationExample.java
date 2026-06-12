package com.data;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

class Book implements Serializable{
	private BookMarker p = new BookMarker();
	private transient String author;
	public Book(String author) {
		super();
		this.author = author;
	}
	@Override
	public String toString() {
		return "Book [p=" + p + ", author=" + author + "]";
	}
	
}
class BookMarker implements Serializable{
	private transient Image i = new Image();
}
class Image {
	
}
public class SeriliazationExample implements Serializable {
	
	public static void main(String[] args) {
		try(var out = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream("book.ser")))){
			Book b = new Book("sean kennady");
			out.writeObject(b);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//deserilize a book
		try(var in = new ObjectInputStream(new BufferedInputStream(new FileInputStream("book.ser")))){
			Book b = (Book)in.readObject();
			System.out.println("After"+b);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

}


