package Snykkk.api;

import java.util.Random;

public class FNum {

	public static int randomInt(int min, int max) {
		max++;
		return new Random().nextInt(max) + min;
	}
	
	public static int randomInt(String min, String max) {
		return randomInt(ri(max), ri(min));
	}
	
	public static double randomDouble(double min, double max) {
		max++;
		return (Math.random() * (max) + min);
	}
	public static double randomDouble(String min, String max) {
		return randomDouble(rd(max), rd(min));
	}
	
	//cho so' 5 -> random tu -5 den 5
	public static double randomDoubleNnega(double d) {
		double nega = d; nega *= -1;
		return (Math.random() * (d - nega) + nega);
	}
	
	public static double rd(String s) {
		return Double.valueOf(s);
	}
	
	public static int ri(String s) {
		return Integer.valueOf(s);
	}
	
	public static String ro(String s) {
		return String.valueOf(s);
	}
}
