package com.penzzly.engine.core.utilites.functions;

import java.util.function.Predicate;

public final class PredicateUtil {
	private PredicateUtil() {
	}
	
	//--Integer--
	public static Predicate<Integer> greaterThan(int amount) {
		return value -> value > amount;
	}
	
	public static Predicate<Integer> lessThan(int amount) {
		return value -> value < amount;
	}
	
	public static Predicate<Integer> multipleOf(int amount) {
		return value -> value % amount == 0;
	}
	
	
	//--Double--
	public static Predicate<Double> greaterThan(double amount) {
		return value -> value > amount;
	}
	
	public static Predicate<Double> lessThan(double amount) {
		return value -> value < amount;
	}
	
	public static Predicate<Double> multipleOf(double amount) {
		return value -> value % amount == 0;
	}
	
	
	//--Float--
	public static Predicate<Float> greaterThan(float amount) {
		return value -> value > amount;
	}
	
	public static Predicate<Float> lessThan(float amount) {
		return value -> value < amount;
	}
	
	public static Predicate<Float> multipleOf(float amount) {
		return value -> value % amount == 0;
	}
	
	//--Long--
	public static Predicate<Long> greaterThan(long amount) {
		return value -> value > amount;
	}
	
	public static Predicate<Long> lessThan(long amount) {
		return value -> value < amount;
	}
	
	public static Predicate<Long> multipleOf(long amount) {
		return value -> value % amount == 0;
	}
	
	//--Short--
	public static Predicate<Long> greaterThan(short amount) {
		return value -> value > amount;
	}
	
	public static Predicate<Long> lessThan(short amount) {
		return value -> value < amount;
	}
	
	public static Predicate<Long> multipleOf(short amount) {
		return value -> value % amount == 0;
	}
}