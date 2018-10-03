/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import commontools.LearningRate;
import commontools.Matrix;
import commontools.Matrix.MyFloatInterface;

/**
 *
 * @author User
 */
public class MultiLayeredPerceptron {
    public int input_nodes, output_nodes, hidden_layers, hn_per_layer;
    public Layer[] layers;
    //public float learning_rate = 1f;
    
    public Matrix[] outputs, output_errors, gradients, delta_w;
    class Sigmoid implements MyFloatInterface{

        @Override
        public float execute(float x, int i, int j) {
            float answer = (float) ((1/(1 + Math.pow(Math.E, (-1*x)))));
            return answer;
        }
    
    }
    class DeSigmoid implements MyFloatInterface{

        @Override
        public float execute(float x, int i, int j) {
            return x * (1 - x);
        }
        
    }
    Sigmoid sigmoid_function;
    DeSigmoid desigmoid_function;
    
    public boolean print = true;
    
    public MultiLayeredPerceptron(int input_nodes, int output_nodes, int hidden_layers, int hidden_nodes_per_layer){
        
        this.input_nodes = input_nodes;
        this.output_nodes = output_nodes;
        this.hidden_layers = hidden_layers;
        this.hn_per_layer = hidden_nodes_per_layer;
        layers = new Layer[hidden_layers+1]; //Including input-hidden layer
        sigmoid_function = new Sigmoid();
        desigmoid_function = new DeSigmoid();
        createLayers();
    }
    public void print(String text){
        if(print) System.out.println(text);
    }
    public void createLayers(){
        int output_nodes;
        if(layers.length == 1) output_nodes = this.output_nodes; //Create connection with output nodes
        else output_nodes = this.hn_per_layer;  //Create connection with hidden nodes
        layers[0] = new Layer(input_nodes, output_nodes); //Create First Layer
        for(int i=1; i < layers.length; i++){
            if(i < layers.length - 1) output_nodes = this.hn_per_layer; //Create connection with hidden nodes
            else output_nodes = this.output_nodes; //Create connection for hidden-output layer in last layer
            layers[i] = new Layer(layers[i-1].output_nodes, output_nodes); //Creating connections within layers
        }
    }
    public float[] feedForward(float[] input){
        Matrix inputs = Matrix.fromArray(input);
        print("Converted input array to matrix:");
        inputs.print(print);
        
        Matrix outputs = null;
        for(int i=0; i < layers.length; i++){
            System.out.println("Multiplying weight of dimension " + layers[i].weights.rows + "x" + layers[i].weights.columns
                    + " by input of dimension " + inputs.rows + "x" + inputs.columns);
            outputs = Matrix.rowcolumnMultiply(layers[i].weights, inputs); //Multiply input with weight to get output
            System.out.println("Multiplied weight matrix of layer " + (i+1) + " (length: " + layers[i].weights.rows + "x" + layers[i].weights.columns + ") with inputs of length " + inputs.rows + ":");
            outputs.print(print);
            
            outputs.add(layers[i].biases); //Add bias to the output
            print("Added biases to the output matrix: ");
            outputs.print(print);
            
            outputs.execute(sigmoid_function); //Applying activation function to get actual output
            print("Executed sigmoid fuction to calculate activation: ");
            outputs.print(print);
            
            inputs = outputs;
            
        }
        print("Found output of length " + outputs.rows + " rows and " + outputs.columns + " columns");
        outputs.print(print);
        
        return Matrix.toArray(outputs);
    }
    public void train(float[] input, float[] target){
        Matrix inputs = Matrix.fromArray(input);
        Matrix targets = Matrix.fromArray(target);
        
        //Calculate feed forward output
        print("Converted input array to matrix:");
        inputs.print(print);
        
        outputs = new Matrix[layers.length];
        for(int i=0; i < layers.length; i++){
            outputs[i] = Matrix.rowcolumnMultiply(layers[i].weights, inputs); //Multiply input with weight to get output
            print("Multiplied weight matrix of layer " + (i+1) + " with inputs of length " + inputs.rows + ":");
            outputs[i].print(print);
            
            outputs[i].add(layers[i].biases); //Add bias to the output
            print("Added biases to the output matrix: ");
            outputs[i].print(print);
            
            outputs[i].execute(sigmoid_function); //Applying activation function to get actual output
            print("Executed sigmoid fuction to calculate activation: ");
            outputs[i].print(print);
            
            inputs = outputs[i];
            
        }
        inputs = Matrix.fromArray(input);
        print("Found output of length " + outputs[outputs.length -1].rows + " rows and " + outputs[outputs.length -1].columns + " columns");
        outputs[outputs.length -1].print(print);
        
        //-----Calculate Output Errors----------------
        output_errors = new Matrix[outputs.length];
        output_errors[output_errors.length - 1] = Matrix.subtract(targets, outputs[outputs.length - 1]); //Error = Target - Output
        print("Output error calculated from target:");
        output_errors[output_errors.length -1].print(print);
        
        for(int i=(output_errors.length - 2); i >=0; i--){
            Matrix weight_transposed = Matrix.transpose(layers[i+1].weights);
            output_errors[i] = Matrix.rowcolumnMultiply(weight_transposed, output_errors[i+1]); //Calculate output error for each layer
            print("Calculated output errors for layer " + (i+1) + ":");
            output_errors[i].print(print);
        }
        
        //-------Calculate Gradients-------------
        gradients = new Matrix[outputs.length];
        for(int i=0; i < gradients.length; i++){
            Matrix desigmoid = Matrix.execute(outputs[i], desigmoid_function); //Desigmoid of output
            print("Desigmoid of output for layer " + (i+1) + " calculated:");
            desigmoid.print(print);
            desigmoid.elementwiseMultiply(output_errors[i]); //Multiply by output error for the layer
            print("Performed element wise multiplication with output error for the layer: ");
            desigmoid.print(print);
            gradients[i] = Matrix.multiply(desigmoid, LearningRate.rate); //Calculate gradient, G = learning rate * output error * Desigmoid(Output)
            print("Calculated gradient for layer " + (i+1) + " by multiplying it with the learning rate which is " + LearningRate.rate);
            gradients[i].print(print);
        }
        
        //-------Calculate change in weight required for each layer-----------------
        delta_w = new Matrix[outputs.length];
        for(int i=0; i < delta_w.length; i++){
            
            if(i==0){
                Matrix.transpose(inputs).print(print);
                delta_w[i] = Matrix.rowcolumnMultiply(gradients[i], Matrix.transpose(inputs));
            }
            else{
                Matrix.transpose(outputs[i-1]).print(print);
                delta_w[i] = Matrix.rowcolumnMultiply(gradients[i], Matrix.transpose(outputs[i-1]));
            }
            print("Calculated adusted weight for layer " + (i+1) + ":");
            delta_w[i].print(print);    
            layers[i].weights =Matrix.add(layers[i].weights, delta_w[i]);
            print("New weigths of layer " + (i+1) + " after adjustmnet:");
            layers[i].weights.print(print);
            layers[i].biases = Matrix.add(layers[i].biases, gradients[i]);
            print("New biases of layer " + (i+1) + " after adjustment: ");
            layers[i].biases.print(print);
        }
        
        
        
        
    }
}
