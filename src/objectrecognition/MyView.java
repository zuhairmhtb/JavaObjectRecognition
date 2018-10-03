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
public class MyView{
    public int view_index;
    public Configuration conf;
    public MyView(int view_index, Configuration conf){
        this.view_index = view_index;
        this.conf = conf;
    }
    public boolean enableView(ContentPane root){
        return false;
    }
    public boolean disableView(ContentPane root){
        return false;
    }
}
