/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package digitrecognition;

import commontools.ApplicationTools;
import commontools.FileManager;
import commontools.LearningRate;
import commontools.Matrix;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import neuralnetwork.MultiLayeredPerceptron;
import neuralnetwork.ThreeLayeredPerceptron;
import trainingdata.ImageData;

/**
 *
 * @author User
 */
public class DigitRecognition {
    public ThreeLayeredPerceptron tlp;
    public MultiLayeredPerceptron mlp;

    int width, height, input_nodes, hidden_nodes, output_nodes, hidden_layers;
    float learning_rate = 0.08f;
    boolean debug = true;

    String extension = "nn";
    String base_dir = "E:\\Fall17\\CSE422_AI_3\\NeuralNetwork\\";
    String weight_file_dir = base_dir + "wab\\";
    String mlp_weight_and_biases_file_dir = base_dir + "wab_mlp\\";
    String mlp_weight_file_dir = mlp_weight_and_biases_file_dir + "w\\";
    String mlp_bias_file_dir = mlp_weight_and_biases_file_dir + "b\\";
    
    String input_hidden_weight_file = weight_file_dir + "ihw." + extension;
    String input_hidden_bias_file = weight_file_dir + "ihb." + extension;
    String hidden_output_weight_file = weight_file_dir + "how." + extension;
    String hidden_output_bias_file = weight_file_dir + "hob." + extension;
    String training_set_directory = base_dir + "td\\";
    String training_image_format = "png";

    int batch_size = 100;
    Random r;
    ArrayList<ImageData> dataset;
    String defualt_image_name = "0\\1";

