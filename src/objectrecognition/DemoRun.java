/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import commontools.ApplicationTools;
import commontools.LearningRate;
import commontools.Matrix;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javax.imageio.ImageIO;
import trainingdata.ImageData;

/**
 *
 * @author User
 */
public class DemoRun implements Runnable {

    public boolean run_demo = false;
    ObjectRecognition or;
    public ContentPane root;
    MainView view;
    ConnectionView cview;
    public int last_accuracy = Integer.MIN_VALUE;
    public Configuration conf;

    public float avg_err, output_err;
    public float[] output_err_layer = new float[0];
    public float[] acc_objects = new float[0];
    public float[] avg_confidence = new float[0];
    public float[] outputs = new float[0];

    public void setConfig(ObjectRecognition or, ContentPane root, MainView view, ConnectionView cview, Configuration conf) {
        System.out.println("Setting configuration for running the neural network");
        this.conf = conf;

        this.or = or;
        this.root = root;

        this.view = view;
        this.cview = cview;

        view.error_data = new XYChart.Series[conf.hidden_layers];
        for (int i = 0; i < view.error_data.length; i++) {
            System.out.println("Creating data for graph: error_data of length: " + +view.error_data.length);
            view.error_data[i] = new XYChart.Series();
            System.out.println("Setting name for data of hidden layer " + (i + 1));
            view.error_data[i].setName("Layer " + (i + 1));
            System.out.println("Adding data container to graph");
            view.error_graph.getData().add(view.error_data[i]);
        }

    }

    public void print(String text) {
        System.out.println(text);
        // root.displayInOutput(text);

    }

