package com.example.mathpractice.math;

import java.util.Random;

public class MulTable extends AbstractPractice{

    public double solution;
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

    public static MulTable generateMulTable(int level){
        Random rnd = new Random();
        double a = 0, b = 0;
        if (level == 1) {
            a = rnd.nextInt(10) + 1;
            b = rnd.nextInt(10) + 1;
        } else if (level == 2) {
            a = (rnd.nextInt(20) + 1) / 2.0;
            b = (rnd.nextInt(20) + 1) / 2.0;
        } else if (level == 3) {
            a = ((rnd.nextInt(100) + 1) / 10.0) % 10;
            b = ((rnd.nextInt(100) + 1) / 10.0) % 10;
        }
        return new MulTable(level, a, b);
    }
}