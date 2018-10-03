/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import commontools.ApplicationTools;
import commontools.FileManager;
import commontools.Matrix;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ProgressIndicator;
import neuralnetwork.MultiLayeredPerceptron;
import neuralnetwork.ThreeLayeredPerceptron;
import trainingdata.ImageData;

/**
 *
 * @author User
 */
public class ObjectRecognition {

    
    public MultiLayeredPerceptron mlp; //The Neural Network that performs task

  
   // float learning_rate = 0.08f; //Learning rate for the neural network
    boolean debug = true;
    Random r; //Random number generator
    

    ContentPane root;
    MainView view;
    int current = 0;
    public static int[] accuracy_digit;
    public static int[] accuracy_digit_total;
    public static boolean reading = false;
    public static float[] last_accuracy;
    public static float[] confidence_value;
    public static int[] confidence_total;
    public Configuration conf;
    

    public ObjectRecognition(boolean debug, ContentPane root, MainView view, Configuration conf) {
        this.conf = conf;
        this.view = view;        
        this.root = root;
        
        accuracy_digit = new int[conf.output_nodes];
        accuracy_digit_total = new int[conf.output_nodes];
        last_accuracy = new float[conf.output_nodes];
        confidence_value = new float[conf.output_nodes];
        confidence_total = new int[conf.output_nodes];
        
        if (!new File(conf.mlp_weight_file_dir).exists()) {
            new File(conf.mlp_weight_file_dir).mkdirs();
        }

        
        if (!new File(conf.mlp_bias_file_dir).exists()) {
            new File(conf.mlp_bias_file_dir).mkdirs();
        }

        
        if (!new File(conf.training_set_directory).exists()) {
            new File(conf.training_set_directory).mkdirs();
        }       

        this.debug = debug;        
        view.inode.setText("" + conf.input_nodes);      
        view.onode.setText("" + conf.output_nodes);
        r = new Random();

        mlp = new MultiLayeredPerceptron(conf.input_nodes, conf.output_nodes, conf.hidden_layers, conf.hidden_nodes);
        
        mlp.print = false;
//        mlp.learning_rate = this.learning_rate;

        //configureMLP();        
        //prepareDataset();
    }

    public void print(String text) {
        if (debug) {
          //  root.displayInOutput(text);
            System.out.println(text);
        }
    }

    public void setLearningRate(float learning_rate) {
       // this.learning_rate = learning_rate;
    }

    public void configureMLP() {
        try {
            for (int i = 0; i < mlp.layers.length; i++) {
                String filename = i + "." + conf.extension;
                print("Loading weight file of layer " + (i+1) + " from " + conf.mlp_weight_file_dir + filename);
                if (new File(conf.mlp_weight_file_dir + filename).exists()) {
                    String weights_str = FileManager.readTextFrom(conf.mlp_weight_file_dir + filename);
                    Matrix weights_mat = ApplicationTools.textToMatrix(weights_str);
                    if (weights_mat.rows == mlp.layers[i].weights.rows && weights_mat.columns == mlp.layers[i].weights.columns) {
                        mlp.layers[i].weights.data = weights_mat.data;
                    } else {
                        print("Could not convert weights of layer " + (i + 1) + " from string to matrix");
                        mlp.layers[i].weights.randomize();
                    }
                } else {
                    print("Could not find weight file for layer " + (i + 1));
                    mlp.layers[i].biases.randomize();
                }
                print("Loading bias file of layer " + (i+1) + " from " + conf.mlp_bias_file_dir + filename);
                if (new File(conf.mlp_bias_file_dir + filename).exists()) {
                    String biases_str = FileManager.readTextFrom(conf.mlp_bias_file_dir + filename);
                    Matrix bias_mat = ApplicationTools.textToMatrix(biases_str);
                    if (bias_mat.rows == mlp.layers[i].biases.rows && bias_mat.columns == mlp.layers[i].biases.columns) {
                        mlp.layers[i].biases.data = bias_mat.data;
                    } else {

                        print("Could not convert bias of layer " + (i + 1) + " from string to matrix");

                    }
                } else {
                    print("Could not find bias file for layer " + (i + 1));

                }

            }
            
            
        } catch (Exception e) {
            print(e.getMessage());
        }

    }

