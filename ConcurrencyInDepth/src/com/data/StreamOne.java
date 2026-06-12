package com.data;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StreamOne {

	public static void main(String[] args) {
		
		Stream<String> names = Stream.of("Joe","Tom","Tom","Alan","Peter");
		Map<Integer, List<String>> groupByLength = names.collect(Collectors.groupingBy(String::length));
		System.out.println(groupByLength);
		
		
		List<Double> tmps = Arrays.asList(92.36, 100.23, 56.55, 102.88);
		//tmps.stream().peek(System.out::println).filter(temp -> temp > 100).peek(System.out::println).count();
		
		
		//steam.iterate
		//Stream.iterate(2, n -> n +2).limit(10).forEach(System.out::println);
		
		String s = Stream.of("cake", "biscuit", "apple tart").collect(Collectors.joining(","));
		//System.out.println(s);
		
		Double l = Stream.of("cake", "biscuit", "apple tart").collect(Collectors.averagingInt(k -> k.length()));
		//System.out.println(l);
		
		Map<String, Integer> dessrtMap =Stream.of("cake", "biscuit", "apple tart").collect(Collectors.toMap(j -> j, j -> j.length()));
		//System.out.println(dessrtMap);
		
		Map<Object, Object> newDessrtMap = Stream.of("cake", "biscuit", "tart").collect(Collectors.toMap(j -> j.length(), j -> j, (j1,j2) -> j1 + ","+j2));
		//System.out.println(newDessrtMap);
		
		Map<Object, Object> newDessrtMap1 = Stream.of("cake", "biscuit", " apple tart", "cake").collect(Collectors.toMap(j -> j, j -> j.length(), (j1,j2) -> j1.toString()+ j2.toString(), TreeMap :: new));
		//System.out.println(newDessrtMap1);
	}

}
