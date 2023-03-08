package com.example.mathpractice.math;

public abstract class AbstractPractice {

	public static final int TYPE_TRINOM = 0;
	public static final int TYPE_MUL_TABLE = 1;

	int type;
	int level;


	public AbstractPractice(int type, int level) {
		this.level = level;
	}

	public int getType() {
		return type;
	}

	public int getLevel() {
		return level;
	}

	public abstract String toExp();

	protected abstract void solve();

	public abstract boolean check(double... solutions);

	//Utility functions
	public static String fn(double num) {
		if (((int) num) == num) {
			return (int) num + "";
		}
		return num + "";
	}

	public static double round(double num, int digitsAfterDecPoint) {
		return ((int) (num * Math.pow(10, digitsAfterDecPoint))) / Math.pow(10, digitsAfterDecPoint);
	}
}