    //remove the first item
    //reduce XValue of all items by 1
    //add the new item
    public void reduceSeriesXValue(XYChart.Series series, float newValue) {
        if (series.getData().size() > 0) {
            series.getData().remove(0);
            int numOfPoint = series.getData().size();
            for (int i = 0; i < numOfPoint; i++) {
                //reduce XValue
                XYChart.Data<Number, Number> data
                        = (XYChart.Data<Number, Number>) series.getData().get(i);
                int x = (int) data.getXValue();
                data.setXValue(x - 1);
            }

            series.getData().add(new XYChart.Data(numOfPoint, newValue));
        } else {
            series.getData().add(new XYChart.Data(0, newValue));
        }

    }

    

    public void saveMLPWeights() throws Exception {
        System.out.println("Saving weights for " + mlp.layers.length + " layers");
        for (int i = 0; i < mlp.layers.length; i++) {
            String weights_str = ApplicationTools.matrixToText(mlp.layers[i].weights);
            if (weights_str.length() > 0) {                
                String dir = conf.mlp_weight_file_dir + i + "." + conf.extension;
                System.out.println("Saving to " + dir);
                System.out.println("Saving weight matrix of layer " + (i + 1) + " to " + dir);
                FileManager.writeTextTo(dir, weights_str);
            } else {
                System.out.println("Error: could not convert weight matrix of layer " + (i + 1) + " to string");
                throw new Exception("Error: could not convert weight matrix of layer " + (i + 1) + " to string");
            }
        }
    }

    public void saveMLPBiases() throws Exception {
        System.out.println("Saving biases for " + mlp.layers.length + " layers");
        for (int i = 0; i < mlp.layers.length; i++) {
            String biases_str = ApplicationTools.matrixToText(mlp.layers[i].biases);
            if (biases_str.length() > 0) {
                String dir = conf.mlp_bias_file_dir + i + "." + conf.extension;
                System.out.println("Saving bias matrix of layer " + (i + 1) + " to " + dir);
                FileManager.writeTextTo(dir, biases_str);
            } else {
                throw new Exception("Error: could not convert bias matrix of layer " + (i + 1) + " to string");
            }
        }
    }

    public void calculateDigitAccuracy(int input, float[] inputs) {
        float[] output = mlp.feedForward(inputs);
        int max_index = -1;
        float max_output = -5;
        for (int i = 0; i < output.length; i++) {
            if (output[i] > max_output) {
                max_output = output[i];
                max_index = i;
            }
        }
        if (max_index >= 0) {
            System.out.println("Predicted output for input " + input + " is " + max_index + " and accuracy is " + max_output);
            if (input == max_index) {
                accuracy_digit[input]++;
                System.out.println("Current total correct anwser for input " + input + " is " + accuracy_digit[input]);
                confidence_value[input]+=max_output;
                System.out.println("Current total confidence for input " + input + " is " + confidence_value[input]);
                
                
            }
            confidence_total[input]++;
            System.out.println("Current total confidence for input " + input + " is " + accuracy_digit_total[input]);
            accuracy_digit_total[input]++;
            System.out.println("Current total training for input " + input + " is " + accuracy_digit_total[input]);

        }
        current++;
        
        //Calculate accuracy for each digit
        for (int i = 0; i < accuracy_digit.length; i++) {
            int index = i;
            int correct = accuracy_digit[i];
            System.out.println("Total accurate answer for input " + index + " is " + correct);
            int total = accuracy_digit_total[i];
            System.out.println("Total trained for input " + index + " is " + total);

            if (total > 0) {
                last_accuracy[index] = (float) correct / total;
            }
            System.out.println("Total accuracy for digit " + index + " is " + (100 * last_accuracy[index]));

        }
        
        

    }

    public void train() throws InterruptedException {
        int index = -1;
        ImageData data = null;
        for (int j = 0; j < conf.batch_size; j++) {
            index = r.nextInt(conf.dataset.size());
            data = conf.dataset.get(index);
            int input = Integer.parseInt(data.target);
            // print("Training neural network for input " + input);

            mlp.train(data.inputs, data.targets);
            if (!reading) {
                calculateDigitAccuracy(input, data.inputs);
            }
        }
    }
}
