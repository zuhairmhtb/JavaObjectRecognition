/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package digitrecognition;

import commontools.ApplicationTools;
import trainingdata.ImageData;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import neuralnetwork.ThreeLayeredPerceptron;

/**
 *
 * @author User
 */
public class GUI implements Runnable {
    JFrame frame;
    JPanel panel;
    
    JLabel label;
    ImageIcon icon;
    BufferedImage image;
    JLabel output_text, current_error_text, average_error_text, accuracy_text, average_accuracy_text, fluctuation_text, average_fluctuation_text, time_elapsed, correct_output_text;
    LocalDateTime started;
    
    
    
    String dir, format;    
    int width, height;   
    
    DigitRecognition drn;
    
    int input_nodes;
    int hidden_nodes = 10;
    int hidden_layers = 1;
    
    int output_nodes = 10;
    float learning_rate = 0.05f;    
    float error_sum = 0;    
    int error_total = 0;
    int correct_output = 0;
    int output_total = 0;
    
    
    float fluctuation_sum = 0f;
    int fluctuation_total = 0;
    float prev_accuracy = -1f;
    
    float accuracy_sum = 0f;
    int accuracy_total = 0;
    
    
    
    int test_data_size = 1000;
    
    boolean debug = true;
    boolean use_mlp = false;
    Random r;
    ArrayList<ImageData> dataset;
    String defualt_image_name = "00000";
    public DigitConfiguration conf;
    public GUI(int width, int height, int hidden_node, int hidden_layer, float learning_rate, String dir, String format, boolean use_mlp, boolean debug){
        this.debug = debug;
        this.conf = new DigitConfiguration(null);
        this.use_mlp = use_mlp;
        hidden_nodes = hidden_node;
        hidden_layers = hidden_layer;
        this.learning_rate = learning_rate;
        this.dir = dir;
        this.format = format;        
        this.width = 400;
        this.height = 600;
        this.input_nodes = width*height;
        prepareDataset();        
        drn = new DigitRecognition(width, height, hidden_nodes, hidden_layers, use_mlp, debug, conf);             
        this.r = new Random();
        initComponents();
        
    }
    public void initComponents(){
        frame = new JFrame("Digit Recognition(AI)");
        frame.setSize(width, height);
        frame.addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e)
            {
                
                //dr.shutdown();
                try {                        
                        drn.saveWeights();                        
                        drn.saveBiases();
                        
                    } catch (Exception ex) {
                        System.out.println(ex.getMessage());
                    }
                e.getWindow().dispose();
                System.exit(0);
            }
        });
        //frame.setLayout(new BoxLayout());
        Container c = frame.getContentPane();
        c.setLayout(new BorderLayout(10, 10));
         
        
        panel = new JPanel();
        
        label = new JLabel();
        output_text = new JLabel();
        current_error_text = new JLabel();
        average_error_text = new JLabel();
        accuracy_text = new JLabel();
        average_accuracy_text = new JLabel();
        fluctuation_text = new JLabel();
        average_fluctuation_text = new JLabel();
        time_elapsed = new JLabel();
        correct_output_text = new JLabel();
        
        
        File f = new File(dir + defualt_image_name + format);
        try {
            image = ImageIO.read(f);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        icon = new ImageIcon(image);
        label.setIcon(icon);
        
        panel.setLayout(new GridLayout(6, 2, 2, 2));
        panel.add(label);
        panel.add(output_text);
        panel.add(accuracy_text);
        panel.add(average_accuracy_text);
        panel.add(new JLabel("Correct output: "));
        panel.add(correct_output_text);
        panel.add(current_error_text);
        panel.add(average_error_text);
        panel.add(fluctuation_text);            
        panel.add(average_fluctuation_text);
        panel.add(new JLabel("Time elapsed: "));
        panel.add(time_elapsed);
        
        
        c.add(panel);
        
        
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    public void destroy(){
        if(frame != null){
            icon = null;
            label = null;
            output_text = null;
            panel = null;
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        }
    }
    public void prepareDataset(){
        dataset = new ArrayList<>();
        for(int i=0; i < test_data_size; i++){
            String filename = "" + i;
            int prefixes = 5 - filename.length();
            for(int j=0; j < prefixes; j++){
                filename = "0" + filename;
            }
            //System.out.println("Preparing test data for file " + filename+ format);
            dataset.add(new ImageData(dir, filename, format, "0", conf));
        }
        System.out.println("Total dataset loaded: " + dataset.size());
    }
    @Override
    public void run(){        
        started = ApplicationTools.getTime();
        while(true){            
            drn.train();
            ImageData data = dataset.get(r.nextInt(dataset.size()));
            float[] outputs;
            if(use_mlp) outputs = drn.mlp.feedForward(data.inputs);
            else outputs = drn.tlp.feedForward(data.inputs);
            float max = Float.MIN_VALUE;
            int matched = -1;
            for(int i=0; i < outputs.length; i++){
                if(outputs[i] > max){
                    max = outputs[i];
                    matched = i;
                }
            }
            
            icon = new ImageIcon(data.image);
            label.setIcon(icon);
            output_text.setText("Matched: " + matched);
            current_error_text.setText("Current Error: "+(int)((1-max)*100) + "%");
            
            error_sum+=(1-max);
            error_total++;
            average_error_text.setText("Average Error: "+(int)((error_sum/error_total)*100) + "%");
            
            accuracy_text.setText("Accuracy: " + (int)(max*100) + "%");
            accuracy_sum+=max;
            accuracy_total++;
            average_accuracy_text.setText("Average Accuracy: " + (int)((accuracy_sum/accuracy_total)*100) + "%");
            
            fluctuation_text.setText("Fluctuate: " + (int) (Math.abs(max-prev_accuracy) * 100) + "%");
            fluctuation_sum+=Math.abs(max-prev_accuracy);
            fluctuation_total++;
            average_fluctuation_text.setText("Average Fluctutation: " + (int) ((fluctuation_sum/fluctuation_total) * 100) + "%");
            prev_accuracy = max;
            time_elapsed.setText(ApplicationTools.timeElasped(started, ApplicationTools.getTime()));
          
            try {
                Thread.sleep((int)(1000 * max) + 200);
            } catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
    
}
