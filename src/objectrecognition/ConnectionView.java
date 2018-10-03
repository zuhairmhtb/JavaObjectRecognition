/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import java.util.ArrayList;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.PixelWriter;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import static objectrecognition.MainView.getMainContentTitle;
import static objectrecognition.MainView.learning_rate_slider;
import static objectrecognition.MainView.title_font;

/**
 *
 * @author User
 */
public class ConnectionView extends MyView {

    public Text container_title;
    public ScrollPane main_scroller;
    public VBox connection_container;
    public ScrollPane[] layer_pane;
    public ArrayList<Canvas[]> connections = new ArrayList<>();
    ArrayList<Canvas> layer_conn = new ArrayList<>();
    public int radius = 20;
    public Text[] layer_label;
    public ColumnConstraints[] column_constraints = new ColumnConstraints[10];

    public static Font title_font = Font.font("Times New Roman", FontWeight.BOLD, 24);
    public static Color title_color = Color.web("#333333");
    public static Color header_color = Color.web("#333333");
    public static Color content_color = Color.BLACK;
    public static Font header_font = Font.font("Times New Roman", FontWeight.BOLD, 18);
    public static Font content_font = Font.font("Times New Roman", FontWeight.NORMAL, 18);

    public ConnectionView(int layers, Configuration conf) {
        super(3, conf);
        container_title = getMainContentTitle();
        createContainers();
        addLayers(layers);
    }

    public static Text getMainContentTitle() {
        Text title = new Text("NEURAL CONNECTION INTERFACE");
        title.setFont(title_font);
        title.setFill(MainView.title_color);
        return title;
    }
    
    public void createColumnConstraints() {
        for (int i = 0; i < this.column_constraints.length; i++) {
            column_constraints[i] = new ColumnConstraints();
            column_constraints[i].setPercentWidth(20);
        }
    }

    @Override
    public boolean enableView(ContentPane root) {
        if (root.current_view != this.view_index) {
            /*for (int i = 0; i < this.column_constraints.length; i++) {
                root.center.getColumnConstraints().add(this.column_constraints[i]);
            }*/
            root.center.add(container_title, 0, 0, 5, 1);
            root.center.add(main_scroller, 0, 1, 2, 3);
            root.current_view = this.view_index;
            return true;
        }
        return false;
    }

    @Override
    public boolean disableView(ContentPane root) {
        if (root.current_view == this.view_index) {
            root.center.getChildren().removeAll(container_title, main_scroller);
            /*for (int i = 0; i < this.column_constraints.length; i++) {
                root.center.getColumnConstraints().remove(this.column_constraints[i]);
            }*/
            return true;
        }
        return false;

    }

    public void createContainers() {
        main_scroller = new ScrollPane();
        main_scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        main_scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        this.connection_container = new VBox();
        connection_container.setSpacing(10);
        connection_container.setAlignment(Pos.CENTER);
        main_scroller.setContent(connection_container);
    }
    
    public void createNeurons(HBox container, int input_neurons, int output_neurons){
        Canvas canvas = new Canvas(input_neurons, output_neurons);
        canvas.setCache(true);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        PixelWriter neurons = gc.getPixelWriter();
        for(int i=0; i < input_neurons; i++){
            for(int j=0; j < output_neurons; j++){
                neurons.setColor(i, j, Color.BLACK);
                
            }
        }
        
        layer_conn.add(canvas);
        container.getChildren().add(canvas);
    }

    public void addLayers(int layers) {
        layer_pane = new ScrollPane[layers];
        
        layer_label = new Text[layers];
        int layer_height = 100;
        int layer_width = 800;
        for (int i = 0; i < layers; i++) {
            layer_label[i] = new Text("Layer " + (i + 1));
            layer_label[i].setFont(header_font);
            layer_label[i].setFill(header_color);

            layer_pane[i] = new ScrollPane();
            layer_pane[i].setPrefSize(layer_width, layer_height);
            layer_pane[i].setHbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            layer_pane[i].setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            layer_pane[i].setStyle("-fx-border-bottom-color: #333333; -fx-border-bottom-width: 1;");
            
            HBox ineuron_box = new HBox();            
            VBox container = new VBox();
            if(i == 0) createNeurons(ineuron_box, Configuration.input_nodes, Configuration.hidden_nodes);
            else if(i == layers -1) createNeurons(ineuron_box, Configuration.hidden_nodes, Configuration.output_nodes);
            else createNeurons(ineuron_box, Configuration.hidden_nodes, Configuration.hidden_nodes);
            
            layer_pane[i].setContent(ineuron_box);           
            container.getChildren().addAll(layer_label[i], layer_pane[i]);
            connection_container.getChildren().addAll(container);
        }
    }
}
