/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package commontools;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;
import java.util.ArrayList;
import sun.misc.IOUtils;

/**
 *
 * @author User
 */
public class FileManager {
    public static String readTextFrom(String url) throws Exception{
        File f = new File(url);
        if(f.exists()){
            String output = "";
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            if(br.ready()){                
                String line = "";
                while((line = br.readLine()) != null){
                    output+=line;
                }
                
            }
            br.close();
            fr.close();
            return output;
        }
        throw new Exception("Could not read file");
    }
    public static boolean writeTextTo(String url, String text) throws Exception{
        File f = new File(url);
        if(!f.exists()) f.createNewFile();
        if(f.exists()){            
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);
            br.write(text);
            br.flush();
            br.close();
            fr.close();
            return true;
            
        }
        throw new Exception("Could not read file");
    }
    public static boolean appendTextTo(String url, String text) throws Exception{
        File f = new File(url);
        if(!f.exists()) f.createNewFile();
        if(f.exists()){
            FileWriter fr = new FileWriter(f);
            BufferedWriter br = new BufferedWriter(fr);
            br.append(text);
            br.flush();
            br.close();
            fr.close();
            return true;
        }
        throw new Exception("Could not read file");
    }
    public static byte[] readByteFrom(String url) throws Exception{
        File f = new File(url);
        if(f.exists()){
            FileInputStream fis = new FileInputStream(f);
            ArrayList<byte[]> output = new ArrayList<>();
            int readVal = 0;
            int total = 0;
            byte[] data = new byte[1000];
            while((readVal = fis.read(data)) != -1){
                output.add(data);
                data = new byte[1000];
                total+=readVal;
            }
            byte[] output_arr = new byte[total];
            int current = 0;
            for(byte[] o: output){
                for(int i=0; i < o.length; i++){
                    output_arr[current] = o[i];
                    current++;
                }
            }
            if(current == total) return output_arr;
        }
        throw new Exception("Could not read file");
    }
    public static boolean writeByteTo(String url, byte[] data) throws Exception{
        File f = new File(url);
        if(!f.exists()) f.createNewFile();
        if(f.exists()){
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(data, 0, data.length);
            fos.flush();
            fos.close();
            return true;
        }
        throw new Exception("Could not read file");
    }
    public static boolean appendByteTo(String url, byte[] data) throws Exception{
        File f = new File(url);
        if(!f.exists()) f.createNewFile();
        if(f.exists()){
            FileOutputStream fos = new FileOutputStream(f);
            byte[] current_data = readByteFrom(url);
            byte[] final_data = new byte[current_data.length + data.length];
            for(int i=0; i < current_data.length; i++){
                final_data[i] = current_data[i];
            }
            for(int i=0; i < data.length; i++){
                final_data[current_data.length + i] = data[i];
            }
            fos.write(final_data, 0, final_data.length);
            fos.flush();
            fos.close();
            return true;
        }
        throw new Exception("Could not read file");
    }
}
