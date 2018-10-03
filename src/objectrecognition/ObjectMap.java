/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

/**
 *
 * @author User
 */
public class ObjectMap {
    public static String[] digit_map = {
      "0", "1", "2", "3", "4", "5", "6", "7", "8", "9"  
    };
    public static String[] face_map = {
      "Mr. Gentleman", "Mr. Moustache", "Asian Guy", "Young man", "Chinese person", "Indian man", "Bald person"   
    };
    public static String[] object_map = {
        "Piggy Bank", "Car", "Packet", "Vaseline", "Wooden Paper Weight", "duck", "bottle", "glass" 
    };
    public static String[] shape_map = {
      "Mr. Gentleman", "Mr. Moustache", "Asian Guy", "Young man", "Chinese person", "Indian man", "Bald person"   
    };
    public static String[] pc_map = {
      "PC", "Not PC"   
    };
    public static String getOutput(int index){
        if(Configuration.object_type.equals("digit")){
           if(index >= 0 && index < digit_map.length)return digit_map[index];             
        }
        else if(Configuration.object_type.equals("face")){
            if(index >= 0 && index < face_map.length)return face_map[index];
        }
        else if(Configuration.object_type.equals("object")){
            if(index >= 0 && index < object_map.length)return object_map[index];
        }
        else if(Configuration.object_type.equals("shape")){
            if(index >= 0 && index < shape_map.length)return shape_map[index];
        }
        else if(Configuration.object_type.equals("pc")){
            if(index >= 0 && index < pc_map.length)return pc_map[index];
        }
        return "MAP ERROR: OBJECT NOT IN THE LIST";
    }
    public static int getOutputLength(){
        if(Configuration.object_type.equals("digit")){
            return digit_map.length;
        }
        else if(Configuration.object_type.equals("face")){
            return face_map.length;
        }
        else if(Configuration.object_type.equals("object")){
            return object_map.length;
        }
        else if(Configuration.object_type.equals("shape")){
            return shape_map.length;
        }
        else if(Configuration.object_type.equals("pc")){
            return pc_map.length;
        }
        return 0;
    }
}
