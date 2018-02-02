package com.penzzly.engine.core.utilites;

public final class MathUtil {
	private MathUtil() {
	}
	
	//Float Bound.
	public static float bound(float value) {
		return bound(value, 1, -1);
	}
	
	public static float bound(float value, float max, float min) {
		return Math.min(Math.max(value, min), max);
	}
	
	
	//Int Bound.
	public static double bound(double value) {
		return bound(value, 1, -1);
	}
	
	public static double bound(double value, double max, double min) {
		return Math.min(Math.max(value, max), min);
	}
}
