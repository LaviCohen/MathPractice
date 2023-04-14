package com.example.mathpractice.math;

import java.util.Random;

/**
 * Subclass  of {@link AbstractPractice}, represents specifically multiplication table practice type.
 * */
public class MulTable extends AbstractPractice{

    /**
     * The solution for the multiplication.
     * */
    public double solution;

    /**
     * The factors of the multiplication.
     * */
    public double a, b;

    /**
     * Basic constructor.
     * @param level the level.
     * @param a the first factor.
     * @param b the second factor.
     */
    public MulTable(int level, double a, double b){
        super(TYPE_MUL_TABLE, level);
        this.a = a;
        this.b = b;
        this.solve();
    }

    /**
     * Converts this {@link MulTable} to mathematical expression.
     * @return a String, represents the practice.
     */
    @Override
    public String toExp() {
        return fn(a) + " x " + fn(b);
    }

    /**
     * Set the correct solution, base on the factors.
     */
    @Override
    protected void solve() {
        this.solution = round(this.a * this.b, 3);
    }

    /**
     * Check the given solutions.
     * @param solutions array of solutions (each practice type require different solutions number).
     * @return if the solutions are correct.
     */
    @Override
    public boolean check(double... solutions) {
        return this.solution == solutions[0];
    }

    /**
     * This method generates {@link MulTable} in the required level.
     * The levels are:
     * <table>
     * <CAPTION><EM>The levels and their definitions</EM></CAPTION>
     * <tr><td>1</td><td>both of the roots are integers from 1 to 10</td></tr>
     * <tr><td>2</td><td>The first root is number from 1.5 to 9.5 (Always have 5 after decimal point)
     *  and the second is from 1 to 10, might be integer or as <i>x.5</i>. </td></tr>
     * <tr><td>3</td><td>both of the roots are doubles from 1 to 10, with one digit after decimal point.</td></tr>
     * </table>
     * @param level the required level to generate the practice at.
     * @return generated practice as required.
     * */
    public static MulTable generateMulTable(int level){
        //Creating random object for number generations
        Random rnd = new Random();
        //Defining the parameters
        double a = 0, b = 0;
        if (level == 1) {
            //Level 1 practice generation
            //Both factors are integers between 1-10
            a = rnd.nextInt(10) + 1;
            b = rnd.nextInt(10) + 1;
        } else if (level == 2) {
            //Level 2 practice generation
            //Both factors might be numbers with possible half between 0.5-10, one of them must be
            a = (Math.round(rnd.nextInt(20)/2.0) * 2 + 1) / 2.0;
            b = (rnd.nextInt(20) + 1) / 2.0;
        } else if (level == 3) {
            //Level 3 practice generation
            //Both factors are one digits after decimal point numbers, between 0.1-10
            a = ((rnd.nextInt(100) + 1) / 10.0) % 10;
            b = ((rnd.nextInt(100) + 1) / 10.0) % 10;
        }
        //Returning the new generated Trinom
        return new MulTable(level, a, b);
    }
}