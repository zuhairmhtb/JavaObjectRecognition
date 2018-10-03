/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import digitrecognition.GUI;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.Animator;
import commontools.ApplicationTools;
import commontools.CannyEdgeDetector;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.WindowConstants;
import objectrecognition.ObjectRecognitionInterface;
import trainingdata.ThreeLayeredPoint;


/**
 *
 * @author User
 */
public class NeuralNetwork{
    
    public static void demoPerceptron(){
        int input_nodes = 2; //X and Y coordinates where -1<=X<=1 and -1<=Y<=1
        float learning_rate = 0.01f;
        //Detects whether the coordinate is above or below the specifically trained line
        Perceptron p = new Perceptron(learning_rate);
        float slope = 3; //Slope of the equation of the line for which the perceptron has to be trained
        float intercept = 1f; //Intercept of the equation of the line for which the perceptron has to be trained
        int display_width = 600;
        int display_height = 600;
        
        
        neuralnetwork.GUI gui = new neuralnetwork.GUI(p, display_width, display_height, slope, intercept);
    }
    public static void demoThreeLayeredPerceptron(){
        int input_nodes = 2; //X and Y where x and y equals either 0 or 1
        int hidden_nodes = 2; //Detects output for XOR operation of X and Y
        int output_nodes = 1;
        float learning_rate = 0.08f;
        int display_width = 600;
        int display_height = 600;
        boolean debug_print = false;
        ThreeLayeredPerceptron mlp = new ThreeLayeredPerceptron(input_nodes, hidden_nodes, output_nodes, learning_rate, debug_print);
        neuralnetwork.GUI gui = new neuralnetwork.GUI(mlp, display_width, display_height);
    }
    public static void demoMultiLayeredPerceptron(){
        int input = 2;
        int hl = 3;
        int output = 1;
        int hnpl = 3;
        MultiLayeredPerceptron mlp = new MultiLayeredPerceptron(input, output, hl, hnpl);
        mlp.print = false;
        System.out.println("Created Layers: " + mlp.layers.length);
        System.out.println("Details per layer:");
        for(int i=0; i < mlp.layers.length; i++){
            System.out.println("  Layer " + (i+1) + " information: ");
            System.out.println("Input nodes: " + mlp.layers[i].weights.columns);
            System.out.println("Output nodes: " + mlp.layers[i].weights.rows);
            System.out.println("Weight: ");
            mlp.layers[i].weights.print(true);
            System.out.println("Bias:");
            mlp.layers[i].biases.print(true);
            
        }
        
        ThreeLayeredPoint[] dataset = new ThreeLayeredPoint[10000];
        for(int i=0; i < dataset.length; i++){
            dataset[i] = new ThreeLayeredPoint();
        }
        for(int i=0; i < 100000; i++){
            int index = new Random().nextInt(dataset.length);
            System.out.println("USing dataset no. " + index);
            System.out.println("Output berfore training for input" + dataset[index].inputs[0] + " and " + dataset[index].inputs[1] + "and target " + dataset[index].answer[0] + ":");
            float[] guess = mlp.feedForward(dataset[index].inputs);
            for(int j=0; j < guess.length; j++){
                System.out.print(guess[j] + ", ");
            }
            System.out.println();
            System.out.println("Output after training: ");
            mlp.train(dataset[index].inputs, dataset[index].answer);
            guess = mlp.feedForward(dataset[index].inputs);
            for(int j=0; j < guess.length; j++){
                System.out.print(guess[j] + ", ");
            }
            System.out.println();
        }
        
    }
    
    public static void demoDigitRecognition(){
        String dir = "D:\\thesis\\png_images\\train\\";        
        String format = ".png";
        boolean use_mlp = true;
        boolean debug = false;
        int width = 28;
        int height = 28;
        int hidden_nodes = 30;
        int hidden_layers = 2;
        float learning_rate = 0.08f;
        Thread t = new Thread(new GUI(width, height, hidden_nodes, hidden_layers, learning_rate, dir, format, use_mlp, debug));
        t.start();
    }
    public static void demoObjectRecognition(){
        ObjectRecognitionInterface ori = new ObjectRecognitionInterface();
        ori.run();
    }
    public static void demoEdgeDetection() throws Exception{
        String root =  "E:\\Fall17\\CSE422_AI_3\\NeuralNetwork\\";
       String base = root + "face\\td\\";
       String out = root + "shape\\td\\";
       String main_format = "png";
       String format = "." + main_format;
        int i=0;
       while(new File(base + i + "\\").exists()){
           System.out.println("Loading image from " + base + i + "\\");
           int j=1;
           while(new File(base + i + "\\" + j + format).exists()){
              // System.out.println("Loading image " + base + i + "\\" + j + format + " to detect edges");
               BufferedImage main = ImageIO.read(new File(base + i + "\\" + j + format));
               BufferedImage edges = ApplicationTools.getEdges(main);
              // System.out.println("Detected edges with dimension " + edges.getWidth() + "x" + edges.getHeight());
               for(int p=0; p < main.getWidth(); p++){
                   for(int q=0; q < main.getHeight(); q++){
                       int[] rgb = ApplicationTools.getRGB(edges.getRGB(p, q));
                       if(rgb[0] != 0 || rgb[1] != 0 || rgb[2] != 0){
                       //    System.out.println("The point is an edge");
                           Color c = new Color(0, 0, 0);
                           main.setRGB(p, q, c.getRGB());
                       }
                       
                   }
               }
               
              // System.out.println("Saving the edges to " + out + i + "\\" + j + format);
               if(!new File(out + i + "\\").exists()) new File(out + i + "\\").mkdirs();
               ImageIO.write(edges, main_format, new File(out + i + "\\" + j + format));
                
               
               j++;
           }
           i++;
       }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        try {
            // TODO code application logic here
               demoObjectRecognition();
                //demoEdgeDetection();
            //  objectrecognition.Connection c =new objectrecognition.Connection(250, 300);
            //  c.setColor(210, 15, Color.CORAL);
        } catch (Exception ex) {
            Logger.getLogger(NeuralNetwork.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
}
