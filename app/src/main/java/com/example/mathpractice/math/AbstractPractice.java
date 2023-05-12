package com.example.mathpractice.math;

/**
 * This is the abstract class that represents a practice, parent of {@link MulTable} and {@link Trinom}.
 * ALso includes static utility functions, such as fn() and round().
 * */
public abstract class AbstractPractice {

	/**
	 * Number code for {@link Trinom} practice type.
	 * */
	public static final int TYPE_TRINOM = 0;

	/**
	 * Number code for {@link MulTable} practice type.
	 * */
	public static final int TYPE_MUL_TABLE = 1;

	/**
	 * The type of the practice.
	 * */
	public final int type;

	/**
	 * The level of the practice.
	 * */
	public final int level;

	/**
	 * Basic constructor.
	 * @param type the type.
	 * @param level the level.
	 */
	public AbstractPractice(int type, int level) {
		this.type = type;
		this.level = level;
	}

	/**
	 * Type's getter.
	 * @return the type.
	 */
	public int getType() {
		return type;
	}

	/**
	 * Level's getter.
	 * @return the level.
	 */
	public int getLevel() {
		return level;
	}

	/**
	 * THis method converts the practice to mathematically written expression.
	 * @return The mathematically written expression of this practice as String.
	 * */
	public abstract String toExp();

	/**
	 * This method set the solutions for the practice, as needed by the given data.
	 * */
	protected abstract void solve();

	/**
	 * Check if the given solutions are correct.
	 * @param solutions array of solutions (each practice type require different solutions number).
	 * @return If the Solutions are correct, as boolean.
	 * */
	public abstract boolean check(double... solutions);

	//Utility functions
	/**
	 * Formatting function for doubles. This method removes the default zero for integer doubles.
	 * @param num the number to format.
	 * @return formatted number as String.
	 * */
	public static String fn(double num) {
		if (((int) num) == num) {
			return (int) num + "";
		}
		return num + "";
	}

	/**
	 * Rounding function for numbers.
	 * @param num the number to round.
	 * @param digitsAfterDecPoint how many digits keep (after the decimal point).
	 * @return rounded double.
	 * */
	public static double round(double num, int digitsAfterDecPoint) {
		return ((int) (num * Math.pow(10, digitsAfterDecPoint))) / Math.pow(10, digitsAfterDecPoint);
	}
}
