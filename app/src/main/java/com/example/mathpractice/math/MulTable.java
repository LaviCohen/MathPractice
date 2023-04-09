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
     * The roots of the multiplication.
     * */
    public double a, b;

    public MulTable(int level, double a, double b){
        super(TYPE_MUL_TABLE, level);
        this.a = a;
        this.b = b;
        this.solve();
    }

    @Override
    public String toExp() {
        return fn(a) + " x " + fn(b);
    }

    @Override
    protected void solve() {
        this.solution = round(this.a * this.b, 3);
    }

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
        Random rnd = new Random();
        double a = 0, b = 0;
        if (level == 1) {
            a = rnd.nextInt(10) + 1;
            b = rnd.nextInt(10) + 1;
        } else if (level == 2) {
            a = (rnd.nextInt(20)/2 * 2 + 1) / 2.0;
            b = (rnd.nextInt(20) + 1) / 2.0;
        } else if (level == 3) {
            a = ((rnd.nextInt(100) + 1) / 10.0) % 10;
            b = ((rnd.nextInt(100) + 1) / 10.0) % 10;
        }
        return new MulTable(level, a, b);
    }
}