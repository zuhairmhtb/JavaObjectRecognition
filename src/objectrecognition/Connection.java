/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import java.util.ArrayList;
import javafx.scene.canvas.Canvas;
import javafx.scene.paint.Color;

/**
 *
 * @author User
 */
public class Connection {
    public Canvas[][] main_image;
    public int fragment_columns;
    public int fragment_rows;
    
    public int length = 0;
    public int width, height, fragments_created;
    public int fragment_width_size = 50;
    public int fragment_height_size = 50;
    public Connection(int width, int height){
        //Detect number of canvases required to draw the connection
        print("Creating a fragmented image of size " + width + " and height " + height);
        this.width = width;
        this.height = height;
        this.fragment_rows = fragmentRowsRequired(height);
        print(this.fragment_rows + " rows can be created in the image");
        this.fragment_columns = fragmentColumnsRequired(width);
        print(this.fragment_columns + " columns can be created in the image");
        fragments_created = this.fragment_rows * this.fragment_columns;
        print(this.fragments_created + " fragments can be created in the image");
        main_image = new Canvas[fragment_columns][fragment_rows];
        print("Created main image canvas containing " + fragments_created + " fragments");
        for(int i=0; i < fragment_columns; i++){
            print("Creating canvas at column number " + i);
            for(int j=0; j < fragment_rows; j++){
                print("Creating canvas at row number " + j);
                main_image[i][j] = new Canvas();
            }
        }
    }
    public void print(String text){
        System.out.println(text);
    }
    public int fragmentRowsRequired(int height){
        int required = height/this.fragment_height_size;
        if(height%this.fragment_height_size > 0) required++;
        return required;
    }
    public int fragmentColumnsRequired(int width){
        int required = width/this.fragment_width_size;
        if(width%this.fragment_width_size > 0) required++;
        return required;
    }
    public int fragmentsRequired(int width, int height){
        //slice the entire image into fragments based on the specified size
        
        int required_width = width/fragment_width_size;//Number of column fragments in the image
        if(width%fragment_width_size > 0) required_width++;
        int required_height = height/fragment_height_size; //Number of row fragments in the image
        if(height%fragment_height_size > 0) required_height++;
        
        return required_width*required_height;       
        
    }
    //Set a color to the specified pixel in the main image
    public boolean setColor(int x, int y, Color color){
        print("Setting color " + color.getRed() + "," + color.getGreen() + "," + color.getBlue() + "[RGB] at pixel position " + x + "," + y + " in the image");
        if(x < this.width && y < this.height){
            print("Provided pixel coordinate is within a valid range");
           int fragment_column_number = x/this.fragment_columns;
           int fragment_row_number = y/this.fragment_rows;
           int col_index = x%this.fragment_columns;
           int row_index = y%this.fragment_rows;
           print("Painting fragment number " + fragment_row_number + "," + fragment_column_number + " in the main image");
           print("Paining at pixel position " + row_index + " and " + col_index + " in the image"); 
           
           
        }
        
        return false;    
    }
}
