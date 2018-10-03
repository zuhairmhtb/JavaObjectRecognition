/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trainingdata;

import commontools.ApplicationTools;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import objectrecognition.Configuration;

/**
 *
 * @author User
 */
public class ImageData {

    public int width, height;
    public String directory_url, filename, format;
    public BufferedImage image;
    public JFrame frame;
    public JPanel panel;
    public JLabel label;
    public ImageIcon icon;
    
    public float[] inputs;
    public float[] targets;
    public String target;
    public String name;
    public Configuration conf;
    public ImageData(String directory_url, String filename, String format, String target, Configuration conf) {
        System.out.println("Creating image data");        
        this.conf = conf;
        this.directory_url = directory_url;
        this.filename = filename;
        this.format = format;
        String[] attr = target.split("_");
        if(attr.length == 2){
           this.target=attr[1];
           this.name = attr[0];
        }
        else{
            this.target = target;
            this.name = target;
        }
        initComponents();
        createInputData();
        calculateTarget();
    }

    public void initComponents() {
        File f = new File(directory_url + filename + format);
        if (f.exists()) {
            System.out.println("Obtaining image data");
            try {
                BufferedImage temp = ImageIO.read(f);
                if(temp.getWidth() != Configuration.image_width || temp.getHeight() != Configuration.image_height) image = ApplicationTools.resize(temp, Configuration.image_width, Configuration.image_height);
                else image = temp;
                
                frame = new JFrame();
                frame.setSize(width, height);
                panel = new JPanel();
                label = new JLabel();
                icon = new ImageIcon(image);
                label.setIcon(icon);
                panel.add(label);
                frame.getContentPane().add(panel);
                width = image.getWidth();
                height = image.getHeight();
                //frame.setVisible(false);
            } catch (IOException ex) {
                image = null;
                frame = null;
                panel = null;
                icon = null;
                width = -1;
                height = -1;
                System.out.println(ex.getMessage());
            }
        } else {
            image = null;
            frame = null;
            panel = null;
            icon = null;
            width = -1;
            height = -1;
            System.out.println("Image in url " + this.directory_url + this.filename + this.format + " does not exist");
        }
    }
    public boolean displayImage(){
        if(frame != null){
            frame.setVisible(true);
            return true;
        }
        return false;
    }
    public boolean hideImage(){
        if(frame != null){
            frame.setVisible(false);
            return true;
        }
        return false;
    }
    public void destroyComponent(){
        image = null;
        label = null;
        panel = null;
        if(frame != null){
            frame.setVisible(false);
            frame.dispose();
            frame = null;
        }
        
        
    }
    public static BufferedImage rgbToGrayScale(BufferedImage image){
        BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        Graphics g = result.getGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return result;
    }
    public void createInputData(){
        if(image != null){
            image = rgbToGrayScale(image);
            inputs = new float[image.getWidth()*image.getHeight()];
            System.out.println("Creating input array for " + inputs.length + " pixels");
            int current = 0;
            //System.out.println("Input values:");
            for(int i=0; i < image.getWidth(); i++){
                for(int j=0; j < image.getHeight(); j++){
                    int[] rgb = ApplicationTools.getRGB(image.getRGB(i, j));
                    float gray = ((float)(rgb[0]/255) + (float)(rgb[1]/255) + (float)(rgb[2]/255))/3;
                    inputs[current] = gray; //Get grayscal value of each pixel
                   // System.out.print(inputs[current] + " ");
                    current++;
                }
                //System.out.println();
            }
        }
    }
    public void calculateTarget(){
        targets = new float[conf.output_nodes];
       
        for(int i=0; i < targets.length; i++){
            targets[i] = 0f;
        }
        int index = Integer.parseInt(target);
        System.out.println("The target output is " + index);
        targets[index] = 1f;
       
        
    }
}
