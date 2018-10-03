/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import commontools.ApplicationTools;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javax.swing.WindowConstants;

/**
 *
 * @author User
 */
public class ContentPane extends BorderPane {
    //Containers in the layout
    HBox top;
    VBox left, right;
    GridPane center;
    
    
    
    Text title, time, run_time, message, container_label;
    Button config, exit, network, connections;
    TextArea output;
    ScrollPane output_scroller;
    
    String object_type;
    ScrollPane center_scroller;
    
    
    
    MyView[] views = new MyView[3];
    public static int current_view = -1;
    
    EventHandler<ActionEvent> exit_event;
    public ContentPane(String object_type, WelcomeView welcome_view, MainView main_view, ConnectionView connection_view, EventHandler<ActionEvent> exit_event){
        super();
        this.exit_event = exit_event;        
        this.object_type = object_type;
        this.views[0] = main_view;
        this.views[1] = welcome_view;
        this.views[2] = connection_view;
        createTopContainer();
        createCenterContainer();
        createRightContainer();
        setTop(top);
        
        setRight(right);
        setCenter(center_scroller);  
        
    }
    public void displayInOutput(String text){
            
            final String t = "\n" + text;
            Platform.runLater(new Runnable(){
            @Override
            public void run() {                
                output.appendText(t);            }
            });
            
       
    }
    public void createTopContainer(){
        int padding = 10;
        int spacing = 5;
        String background_color = "-fx-background-color: #000000";
        Color font_color = Color.WHITE;
        Font font_style = Font.font("Times New Roman", FontWeight.BOLD, 24);
        top = new HBox();
        top.setPadding(new Insets(padding));
        top.setSpacing(spacing);
        top.setStyle(background_color);
        
        title = new Text("Neural Network: " + object_type.toUpperCase() + " RECOGNITION");
        title.setFont(font_style);
        title.setFill(font_color);
        
        top.getChildren().add(title);
    }
    public void createLeftContainer(ContentPane root){
        int padding = 10;
        int spacing = 20;
        String background_color = "-fx-background-color: #333333";
        String button_style = "-fx-background-color: transparent; -fx-text-fill: #f6f6f6; -fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );";
        String box_style = "-fx-border-color: #000000; -fx-border-width: 1; -fx-cursor: hand; -fx-effect: dropshadow( gaussian , rgba(255,255,255,0.5) , 0,0,0,1 );";
        Color font_color = Color.WHITE;
        Color font_color_dark = Color.web("#f6f6f6");
        Font font_style = Font.font("Times New Roman", 18);
        Font font_style_small = Font.font("Times New Roman", 12);
        
        LocalDateTime now = ApplicationTools.getTime();
        String t = ApplicationTools.formatTime(now);
        DropShadow effect = new DropShadow();
        effect.setRadius(5);
        effect.setOffsetX(3);
        effect.setOffsetY(3);
        effect.setColor(Color.BLACK);
        effect.setSpread(0.6);
        
        time = new Text(t);
        time.setFont(font_style);
        time.setFill(font_color);
        time.setEffect(effect);
        
        run_time = new Text("0Y-0M-0D  -- 0h:0m:0s");
        run_time.setFont(font_style_small);
        run_time.setFill(font_color_dark);
        run_time.setEffect(effect);
        
        
        left = new VBox();
        left.setPadding(new Insets(padding));
        left.setSpacing(spacing);
        left.setStyle(background_color);
        
        
        config = new Button("Network Status");
        config.setFont(font_style);
        config.setStyle(button_style);
        config.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                if(current_view != views[0].view_index){
                    for(MyView view: views){
                        if(current_view == view.view_index){
                            view.disableView(root);
                            break;
                        }                    
                    }
                    views[0].enableView(root);
                    
                }
                
            }
        });
        
        
        exit = new Button("Exit");
        exit.setFont(font_style);
        exit.setOnAction(exit_event);
        exit.setStyle(button_style);
        
        network = new Button("Home");
        network.setFont(font_style);
        network.setStyle(button_style);
        network.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                
                if(current_view != views[1].view_index){
                    for(MyView view: views){
                        if(current_view == view.view_index){
                            view.disableView(root);
                            break;
                        }                    
                    }
                    views[1].enableView(root);
                    
                }
                
            }
        });
        
        connections = new Button("Neural Connections");
        connections.setFont(font_style);
        connections.setStyle(button_style);
        connections.setOnAction(new EventHandler<ActionEvent>(){
            @Override
            public void handle(ActionEvent t) {
                
                if((current_view != views[2].view_index) && (Configuration.image_width <= Configuration.max_neuron_display_width) && (Configuration.image_height <= Configuration.max_neuron_display_height)){
                    for(MyView view: views){
                        if(current_view == view.view_index){
                            view.disableView(root);
                            break;
                        }                    
                    }
                    views[2].enableView(root);
                    
                }
                
            }
        });
        HBox box1 = new HBox();
        box1.setAlignment(Pos.CENTER);
        box1.getChildren().add(time);                
        left.getChildren().add(box1);
        
        HBox box2 = new HBox();
        box2.setAlignment(Pos.CENTER);
        box2.getChildren().add(run_time);
        left.getChildren().add(box2);
        
        HBox box3 = new HBox();
        box3.setStyle(box_style);
        box3.setAlignment(Pos.CENTER_LEFT);
        box3.getChildren().add(network);
        left.getChildren().add(box3);
        
        HBox box4 = new HBox();
        box4.setStyle(box_style);
        box4.setAlignment(Pos.CENTER_LEFT);
        box4.getChildren().add(config);
        left.getChildren().add(box4);
        
        HBox box5 = new HBox();
        box5.setStyle(box_style);
        box5.setAlignment(Pos.CENTER_LEFT);
        box5.getChildren().add(connections);
        left.getChildren().add(box5);
        
        HBox box6 = new HBox();
        box6.setStyle(box_style);
        box6.setAlignment(Pos.CENTER_LEFT);
        box6.getChildren().add(exit);
        left.getChildren().add(box6);
        setLeft(left);
        
    }
    public void createRightContainer(){
        int padding = 10;
        int spacing = 5;
        String background_color = "-fx-background-color: #ffffff";
        Color font_color = Color.BLACK;
        Font font_style = Font.font("Times New Roman", 12);
        
        container_label = new Text("OUTPUT");
        container_label.setFont(font_style);
        container_label.setFill(font_color);
        
        
        
        
        output = new TextArea("Initializing scene contents...");
        output.setEditable(false);
        
        output.setFont(font_style);
        output.setWrapText(true);
        output.setPrefRowCount(50);
        
        output_scroller = new ScrollPane();
        output_scroller.setContent(output);
        output_scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        output_scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
        
        VBox.setVgrow(output, Priority.ALWAYS);
        VBox.setVgrow(output_scroller, Priority.ALWAYS);
        
        right = new VBox();
        right.setStyle(background_color);
        right.getChildren().addAll(container_label, output_scroller);
         
    }
    public void createCenterContainer(){
        int padding = 10;
        int spacing = 5;
        int hGap = 10;
        int vGap = 10;
        String background_color = "-fx-background-color: #f6f6f6";
        Color font_color = Color.WHITE;
        Font font_style = Font.font("Times New Roman", 18);
        
        center = new GridPane();
        center.setStyle(background_color);
        center.setHgap(hGap);
        center.setVgap(vGap);
        
        this.center_scroller = new ScrollPane();
        this.center_scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.center_scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.center_scroller.setContent(center);
        
        
        
        
        
    }
    public void destroy(){
        System.exit(WindowConstants.EXIT_ON_CLOSE);
    }
}
