/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import trainingdata.ImageData;

/**
 *
 * @author User
 */
public class ObjectRecognitionInterface extends Application {
    
    
    public static ObjectRecognition or;
    
    Configuration conf;

    WelcomeView welcome_view;
    MainView main_view;
    ConnectionView connection_view;
    public ContentPane root;
    
    public boolean run_demo = false;
    public DemoRun demo;    
    public RecognitionService recog_service;
    public TemporalCortex time;
    Timer timer;
    public void print(String text) {

        root.displayInOutput(text);
        System.out.println(text);

    }
    

    public void run() {
        // demoNeuralNetwork();
        launch();
    }
    public void exitApplication(Stage stage){
        try{
           stage.hide();           
            
           stage.close();
           timer.cancel();
           
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        
    }

    @Override
    public void start(Stage stage) throws Exception {
        //Create Views for content display
        welcome_view = new WelcomeView(conf);
        main_view = new MainView(conf);
        connection_view = new ConnectionView(conf.hidden_layers+1, conf);        
        demo = new DemoRun();
        recog_service = new RecognitionService();
        time = new TemporalCortex();
        
        timer = new Timer(true);
        
        EventHandler<ActionEvent> exit_event = new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                                
                demo.run_demo = false;                
            }
            
        };
        root = new ContentPane(conf.object_type, welcome_view, main_view, connection_view, exit_event);
        root.createLeftContainer(root);
        conf = new Configuration(root);
        
        
        Scene scene = new Scene(root, 500, 500);
        welcome_view.enableView(root);  
        
        recog_service.setConfig(welcome_view, main_view, connection_view, or, root, demo, conf);
        recog_service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {

            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("done");
                exitApplication(stage);
            }
        });
        
        stage.setTitle(conf.object_type.toUpperCase() + " RECOGNITION");
        stage.setScene(scene);
        stage.setFullScreen(true);
        recog_service.start();
        time.setPane(root);
        
        new Thread(new Runnable(){
            @Override
            public void run() {
                timer.schedule(new TimerTask(){
                    @Override
                    public void run() {
                        time.run();
                    }
                }, 0, 1000);
            }
        }).start();
        
        stage.show();
        
        
    }
}