    private boolean should_stop = false;
    private boolean use_mlp = true;
    public DigitConfiguration conf;
    public DigitRecognition(int image_width, int image_height, int hidden_nodes, int hidden_layer, boolean use_mlp, boolean debug, DigitConfiguration conf) {
        this.debug = debug;
        this.conf = conf;
        this.use_mlp = use_mlp;
        width = image_width;
        height = image_height;
        input_nodes = width * height;
        this.hidden_nodes = hidden_nodes;
        this.hidden_layers = hidden_layer;
        output_nodes = 10;
        
        r = new Random();
        tlp = new ThreeLayeredPerceptron(input_nodes, hidden_nodes, output_nodes, learning_rate, debug);
        mlp = new MultiLayeredPerceptron(input_nodes, output_nodes, hidden_layers, hidden_nodes);
        mlp.print = false;
        LearningRate.rate = this.learning_rate;
        configure();        
        prepareDataset();        
        
        
    }
    public void setLearningRate(float learning_rate) {
        this.learning_rate = learning_rate;
    }
    private void configureMLP(){
        try{
            for(int i=0; i < mlp.layers.length; i++){
                String filename = i + "." + extension;
                String weights_str = FileManager.readTextFrom(mlp_weight_file_dir + filename);
                Matrix weights_mat = ApplicationTools.textToMatrix(weights_str);
                if(weights_mat.rows == mlp.layers[i].weights.rows && weights_mat.columns == mlp.layers[i].weights.columns){
                    mlp.layers[i].weights.data = weights_mat.data;
                }
                else {
                    System.out.println("Could not convert weights of layer "+(i+1)+" from string to matrix");
                }
                String biases_str = FileManager.readTextFrom(mlp_bias_file_dir + filename);
                Matrix bias_mat = ApplicationTools.textToMatrix(biases_str);
                if(bias_mat.rows == mlp.layers[i].biases.rows && bias_mat.columns == mlp.layers[i].biases.columns){
                    mlp.layers[i].biases.data = bias_mat.data;
                }
                else {
                    System.out.println("Could not convert bias of layer "+(i+1)+" from string to matrix");
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
    }
    private void configureTLP(){
        try {
            //Load weights and biases from configuration files
            
            String weights_ih_str = FileManager.readTextFrom(input_hidden_weight_file);
            Matrix weights_ih_mat = ApplicationTools.textToMatrix(weights_ih_str);
            if (weights_ih_mat.rows == tlp.weights_ih.rows && weights_ih_mat.columns == tlp.weights_ih.columns) {
                tlp.weights_ih.data = weights_ih_mat.data;
            } else {
                System.out.println("Could not convert input hidden weights from string to matrix");
            }

            String weights_ho_str = FileManager.readTextFrom(hidden_output_weight_file);
            Matrix weights_ho_mat = ApplicationTools.textToMatrix(weights_ho_str);
            if (weights_ho_mat.rows == tlp.weights_ho.rows && weights_ho_mat.columns == tlp.weights_ho.columns) {
                tlp.weights_ho.data = weights_ho_mat.data;
            } else {
                System.out.println("Could not convert hidden outputs weights from string to matrix");
            }

            String biases_ih_str = FileManager.readTextFrom(input_hidden_bias_file);
            Matrix biases_ih_mat = ApplicationTools.textToMatrix(biases_ih_str);
            if (biases_ih_mat.rows == tlp.bias_hidden.rows && biases_ih_mat.columns == tlp.bias_hidden.columns) {
                tlp.bias_hidden.data = biases_ih_mat.data;
            } else {
                System.out.println("Could not convert input hidden biases from string to matrix");
            }

            String biases_ho_str = FileManager.readTextFrom(hidden_output_bias_file);
            Matrix biases_ho_mat = ApplicationTools.textToMatrix(biases_ho_str);
            if (biases_ho_mat.rows == tlp.bias_output.rows && biases_ho_mat.columns == tlp.bias_output.columns) {
                tlp.bias_output.data = biases_ho_mat.data;
            } else {
                System.out.println("Could not convert hidden output biases from string to matrix");
            }

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    private void configure() {
        if(!use_mlp) configureTLP();
        else configureMLP();
    }
    public void prepareDataset() {
        dataset = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int j = 1;
            while (j < 100) {
                String filename = "" + j;
                //System.out.println("Adding " + training_set_directory + i + "\\" + filename + "." + training_image_format + " to dataset");
                if (new File(training_set_directory + i + "\\" + filename + "." + training_image_format).exists()) {
                    dataset.add(new ImageData(training_set_directory + i + "\\", filename, "." + training_image_format, "" + i, conf));

                }
                // else break;
                j++;
            }
        }
        System.out.println("Total dataset loaded in digitrecognizer: " + dataset.size());
      //  batch_size = dataset.size();
    }
    public void saveMLPWeights() throws Exception{
        for(int i=0; i < mlp.layers.length; i++){
            String weights_str = ApplicationTools.matrixToText(mlp.layers[i].weights);
            if(weights_str.length() > 0){
                String dir = mlp_weight_file_dir + i + "." + extension;
                System.out.println("Saving weight matrix of layer " + (i+1) + " to " + dir);
                FileManager.writeTextTo(dir, weights_str);
            }
            else throw new Exception("Error: could not convert weight matrix of layer " + (i+1) + " to string");
        }
    }
    public void saveTLPWeights() throws Exception{
        String weights_ih_str = ApplicationTools.matrixToText(tlp.weights_ih);
        if (weights_ih_str.length() > 0) {
            System.out.println("Saving weight matrix of input-hidden layer to " + input_hidden_weight_file);
            FileManager.writeTextTo(input_hidden_weight_file, weights_ih_str);
        } else {
            throw new Exception("Error: could not convert input hidden weight matrix to string");
        }

        String weights_ho_str = ApplicationTools.matrixToText(tlp.weights_ho);
        if (weights_ho_str.length() > 0) {
            System.out.println("Saving weight matrix of hidden-output layer to " + hidden_output_weight_file);
            FileManager.writeTextTo(hidden_output_weight_file, weights_ho_str);
        } else {
            throw new Exception("Error: could not convert hidden output weight matrix to string");
        }
    }
    public void saveWeights() throws Exception {
        if(!use_mlp) saveTLPWeights();
        else saveMLPWeights();
        
    }
    public void saveMLPBiases() throws Exception{
        for(int i=0; i < mlp.layers.length; i++){
            String biases_str = ApplicationTools.matrixToText(mlp.layers[i].biases);
            if(biases_str.length() > 0){
                String dir = mlp_bias_file_dir + i + "." + extension;
                System.out.println("Saving bias matrix of layer " + (i+1) + " to " + dir);
                FileManager.writeTextTo(dir, biases_str);
            }
            else throw new Exception("Error: could not convert bias matrix of layer " + (i+1) + " to string");
        }
    }
    public void saveTLPBiases() throws Exception{
        String bias_ih_str = ApplicationTools.matrixToText(tlp.bias_hidden);
        if (bias_ih_str.length() > 0) {
            System.out.println("Saving bias matrix of input-hidden layer to " + input_hidden_bias_file);
            FileManager.writeTextTo(input_hidden_bias_file, bias_ih_str);
        } else {
            throw new Exception("Error: could not convert input hidden bias matrix to string");
        }

        String bias_ho_str = ApplicationTools.matrixToText(tlp.bias_output);
        if (bias_ho_str.length() > 0) {
            System.out.println("Saving bias matrix of hidden-output layer to " + hidden_output_bias_file);
            FileManager.writeTextTo(hidden_output_bias_file, bias_ho_str);
        } else {
            throw new Exception("Error: could not convert hidden output bias matrix to string");
        }
    }
    public void saveBiases() throws Exception {
        if(!use_mlp) saveTLPBiases();
        else saveMLPBiases();
    }
    
    public void train(){
        int index = -1;
        ImageData data = null;         
        for(int j=0; j < batch_size; j++){
            index = r.nextInt(dataset.size());
            data = dataset.get(index);
            if(!use_mlp) tlp.train(data.inputs, data.targets);
            else mlp.train(data.inputs, data.targets);
                
        }
    }
    
    
}
