package neuralnetwork;

import com.jogamp.opengl.GL2;
import java.util.Random;
import javax.swing.JFrame;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author User
 */
public class Perceptron {
    float[] weights;
    float learning_rate;
    
    public Perceptron(float learning_rate){
        this.learning_rate = learning_rate;
        weights = new float[3];
        Random r = new Random();
        for(int i=0; i < 3; i++){
          int sign = 1;
          if(r.nextInt()%2 != 0) sign = -1;  
          weights[i] = r.nextFloat() * sign;
        } 
    }
    private int activation_function(float sum){
        if(sum > 0) return 1;
        return -1;
    }
    
    public int guess(float[] inputs){
        float sum = 0f;
        for(int i=0; i < weights.length; i++){
            sum+=weights[i] * inputs[i];
        }
        System.out.println("SUM: " + sum);
        
        return activation_function(sum);
    }
    public void drawLine(GL2 gl, JFrame frame){
        int current = 0;        
        for(float x=-1; x < 1; x+=0.001f){
            gl.glColor3f(0.4f, 0.8f, 0.6f);
            float y = (((-1)* (weights[2]/weights[1])) + ((-1) * (weights[0]/weights[1]) * x));
            if(y >= (-1 * frame.getHeight()) && y <= frame.getHeight()){
                gl.glVertex2f(x, y);
                
                current++;
            }
        }
    }
    public void drawLine(GL2 gl, JFrame frame, int bias){
        int current = 0;
        
        for(int x=(-1)*frame.getWidth(); x < frame.getWidth(); x++){
            gl.glColor3f(0.4f, 0.8f, 0.6f);
            int y = (int)(((-1)* (weights[2]/weights[1]) * bias) + ((-1) * (weights[0]/weights[1]) * x));
            if(y >= (-1 * frame.getHeight()) && y <= frame.getHeight()){
                gl.glVertex2i(x, y);
                
                current++;
            }
        }
    }
    public int train(float[] inputs, int desired_output){
        System.out.print("Current weight: ");
        for(int i=0; i < weights.length; i++){
            System.out.print(weights[i]);
            if(i != weights.length - 1) System.out.print(", ");
        }
        System.out.println();
        int guessed_output = guess(inputs);
        int error = desired_output - guessed_output;
        System.out.println("ERROR: " + error);
        for(int i=0; i < weights.length; i++){
            weights[i] += error * inputs[i] * learning_rate;
        }
        System.out.print("Adjusted weight: ");
        for(int i=0; i < weights.length; i++){
            System.out.print(weights[i]);
            if(i != weights.length - 1) System.out.print(", ");
        }
        return guessed_output;
    }
    
}
