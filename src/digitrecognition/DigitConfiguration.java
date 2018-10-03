/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package digitrecognition;

import objectrecognition.Configuration;
import objectrecognition.ContentPane;

/**
 *
 * @author User
 */
public class DigitConfiguration extends Configuration{
    public int image_width = 28;
    public int image_height = 28;
    public int input_nodes = image_width * image_height;
    public int hidden_nodes = 10;
    public int hidden_layers = 2;
    public int output_nodes = 10;
    public float learning_rate = 0.01f;
    public String test_image_dir = "D:\\thesis\\png_images\\test\\";
    public String test_image_format = ".png";
    public int test_data_size = 1000;

    public DigitConfiguration(ContentPane root) {
        super(root);
    }
    
}
