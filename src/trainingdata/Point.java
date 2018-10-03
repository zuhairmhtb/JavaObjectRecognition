/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainingdata;

import java.util.Random;

/**
 *
 * @author User
 */
public class Point {
    public float[] points;
    public float bias;
    public int answer;
    public float m;
    public float c;
    public Point(float m, float c){
        this.m = m;
        this.c = c;
        this.bias = 1;
        points = new float[2];
        Random r = new Random();
        int sign = -1;
        if(r.nextInt()%2 == 0) sign = 1;
        points[0] = r.nextFloat() * sign;
        sign = -1;
        if(r.nextInt()%2 == 0) sign = 1;
        points[1] = r.nextFloat() * sign;
        calculateAnswer();
        
    }
    public float f(float x){
        return m*x+c;
    }
    private void calculateAnswer(){
        if(points[1] > f(points[0])) answer = 1;
        else answer = -1;
    }
}
