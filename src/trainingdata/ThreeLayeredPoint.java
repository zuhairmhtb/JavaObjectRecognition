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
public class ThreeLayeredPoint {
    public float[] points;
    public float[] answer;
    public float[] inputs;
    public ThreeLayeredPoint(){
        points = new float[2];
        inputs = new float[2];
        Random r = new Random();
        int sign = -1;
        if(r.nextInt()%2 == 0) sign = 1;
        points[0] = r.nextFloat() * sign;
        sign = -1;
        if(r.nextInt()%2 == 0) sign = 1;
        points[1] = r.nextFloat() * sign;
        answer = new float[1];
        calculateAnswer();
    }
    public ThreeLayeredPoint(float x, float y){
        points = new float[2];
        inputs = new float[2];
        points[0] = x;
        points[1] = y;
        answer = new float[1];
        calculateAnswer();
    }
    public void calculateAnswer(){
        if(points[0] < 0 && points[1] > 0){
            answer[0] = 0f;
            inputs[0] = 0f;            
            inputs[1] = 0f;
            
            
        }
        else if(points[0] > 0 && points[1] > 0){
            answer[0] = 1f;
            inputs[0] = 0f;
            inputs[1] = 1f;
        }
        else if(points[0] < 0 && points[1] < 0){
            answer[0] = 1f;
            inputs[0] = 1f;
            inputs[1] = 0f;            
        }
        else if(points[0] > 0 && points[1] < 0){
            answer[0] = 0f;
            inputs[0] = 1f;
            inputs[1] = 1f;
        }
        
    }
}