    //remove the first item
    //reduce XValue of all items by 1
    //add the new item
    public void reduceSeriesXValue(XYChart.Series series, float newValue) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (series.getData().size() > 0) {
                    series.getData().remove(0);
                    int numOfPoint = series.getData().size();
                    series.getData().add(new XYChart.Data(numOfPoint, newValue));

                } else {
                    series.getData().add(new XYChart.Data(0, newValue));
                }
            }

        });
    }

    public void updateUI(ImageData data, int index_output, float acc, float acc_fluc, int current_number) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                //Calculate confidence level
                if (new File(data.directory_url + data.filename + data.format).exists()) {
                    //view.input_image.setImage(ApplicationTools.bufferedToImage(ApplicationTools.resize(data.image, Configuration.image_display_width, Configuration.image_display_height)));
                    view.input_image.setImage(ApplicationTools.bufferedToImage(data.image));
                }
                view.output_val.setText(ObjectMap.getOutput(index_output));
                
                if(outputs != null){
                    ArrayList<Integer> added_index = new ArrayList<>();                    
                    for(int i=0; i < outputs.length; i++){
                        int max_index = -1;
                        float max_val = Float.MIN_VALUE;
                        for(int j=0; j < outputs.length; j++){
                            if(outputs[j] > max_val && !added_index.contains(j)){
                                max_index = j;
                                max_val = outputs[j];
                            }
                        }
                        if(max_index >= 0){  
                            DecimalFormat df = new DecimalFormat("#.00");                   
                            view.probable_outputs[i+1].setText(ObjectMap.getOutput(max_index) + ":     " + df.format((double)(max_val * 100)) + "%");
                            added_index.add(max_index);
                        }
                    }
                }

                view.accuracy_data.getData().add(new XYChart.Data(current_number, acc));
                view.accuracy_fluctuation_data.getData().add(new XYChart.Data(current_number, acc_fluc));
                if (!ObjectMap.getOutput(Integer.parseInt(data.target)).equals(ObjectMap.getOutput(index_output))) {
                    view.tick_wrong_image.setImage(conf.wrong_imagefx);
                } else {
                    view.tick_wrong_image.setImage(conf.tick_imagefx);
                }

                view.output_error_data.getData().add(new XYChart.Data(current_number, (100 * avg_err)));
                for (int i = 0; i < output_err_layer.length - 1; i++) {
                    view.error_data[i].getData().add(new XYChart.Data(current_number, (100 * output_err_layer[i])));
                }
                for (int i = 0; acc_objects != null && i < acc_objects.length; i++) {
                    view.digit_recognized_data[i].getData().add(new XYChart.Data(current_number, (int) 100 * acc_objects[i]));

                }
                for (int i = 0; i < avg_confidence.length; i++) {
                    if (current_number != 0 && view.expertise_data[i].getData().size() > 1) {
                        view.expertise_data[i].getData().remove(1);
                    }

                    view.expertise_data[i].getData().add(new XYChart.Data((int) (100 * acc_objects[i]), (int) (100 * avg_confidence[i])));
                }

            }
        });
    }
    
    public final float getSigmoid(float x) {
        return ((float) ((1 / (1 + Math.pow(Math.E, (-1 * x))))));
    }
    public void analyzeConnection() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                
                for (int i = 0; i < or.mlp.layers.length; i++) {
                    System.out.println("Analyzing connection for layer " + i);
                    Canvas connection = cview.layer_conn.get(i);
                    PixelWriter neurons = connection.getGraphicsContext2D().getPixelWriter();
                    int output_neurons = or.mlp.layers[i].weights.columns;
                    int input_neurons = or.mlp.layers[i].weights.rows;
                    float[][] data = or.mlp.layers[i].weights.data;
                  //  System.out.println("Analyzing for " + input_neurons + " input and " + output_neurons + " output neurons");
                    float max_weight = Float.MIN_VALUE;
                    for (int j = 0; j < output_neurons; j++) {
                        for (int k = 0; k < input_neurons; k++) {
                            float weight = data[k][j];
                          //  System.out.println("Weight for connection " + j + "--" + k + " is " + weight);
                            
                            float sigmoided = getSigmoid(weight);
                            int color = (int) (sigmoided * 255);
                            if(weight > 0) neurons.setColor(j, k, Color.rgb(0, color, 0));
                            else neurons.setColor(j, k, Color.rgb(color,0 , 0));
                            if(weight > max_weight) max_weight = weight;
                        }
                    }
                    cview.layer_label[i].setText("Layer " + (i+1) + "(Maximum Weight: " + max_weight + ")");
                }
            }
        });

    }

    public void demoNeuralNetwork() throws Exception {
        int current = 0;
        System.out.println("Creating data for graph output error");
        output_err_layer = new float[Configuration.hidden_layers];
        LearningRate.rate = conf.learning_rate;
        view.learning_rate.setText("" + LearningRate.rate);

        while (run_demo) {
            print("Creating neural network for digit recognition with " + conf.input_nodes + " input nodes, " + (conf.hidden_nodes * conf.hidden_layers) + " hidden nodes(" + conf.hidden_nodes + " nodes per layer for " + conf.hidden_layers + " layers) and " + conf.output_nodes + " output nodes");
            print("Learning rate: " + conf.learning_rate);

            final ImageData data = conf.dataset.get(new Random().nextInt(conf.dataset.size()));
            print("Performing recognition for " + data.directory_url + data.filename + data.format);

            print("Acquiring output for test_data: " + data.filename);
            float[] outputs = or.mlp.feedForward(data.inputs);
            this.outputs = outputs;
            /* print("Inputs:");
            String input = "";
            for (int i = 0; i < data.inputs.length; i++) {
                input += data.inputs[i];
                if (i != data.inputs.length - 1) {
                    input += ", ";
                }
            }
            print(input);
            print("Output:");*/
            String output = "";
            int max_index = -1;
            float max_output = -5f;
            for (int i = 0; i < outputs.length; i++) {
                output += outputs[i];
                if (i != outputs.length - 1) {
                    output += ", ";
                }
                if (outputs[i] > max_output) {
                    max_index = i;
                    max_output = outputs[i];
                }
            }
            print(output);

            if (max_index >= 0) {
                final int index_output = max_index;
                final int current_number = current;

                final int acc;
                final int acc_fluc;
                acc = (int) (100 * max_output);
                if (last_accuracy == Integer.MIN_VALUE) {
                    acc_fluc = 0;
                } else {
                    acc_fluc = Math.abs(last_accuracy - acc);
                }
                last_accuracy = acc;
                if (!ObjectMap.getOutput(Integer.parseInt(data.target)).equals(ObjectMap.getOutput(index_output))) {
                    //Calculate layer errors
                    print("Training neural network");
                    or.train();
                } else {
                    Thread.sleep(2000);
                }
                try {
                    Matrix errors = or.mlp.output_errors[or.mlp.output_errors.length - 1];
                    float average_error = 0;
                    for (int i = 0; i < errors.rows; i++) {
                        for (int j = 0; j < errors.columns; j++) {
                            average_error += errors.data[i][j];
                        }
                    }
                    average_error = average_error / (errors.rows * errors.columns);
                    avg_err = average_error;
                    print("Average output error: " + average_error);

                    float output_error = 0;
                    for (int i = 0; i < or.mlp.output_errors.length - 1; i++) {
                        errors = or.mlp.output_errors[i];
                        average_error = 0;
                        for (int j = 0; j < errors.rows; j++) {
                            for (int k = 0; k < errors.columns; k++) {
                                average_error += errors.data[j][k];
                            }
                        }
                        average_error = average_error / (errors.rows * errors.columns);
                        output_error += average_error;
                        output_err_layer[i] = average_error;
                    }
                    output_err = output_error / or.mlp.output_errors.length - 1;
                    //Calculate accuracy and confidence for each digit
                    or.reading = true;
                    acc_objects = new float[or.last_accuracy.length];
                    avg_confidence = new float[or.confidence_total.length];
                    for (int i = 0; i < acc_objects.length; i++) {
                        acc_objects[i] = or.last_accuracy[i];

                    }
                    for (int i = 0; i < avg_confidence.length; i++) {
                        avg_confidence[i] = (float) or.confidence_value[i] / or.confidence_total[i];
                    }
                    or.reading = false;
                } catch (Exception e) {
                    print(e.getMessage());
                }
                updateUI(data, index_output, acc, acc_fluc, current_number);
                if(Configuration.image_display_width <= Configuration.max_neuron_display_width && Configuration.image_display_height <= Configuration.max_neuron_display_height)analyzeConnection();

            }

            current++;
            Thread.sleep(2000);

        }
        System.out.println("Saving weights and biases...exiting");
        or.saveMLPWeights();
        or.saveMLPBiases();
    }

    @Override
    public void run() {
        try {
            run_demo = true;
            demoNeuralNetwork();
        } catch (Exception e) {
            print(e.getMessage());
        }
    }
}
