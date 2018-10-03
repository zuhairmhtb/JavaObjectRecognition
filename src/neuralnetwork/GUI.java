/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import trainingdata.ThreeLayeredPoint;
import trainingdata.Point;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

/**
 *
 * @author User
 */
public class GUI implements GLEventListener {

    GLProfile glProfile;
    GLCapabilities glCapabilities;
    Animator animator;
    GLCanvas canvas;
    JFrame frame;

    int canvas_width, canvas_height;
    float m, c;
    boolean should_stop;
    private GL2 gl;

    Perceptron p;
    ThreeLayeredPerceptron mlp;
    Point[] training_data;
    float current_i = -1f;
    float current_j = -1f;
    Random r = new Random();
    ArrayList<ThreeLayeredPoint> tds;
    int prev = -1;
    float learning_rate = 0.01f;

    float[] pos_color = {0f, 0f, 1f};
    float[] neg_color = {0f, 1f, 0f};
    int current = 0;
    int training_data_length = 1000;
    boolean new_dataset_per_loop = false;
    boolean repeat_same_data = true;
    boolean isMultiLayered = true;

    WindowListener listener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            e.getWindow().dispose();
            should_stop = true;
        }
    };
    public GUI(Perceptron p, int width, int height, float m, float c){
        
        this.canvas_width = width;
        this.canvas_height = height;
        this.should_stop = false;
        
        this.m = m;
        this.c = c;
        this.p = p;        
        this.isMultiLayered = false;
        training_data = new Point[training_data_length];
        refillData();
        loadOpenGL();
    }
    public GUI(ThreeLayeredPerceptron mlp, int width, int height){
        this.canvas_width = width;
        this.canvas_height = height;
        this.should_stop = false;        
        this.mlp = mlp;
        this.isMultiLayered = true;
        refillData();        
        loadOpenGL();
        
    }
    
    public void loadOpenGL(){
        glProfile = GLProfile.get(GLProfile.GL2);
        glCapabilities = new GLCapabilities(glProfile);
        canvas = new GLCanvas(glCapabilities);
        canvas.addGLEventListener(this);
        canvas.setSize(canvas_width, canvas_height);
        animator = new Animator(canvas);
        animator.start();
        frame = new JFrame("Neural Network Simulation");
        frame.addWindowListener(listener);
        frame.getContentPane().add(canvas);
        frame.setSize(frame.getContentPane().getPreferredSize());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setVisible(true);
    }
    public void refillData(){
        if(isMultiLayered){
            tds = new ArrayList<>();
            for(float i=-1f; i < 1f; i+=0.01f){
                for(float j= -1f; j < 1f; j+=0.01f){
                    if(i != 0 && j != 0)tds.add(new ThreeLayeredPoint(i,j));
                }
            }
        }
        else{
           for(int i=0; i < training_data.length; i++){
               training_data[i] = new Point(m,c);
           }
        }
        
    }
    public void paintAxis() {
        gl.glColor3f(0f, 1f, 0f);
        for (float x = -1f; x < 1f; x += 0.001f) {
            gl.glVertex2f(x, 0);            
        }
        gl.glColor3f(0f, 0f, 1f);
        for (float x = -1f; x < 1f; x += 0.001f) {
            gl.glVertex2f(0, x);            
        }

    }

    public boolean draw(float x, float y, float c1, float c2, float c3) {
        if (gl != null && x >= -frame.getWidth() && x <= frame.getWidth() && y >= -frame.getHeight() && y <= frame.getHeight()) {
            gl.glColor3f(c1, c2, c3);
            gl.glVertex2f(x, y);
            return true;
        }
        return false;
    }

    public void drawLine() {
        float[] line_color = {1f, 1f, 1f};
        for (float x = -1; x < 1; x += 0.001f) {
            float y = (int) m * x + (int) c;
            draw(x, y, line_color[0], line_color[1], line_color[2]);
        }
    }
    public void drawTarget(){
        for(ThreeLayeredPoint point: tds){
            if(point.answer[0] == 1f) draw(point.points[0], point.points[1], 1f, 1f, 1f);
            else if(point.answer[0] == 0f) draw(point.points[0], point.points[1], 0f, 0f, 0f);
        }
    }

    @Override
    public void display(GLAutoDrawable glad) {
        gl = glad.getGL().getGL2();
        if(!isMultiLayered) gl.glClear(GL2.GL_COLOR_BUFFER_BIT);
        //enableProjection(gl);
        gl.glBegin(GL2.GL_POINTS);
        paintAxis();
        
        if (!isMultiLayered) {
            //GUI for training a single layered Perceptron
            drawLine();
            if (new_dataset_per_loop) {
                for (int current = 0; current < training_data_length; current++) {
                    if (!repeat_same_data) {
                        training_data[current] = new Point(m, c);
                    }
                    float[] input = {training_data[current].points[0], training_data[current].points[1], training_data[current].bias};
                    int guessed = p.train(input, training_data[current].answer);
                    System.out.println(current + ". Training neural network for inputs: " + training_data[current].points[0] + ", " + training_data[current].points[1] + "\nTarget: " + training_data[current].answer + "\nGuessed: " + guessed);
                    if (guessed > 0) {
                        draw(training_data[current].points[0], training_data[current].points[1], pos_color[0], pos_color[1], pos_color[2]);
                    } else {
                        draw(training_data[current].points[0], training_data[current].points[1], neg_color[0], neg_color[1], neg_color[2]);
                    }

                }
            } else if (repeat_same_data) {
                for (int i = 0; i < current - 1; i++) {
                    float[] input = {training_data[i].points[0], training_data[i].points[1], training_data[i].bias};
                    int guessed = p.guess(input);
                    if (guessed > 0) {
                        draw(training_data[i].points[0], training_data[i].points[1], pos_color[0], pos_color[1], pos_color[2]);
                    } else {
                        draw(training_data[i].points[0], training_data[i].points[1], neg_color[0], neg_color[1], neg_color[2]);
                    }
                }
                float[] input = {training_data[current].points[0], training_data[current].points[1], training_data[current].bias};
                int guessed = p.train(input, training_data[current].answer);
                System.out.println(current + ". Training neural network for inputs: " + training_data[current].points[0] + ", " + training_data[current].points[1] + "\nTarget: " + training_data[current].answer + "\nGuessed: " + guessed);
                if (guessed > 0) {
                    draw(training_data[current].points[0], training_data[current].points[1], pos_color[0], pos_color[1], pos_color[2]);
                } else {
                    draw(training_data[current].points[0], training_data[current].points[1], neg_color[0], neg_color[1], neg_color[2]);
                }
                current = (current + 1) % training_data_length;
            }

            p.drawLine(gl, frame);
        } else {
            //GUI for training a multilayered Perceptron
            
            int index = r.nextInt(tds.size());
            if(index != prev){
                ThreeLayeredPoint point = tds.get(index);
                float[] output = mlp.feedForward(point.inputs);
                float offset = Math.abs(point.answer[0] - output[0]);                
                if(output[0] > 0.5f) draw(point.points[0]*output[0], point.points[1], pos_color[0], pos_color[1]-offset, pos_color[2]);
                else draw(point.points[0], point.points[1], neg_color[0], neg_color[1], neg_color[2]-offset);
                mlp.train(point.inputs, point.answer);
            }
            prev = index;
            
            
        }

        
        gl.glFlush();
        
        gl.glEnd();

    }

    @Override
    public void reshape(GLAutoDrawable glad, int i, int i1, int i2, int i3) {

    }

    @Override
    public void init(GLAutoDrawable glad) {
        glad.getGL().setSwapInterval(30000);
    }

    @Override
    public void dispose(GLAutoDrawable glad) {

    }

}
