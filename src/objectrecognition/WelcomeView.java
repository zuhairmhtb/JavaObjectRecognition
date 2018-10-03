/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author User
 */
public class WelcomeView extends MyView{
    
    public Text welcome_message, progress_notification;
    public ProgressIndicator welcome_progress;
    
    
    public static Font welcome_message_font = Font.font("Times New Roman", FontWeight.BOLD, 24);
    public static Font progressbar_notification_font = Font.font("Times New Roman", FontWeight.NORMAL, 16);
    
    public WelcomeView(Configuration conf){  
        super(1, conf);
        welcome_message = getWelcomeContentMessage();
        welcome_progress = getWelcomeContentProgress();
        
        progress_notification = getWelcomeContentNotification();
        
    }
    @Override
    public boolean enableView(ContentPane root){
        if(root.current_view != this.view_index){
            root.center.add(welcome_message, 31, 30, 2, 1);
            root.center.add(welcome_progress, 30, 31, 3, 1);
            root.center.add(progress_notification, 30, 32, 3, 1);
            root.current_view = this.view_index;
            return true;
        }
        return false;
        
    }
    @Override
    public boolean disableView(ContentPane root){
        if(root.current_view == this.view_index){
            root.center.getChildren().removeAll(welcome_message, welcome_progress, progress_notification);
            return true;
        }
        return false;
    }
    public static Text getWelcomeContentMessage(){
        Text message = new Text("Loading...");
        message.setFont(welcome_message_font);
        message.setFill(Color.BLACK);        
        return message;        
    }
    public static ProgressIndicator getWelcomeContentProgress(){
        ProgressIndicator pi = new ProgressIndicator(0.1);             
        return pi;
    }
    public static Text getWelcomeContentNotification(){
        Text message = new Text("Loading views of the application's scene/-");
        message.setFont(progressbar_notification_font);
        message.setFill(Color.BLACK);
        return message;
    }
}
