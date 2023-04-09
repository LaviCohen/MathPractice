package com.example.mathpractice.math;

import java.util.Random;

/**
 * Subclass  of {@link AbstractPractice}, represents specifically trinom practice type.
 * */
public class Trinom extends AbstractPractice{

    public double solution1, solution2;
    public double a, b, c;

    public Trinom(int level, double a, double b, double c) throws IllegalArgumentException{
        super(TYPE_TRINOM, level);
        if (a == 0) {
            throw new IllegalArgumentException("Parameter a cannot be 0");
        }
        this.a = a;
        this.b = b;
        this.c = c;
        this.solve();
    }

    @Override
    public String toExp() {
        String exp = "";
        if (a != 1) {
            exp += fn(a);
        }
        exp += "x²";
        if (b != 0) {
            if (b < 0) {
                exp += " - ";
            } else {
                exp += " + ";
            }
            if (Math.abs(b) != 1) {
                if (b > 0) {
                    exp += fn(b);
                } else {
                    exp += fn(Math.abs(b));
                }
            }
            exp += "x";
        }
        if (c != 0) {
            if (c > 0) {
                exp += " + " + fn(c);
            } else {
                exp += " - " + fn(Math.abs(c));
            }
        }
        return exp + " = 0";
    }

    @Override
    protected void solve() throws IllegalArgumentException{
        double underSqrt = (b * b) - (4 * a * c);
        if (underSqrt < 0) {
            throw new IllegalArgumentException("This trinom has no solution");
        }
        double sqrt = Math.sqrt(underSqrt);
        this.solution1 = round((-b + sqrt) / (2 * a), 3);
        this.solution2 = round((-b - sqrt) / (2 * a), 3);
    }

    /**
     * This method generates {@link Trinom} in the required level.
     * The levels are:
     * <table>
     * <CAPTION><EM>The levels and their definitions</EM></CAPTION>
     * <tr><td>1</td><td>a = 1, factors and solutions are integers.</td></tr>
     * <tr><td>2</td><td>a=1, factors and solutions might be <i>x.5</i> and/or negative.</td></tr>
     * <tr><td>3</td><td>a is integer from 1 to 10, factors and solutions might be <i>x.5</i> and/or negative.</td></tr>
     * </table>
     * @param level the required level to generate the practice at.
     * @return generated practice as required.
     * */
    public static Trinom generateTrinom(int level) {
        Random rnd = new Random();
        double a = 0, b = 0, c = 0;
        if (level == 1) {
            double solution1 = rnd.nextInt(10);
            a = 1;
            b = rnd.nextInt(9) + 1;
            c = -(solution1 * solution1 + solution1 * b);
        } else if (level == 2) {
            double solution1 = rnd.nextInt(10);
            a = 1;
            b = (rnd.nextInt(20) - 10) / 2.0;
            c = -(solution1 * solution1 + solution1 * b);
        } else if (level == 3) {
            double solution1 = (rnd.nextInt(20) - 10);
            a = rnd.nextInt(10);
            b = (rnd.nextInt(20) - 10) / 2.0;
            c = -(a * solution1 * solution1 + solution1 * b);
        }
        return new Trinom(level, a, b, c);
    }

    @Override
    public boolean check(double... solutions) {
        solutions[0] = round(solutions[0], 3);
        solutions[1] = round(solutions[1], 3);
        return (solutions[0] == this.solution1 && solutions[1] == this.solution2) ||
                (solutions[1] == this.solution1 && solutions[0] == this.solution2);
    }
}