/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import commontools.ApplicationTools;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.image.Image;
import javax.imageio.ImageIO;
import trainingdata.ImageData;

/**
 * Contains Configurations of the package objectrecognition
 *
 * @author User
 */
public class Configuration {

    public final static int image_width = 50; //digit:28x28 and face:320x243 and object: 448x416
    public final  static int image_height = 50;
    public static final int max_neuron_display_width = 50;
    public static final int max_neuron_display_height = 50;
    public final static int image_display_width = 28; //digit:28x28 and face:320x243 and object: 448x416
    public final  static int image_display_height = 28;
    public final  static String training_image_format = "png"; //Format of images of training set
    public final  static String object_type = "digit";
    public final  static int output_nodes = ObjectMap.getOutputLength();
    public final  static int batch_size = 100; //Number of training images for a single object
    
    public final  static int input_nodes = image_width * image_height;
    public final  static int hidden_nodes = 50;
    public final  static int hidden_layers = 3;
    
    public final  static float learning_rate = 0.2f;
    public final  static String test_image_dir = "D:\\thesis\\png_images\\test\\";
    public final  static String test_image_format = ".png";
    public final  static int test_data_size = 10;
    
    public final  static String extension = "nn"; //Extension for data files of the application
    public final static String base_dir = "E:\\Fall17\\CSE422_AI_3\\NeuralNetwork\\";
    public final  static String base_object_dir = base_dir + object_type + "\\"; //Base directory for the data of the application
    public final  static String mlp_weight_and_biases_file_dir = base_object_dir + "wab_mlp\\"; //Directory containing weights and biases of the network 
    public final  static String mlp_weight_file_dir = mlp_weight_and_biases_file_dir + "w\\";
    public final  static String mlp_bias_file_dir = mlp_weight_and_biases_file_dir + "b\\"; //Directory containing biases of the network
    public final  static String training_set_directory = base_object_dir + "td\\"; //Directory containing images for training set
    
    
    public static ArrayList<ImageData> test_data;
    public static ArrayList<ImageData> dataset; //Training dataset
    public static String tick_image_path = base_dir + "tick_icon.png";
    public static String wrong_image_path = base_dir + "wrong_icon.png";
    public static BufferedImage tick_image, wrong_image;
    public static Image tick_imagefx, wrong_imagefx;
    public ContentPane root;

    public Configuration(ContentPane root) {
        this.root = root;
        try {
            tick_image = ApplicationTools.resize(ImageIO.read(new File(tick_image_path)), 20, 20);
            wrong_image = ApplicationTools.resize(ImageIO.read(new File(wrong_image_path)), 20, 20);
            tick_imagefx = ApplicationTools.bufferedToImage(tick_image);
            wrong_imagefx = ApplicationTools.bufferedToImage(wrong_image);
        } catch (IOException ex) {
            System.out.println("Image Not found:" + ex.getMessage());
        }
    }

    public void print(String text) {
        if(root != null) root.displayInOutput(text);
        System.out.println(text);
    }

    public void createTestDataset(Configuration conf) {
        test_data = new ArrayList<>();
        for (int i = 0; i < test_data_size; i++) {
            print("Loading test data number " + i);
            String filename = "" + i;
            int prefixes = 5 - filename.length();
            for (int j = 0; j < prefixes; j++) {
                filename = "0" + filename;
            }
            if (new File(test_image_dir + filename + test_image_format).exists()) {
                print("Preparing test data for file " + filename);
                test_data.add(new ImageData(test_image_dir, filename, test_image_format, "0", conf));
            } else {
                print("Test data " + test_image_dir + filename + test_image_format + " does not exist");
            }
        }

        print("Total dataset loaded: " + test_data.size());
        
    }

    public void prepareTrainDataset(Configuration conf) {
        dataset = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            int j = 1;
            while (j < 100) {
                String filename = "" + j;
                print("Adding " + training_set_directory + i + "\\" + filename + "." + training_image_format + " to dataset");
                if (new File(training_set_directory + i + "\\" + filename + "." + training_image_format).exists()) {
                    ImageData data = new ImageData(training_set_directory + i + "\\", filename, "." + training_image_format, "" + i, conf);
                    
                    dataset.add(data);
                    print("Data added");
                } else {
                    print("Data does not exist");
                    break;
                }
                // else break;
                j++;
            }
        }
        print("Total dataset loaded in " + object_type + " recognizer: " + dataset.size());

        //  conf.batch_size = dataset.size();
    }

}
