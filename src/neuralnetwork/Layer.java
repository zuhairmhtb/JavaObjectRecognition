/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package neuralnetwork;

import commontools.Matrix;

/**
 *
 * @author User
 */
public class Layer {
    public Matrix weights;
    public Matrix biases;
    public int input_nodes;
    public int output_nodes;
    public Layer(int input_nodes, int output_nodes){
        this.input_nodes = input_nodes;
        this.output_nodes = output_nodes;
        weights = new Matrix(output_nodes, input_nodes);
        weights.randomize();
        biases = new Matrix(output_nodes, 1);
        biases.randomize();
    }
}
