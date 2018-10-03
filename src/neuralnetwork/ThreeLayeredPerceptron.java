/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import commontools.Matrix;
import commontools.Matrix.MyFloatInterface;

/**
 *
 * @author User
 */
public class ThreeLayeredPerceptron {
    int input_nodes;
    int hidden_nodes;
    int output_nodes;
    float learning_rate = 0.05f;
    
    public Matrix weights_ih, weights_ho, bias_hidden, bias_output;
    
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
    
    boolean print = true;
    public ThreeLayeredPerceptron(int input, int hidden, int output, float learning_rate, boolean print){        
        this.print = print;
        this.input_nodes = input;
        this.hidden_nodes = hidden;
        this.output_nodes = output;
        this.learning_rate = learning_rate;
        
        weights_ih = new Matrix(hidden_nodes, input_nodes);
        weights_ho = new Matrix(output_nodes, hidden_nodes);
        print("Inititializing Weight matrix for connection input-hidden:");
        weights_ih.randomize();
        
        weights_ih.print(print);
        print("Inititializing Weight matrix for connection hidden-output:");
        weights_ho.randomize();
       
        weights_ho.print(print);
        
        bias_hidden = new Matrix(hidden_nodes, 1);
        print("Inititializing bias matrix for hidden layer:");
        bias_hidden.randomize();
        
        bias_hidden.print(print);
        bias_output = new Matrix(output_nodes, 1);
        print("Inititializing bias matrix for output layer:");
        bias_output.randomize();
        
        bias_output.print(print);        
        sigmoid_function = new Sigmoid();
        desigmoid_function = new DeSigmoid();
        
        
    }
    public void print(String text){
        if(print) System.out.println(text);
    }
    public boolean setIhWeights(float[][] weights, int rows, int columns){
        if(rows == weights_ih.rows && columns == weights_ih.columns){
            weights_ih.data = weights;
            return true;
        }
        return false;
    }
    
    public float[] feedForward(float[] input){
        print("Calculating feed forward output for the multilayered perceptron");
        Matrix inputs = Matrix.fromArray(input);
        print("Converted input to Matrix:");
        inputs.print(print);
        print("Multiplying weight matrix of input-hidden by input:");
        Matrix hidden_output = Matrix.rowcolumnMultiply(this.weights_ih, inputs); //Multiply all the weights with all inputs for the perceptrons in the hidden layer
        hidden_output.print(print);
        print("Adding bias to the hidden output: ");
        hidden_output.add(this.bias_hidden);
        hidden_output.print(print);
        print("Executing sigmoid function for the hidden output matrix:");
        hidden_output.execute(sigmoid_function);
        hidden_output.print(print);
        print("Multiplying weight matrix of hidden-output by output matrix:");
        Matrix output = Matrix.rowcolumnMultiply(weights_ho, hidden_output);
        output.print(print);
       print("Adding bias to the output matrix: ");
        output.add(bias_output);
        output.print(print);
        print("Executing sigmoid function for the output matrix:");
        output.execute(sigmoid_function);
        output.print(print);
        return Matrix.toArray(output);
    }
    public void train(float[] input, float[] target){
        //Step 1: Feed forward the neural network in order to get an output
        
        Matrix inputs = Matrix.fromArray(input); //Convert input from Array to Matrix
        print("Input:");
        inputs.print(print);
        Matrix targets = Matrix.fromArray(target); //Convert target from Array to Matrix
        print("Target:");
        targets.print(print);
        print("Weight Input-hidden:");
        Matrix hidden_input = Matrix.rowcolumnMultiply(this.weights_ih, inputs); //Hidden output = activation_function(weight of input * value of input + bias)
        hidden_input.add(this.bias_hidden); //Add bias value to the output
        hidden_input.print(print);
        Matrix hidden_output = Matrix.execute(hidden_input, sigmoid_function); //Pass the hidden output through activation function
        print("Hidden output: ");
        hidden_output.print(print);
        Matrix output_inputs = Matrix.rowcolumnMultiply(this.weights_ho, hidden_output); // The input to the output layer is the weights (who) multiplied by hidden layer
        output_inputs.add(this.bias_output); //Add bias to the output
        Matrix outputs = Matrix.execute(output_inputs, sigmoid_function); //Pass the output through activation function
        
        print("Guessed output: ");
        outputs.print(print);
        print("Target output: ");
        targets.print(print);
        
        //Step 2: Calculate error for the hidden-output and input-hidden layer
        Matrix output_errors = Matrix.subtract(targets, outputs);
        print("Error: ");
        output_errors.print(print);
        
        //Step 3: Start Backpropagation
        Matrix who_transposed = Matrix.transpose(this.weights_ho); //Transpose the weights of the hidden-output layer
        Matrix hidden_errors = Matrix.rowcolumnMultiply(who_transposed, output_errors); // Hidden errors is output error multiplied by weights (who)
        print("Hidden error: ");
        hidden_errors.print(print);
        //Calculate the gradient for hidden-output layer which is the product of learning rate, error of the output, deriviative of the output and the input from hidden layer
        Matrix gradient_output = Matrix.execute(outputs, desigmoid_function); //Calculate deriviative of the output
        gradient_output.elementwiseMultiply(output_errors); //Multiply the deriviative with output errors
        gradient_output.multiply(this.learning_rate); //Multiply the result by the learning rate
        print("Output gradient:");
        gradient_output.print(print);
        
        //Calculate gradient for the input-hidden layer
        
        Matrix gradient_hidden = Matrix.execute(hidden_output, desigmoid_function); //Calculate deriviative of the output
        gradient_hidden.elementwiseMultiply(hidden_errors);//Multiply the deriviative with output errors
        gradient_hidden.multiply(this.learning_rate);//Multiply the result by the learning rate
        print("Hidden output gradient: ");
        gradient_hidden.print(print);
        
        //Calculate change in weight from hidden to output layer
        Matrix hidden_output_transposed = Matrix.transpose(hidden_output);
        /*print("Multiplying gradient output matrix: ");
        gradient_output.print(print);
        print("------------BY HIDDEN OUTPUT TRANSPOSED---------------------");
        hidden_output_transposed.print(print);*/
        Matrix deltaW_output = Matrix.rowcolumnMultiply(gradient_output, hidden_output_transposed);
        print("Delta weight hidden output: ");
        deltaW_output.print(print);
        this.weights_ho.add(deltaW_output); //Add the change to the weight matrix of hidden-output layer
        print("Adjusted weight:");
        this.weights_ho.print(print);
        this.bias_output.add(gradient_output);
        print("Bias hidden output:");
        this.bias_output.print(print);
        //Calculate change in weight from input to hiddden layer
        Matrix inputs_transposed = Matrix.transpose(inputs);
        Matrix deltaW_hidden = Matrix.rowcolumnMultiply(gradient_hidden, inputs_transposed);
        print("Delta weight input hidden: ");
        deltaW_hidden.print(print);
        this.weights_ih.add(deltaW_hidden);
        print("Adjusted weight input hidden:");
        this.weights_ih.print(print);
        this.bias_hidden.add(gradient_hidden);
         print("Bias input hidden:");
        this.bias_hidden.print(print);
        
        
        
    }
    
}
