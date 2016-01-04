package com.hikobe8.lockviewdemo;

/**
 * Created by Soda on 2016/1/4.
 */
public class Point {
    public static final int NORMAL = 1;
    public static final int SELECTED = 2;
    public static final int ERROR = 3;
    float x;
    float y;
    int state;
    public Point(float x, float y) {
        this.x = x;
        this.y = y;
        this.state = NORMAL;
    }
    public double distance(float dx, float dy) {
        return Math.sqrt(Math.pow(dx - this.x , 2) + Math.pow(dy - this.y, 2));
    }
}
