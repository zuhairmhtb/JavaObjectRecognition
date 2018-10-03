/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import commontools.LearningRate;
import java.text.DecimalFormat;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 *
 * @author User
 */
public class MainView extends MyView {    
    public Text container_title, learning_rate_slider_label, input_title, output_title, inode_label, inode, onode_label, onode, iweight_label, iweight, oweight_label, oweight, ibias_label, ibias, obias_label, obias, learning_rate_label, learning_rate, output_val_label, output_val, hidden_title, hnode_label, hnode, hlayer_label, hlayer;
    public ScrollPane output_scroller;
    public HBox box, box2;
    public VBox output_box;
    public Text[] probable_outputs = new Text[Configuration.output_nodes + 1];
    public ImageView input_image, output_image, tick_wrong_image;
    public ColumnConstraints[] column_constraints = new ColumnConstraints[10];
    public static Slider learning_rate_slider;
    
    public LineChart<Number, Number> accuracy_graph;
    public LineChart<Number, Number> error_graph;
    public LineChart<Number, Number> digit_recognized_graph;
    public ScatterChart<Number, Number> expertise_graph;
    public int max_nodes_in_graph = 100;
    
    public XYChart.Series accuracy_data = new XYChart.Series();
    public XYChart.Series accuracy_fluctuation_data = new XYChart.Series();
    public XYChart.Series output_error_data = new XYChart.Series();
    public XYChart.Series[] error_data;
    public XYChart.Series[] digit_recognized_data = new XYChart.Series[conf.output_nodes];
    public XYChart.Series[] expertise_data = new XYChart.Series[conf.output_nodes];
    
    public static Font title_font = Font.font("Times New Roman", FontWeight.BOLD, 16);
    public static Color title_color = Color.web("#333333");
    public static Color title_color_inverse = Color.web("#f6f6f6");
    public static Color header_color = Color.web("#333333");
    public static Color header_color_inverse = Color.web("#f6f6f6");
    public static Color content_color = Color.BLACK;
    public static Color content_color_inverse = Color.WHITE;
    
    private final Background focusBackground = new Background( new BackgroundFill( Color.web( "#333333" ), CornerRadii.EMPTY, Insets.EMPTY ) );
    public static Font header_font = Font.font("Times New Roman", FontWeight.BOLD, 16);
    public static Font content_font = Font.font("Times New Roman", FontWeight.NORMAL, 16);
    public MainView(Configuration conf){
        super(2, conf);
        container_title = getMainContentTitle();
        createColumnConstraints();
        createLabels();
        createDataFields();
        createImageView();
        createGraphs();
        createSliders();
        createBoxes();
        createScrollers();
        
    }
    public void createBoxes(){
        box = new HBox(20);
        box.getChildren().addAll(accuracy_graph, error_graph);

        box2 = new HBox(20);
        box2.getChildren().addAll(this.digit_recognized_graph, this.expertise_graph);
        
        output_box = new VBox();
        output_box.setBackground(focusBackground);
        output_box.setAlignment(Pos.CENTER);
        for(int i=0; i < probable_outputs.length; i++){
            output_box.getChildren().add(probable_outputs[i]);
        }
    }
    public static Text getMainContentTitle(){
        Text title = new Text("AISMS-NEURAL NETWORK INTERFACE");
        title.setFont(title_font);
        title.setFill(MainView.title_color);
        return title;
    }
    public void createSliders(){
        learning_rate_slider = new Slider();
        learning_rate_slider.setMin(0);
        learning_rate_slider.setMax(1);
        learning_rate_slider.setValue(LearningRate.rate);
        learning_rate_slider.setShowTickLabels(true);
        learning_rate_slider.setShowTickMarks(true);
        learning_rate_slider.setMajorTickUnit(0.5);
        learning_rate_slider.setMinorTickCount(1);
        learning_rate_slider.setBlockIncrement(0.001);
        learning_rate_slider.valueProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> ov, Number old_value, Number new_value) {   
                    LearningRate.rate = new_value.floatValue();
                    learning_rate.setText("" + new DecimalFormat("#.00").format(LearningRate.rate));
                }
            });
        
    }
    public void createGraphs(){
        final NumberAxis xAxis = new NumberAxis();
        
        xAxis.setAnimated(true);
        xAxis.setLabel("NUMBER OF TIMES TRAINED");
        xAxis.setAutoRanging(true);
        final NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("CONFIDENCE(%)");
        yAxis.setAnimated(true);
        
        accuracy_graph = new LineChart<Number, Number>(xAxis, yAxis);
        accuracy_graph.setAnimated(true);
        accuracy_graph.setMaxSize(20, 20);
        accuracy_graph.setMaxSize(400, 400);
        accuracy_graph.setPrefSize(300, 300);
        accuracy_graph.legendVisibleProperty().set(true);
        
        accuracy_graph.setTitle("CONFIDENCE ABOUT OUTPUT");
        accuracy_data.setName("OUTPUT");
        accuracy_fluctuation_data.setName("RATE OF CHANGE OF CONFIDENCE");
        accuracy_graph.getData().add(accuracy_data);
        accuracy_graph.getData().add(accuracy_fluctuation_data);
        
        
        
       final NumberAxis xAxis2 = new NumberAxis();
        xAxis2.setLabel("NUMBER OF TIMES TRAINED");
        final NumberAxis yAxis2 = new NumberAxis();
        yAxis2.setLabel("ERROR(%)");
        
        error_graph = new LineChart<Number, Number>(xAxis2, yAxis2);
        error_graph.setAnimated(true);
        error_graph.setMaxSize(20, 20);
        error_graph.setMaxSize(400, 400);
        error_graph.setPrefSize(300, 300);
        error_graph.legendVisibleProperty().set(true);
        
        error_graph.setTitle("CHANGE IN ERROR WHILE TRAINING THE NETWORK");
        output_error_data.setName("OUTPUT");
        
        error_graph.getData().add(output_error_data);
        
        
        final NumberAxis xAxis3 = new NumberAxis();
        xAxis3.setLabel("NUMBER OF TIMES TRAINED");
        final NumberAxis yAxis3 = new NumberAxis();
        yAxis3.setLabel("ACCURACY(%)");
        
        digit_recognized_graph = new LineChart<Number, Number>(xAxis3, yAxis3);
        digit_recognized_graph.setAnimated(true);
        digit_recognized_graph.setMaxSize(20, 20);
        digit_recognized_graph.setMaxSize(400, 400);
        digit_recognized_graph.setPrefSize(300, 600);
        digit_recognized_graph.legendVisibleProperty().set(true);
        
        digit_recognized_graph.setTitle("AVERAGE ACCURACY OF THE NETWORK");
        for(int i=0; i < digit_recognized_data.length; i++){
            digit_recognized_data[i] = new XYChart.Series();
            digit_recognized_data[i].setName(Configuration.object_type.toUpperCase() + ": " + ObjectMap.getOutput(i));
            digit_recognized_graph.getData().add(digit_recognized_data[i]);
        }
        
        final NumberAxis xAxis4 = new NumberAxis();
        xAxis4.setLabel("ACCURACY(%)");
        final NumberAxis yAxis4 = new NumberAxis();
        yAxis4.setLabel("CONFIDENCE(%)");
        
        expertise_graph = new ScatterChart<Number, Number>(xAxis4, yAxis4);
        expertise_graph.setAnimated(false);
        expertise_graph.setMaxSize(20, 20);
        expertise_graph.setMaxSize(400, 400);
        expertise_graph.setPrefSize(300, 600);
        expertise_graph.legendVisibleProperty().set(true);
        
        expertise_graph.setTitle("GROWTH OF THE NEURAL NETWORK");
        for(int i=0; i < expertise_data.length; i++){
            expertise_data[i] = new XYChart.Series();            
            expertise_data[i].setName(Configuration.object_type.toUpperCase() + ": " + ObjectMap.getOutput(i));
            expertise_data[i].getData().add(new XYChart.Data(0, 0));
            expertise_graph.getData().add(expertise_data[i]);
        }
        
    }
    public void createScrollers(){
        
     output_scroller = new ScrollPane();
     output_scroller.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
     output_scroller.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
     output_scroller.setContent(output_box);
           
    }
    public void createImageView(){
        input_image = new ImageView();
        input_image.setFitWidth(Configuration.image_width);
        input_image.setFitHeight(Configuration.image_height);
        tick_wrong_image = new ImageView();
        
    }
    public void createDataFields(){
        inode = new Text("N/A");
        onode = new Text("N/A");
        iweight = new Text("N/A");
        oweight = new Text("N/A");
        ibias = new Text("N/A");
        obias = new Text("N/A");
        learning_rate = new Text("N/A");
        output_val = new Text("N/A");
        hnode = new Text("N/A");
        hlayer = new Text("N/A");
        
        for(int i=1; i < probable_outputs.length; i++){
            probable_outputs[i] = new Text(ObjectMap.getOutput(i-1) + ": 0%");
        }
        
        inode.setFont(MainView.content_font);
        onode.setFont(MainView.content_font);
        iweight.setFont(MainView.content_font);
        oweight.setFont(MainView.content_font);
        ibias.setFont(MainView.content_font);
        obias.setFont(MainView.content_font);
        learning_rate.setFont(MainView.content_font);
        output_val.setFont(MainView.content_font);
        hnode.setFont(MainView.content_font);
        hlayer.setFont(MainView.content_font);
        
        inode.setFill(MainView.content_color);
        onode.setFill(MainView.content_color);
        iweight.setFill(MainView.content_color);
        oweight.setFill(MainView.content_color);
        ibias.setFill(MainView.content_color);
        obias.setFill(MainView.content_color);
        learning_rate.setFill(MainView.content_color);
        output_val.setFill(MainView.content_color);
        hnode.setFill(MainView.content_color);
        hlayer.setFill(MainView.content_color);
        for(int i=1; i < probable_outputs.length; i++){
            probable_outputs[i].setFill(MainView.content_color_inverse);
            probable_outputs[i].setFont(MainView.content_font);
        }
        
    }
    public void createLabels(){
        learning_rate_slider_label = new Text("LEARNING RATE: ");
        input_title = new Text("INPUT");        
        output_title = new Text("OUTPUT");
        hidden_title = new Text("Hidden Layers");
        inode_label = new Text("Nodes: ");
        onode_label= new Text("Nodes: ");
        iweight_label = new Text("Weights: ");
        oweight_label = new Text("Weights: ");
        ibias_label = new Text("Bias: ");
        obias_label = new Text("Bias: ");
        learning_rate_label = new Text("Learning rate: ");
        output_val_label = new Text("Output: ");
        hnode_label = new Text("Nodes per layer: ");
        hlayer_label = new Text("Hidden layers: ");
        probable_outputs[0] = new Text("Descending order of probable outputs");
        
        learning_rate_slider_label.setFont(header_font);
        input_title.setFont(header_font);
        output_title.setFont(header_font);
        hidden_title.setFont(header_font);
        inode_label.setFont(header_font);
        onode_label.setFont(header_font);
        iweight_label.setFont(header_font);
        oweight_label.setFont(header_font);
        ibias_label.setFont(header_font);
        obias_label.setFont(header_font);
        learning_rate_label.setFont(header_font);
        output_val_label.setFont(header_font);
        hnode_label.setFont(header_font);
        hlayer_label.setFont(header_font);
        probable_outputs[0].setFont(header_font);
        
        learning_rate_slider_label.setFill(MainView.title_color);
        input_title.setFill(MainView.header_color);
        output_title.setFill(MainView.header_color);
        hidden_title.setFill(MainView.header_color);
        inode_label.setFill(MainView.header_color);
        onode_label.setFill(MainView.header_color);
        iweight_label.setFill(MainView.header_color);
        oweight_label.setFill(MainView.header_color);
        ibias_label.setFill(MainView.header_color);
        obias_label.setFill(MainView.header_color);
        learning_rate_label.setFill(MainView.header_color); 
        output_val_label.setFill(MainView.header_color); 
        hnode_label.setFill(MainView.header_color);
        hlayer_label.setFill(MainView.header_color);
        probable_outputs[0].setFill(MainView.header_color_inverse);
        
        
    }
    public void createColumnConstraints(){
        for(int i=0; i < this.column_constraints.length; i++){
            column_constraints[i] = new ColumnConstraints();
            column_constraints[i].setPercentWidth(20);
        }
    }
    
    @Override
    public boolean enableView(ContentPane root){
        if(root.current_view != this.view_index){
           /* for(int i=0; i < this.column_constraints.length; i++){
                root.center.getColumnConstraints().add(this.column_constraints[i]);
            }*/
           root.center.add(container_title, 0, 0, 10, 1);
           
           root.center.add(input_title, 0, 1, 1, 1);           
           root.center.add(this.inode_label, 0, 2, 1, 1);
           root.center.add(this.iweight_label, 0, 3, 1, 1);
           root.center.add(this.ibias_label, 0, 4, 1, 1);
           root.center.add(this.learning_rate_label, 0, 5, 1, 1);
           
           root.center.add(this.inode, 1, 2, 1, 1);
           root.center.add(this.iweight, 1, 3, 1, 1);
           root.center.add(this.ibias, 1, 4, 1, 1);
           root.center.add(this.learning_rate, 1, 5, 1, 1);
           
           root.center.add(this.input_image, 2, 2, 1, 1);
           root.center.add(this.learning_rate_slider, 2, 5, 1, 1);
           
           root.center.add(this.output_title, 3, 1, 1, 1);
           root.center.add(this.onode_label, 3, 2, 1, 1);
           root.center.add(this.oweight_label, 3, 3, 1, 1);
           root.center.add(this.obias_label, 3, 4, 1, 1);
           root.center.add(this.output_val_label, 3, 5, 1, 1);
           
           root.center.add(this.onode, 4, 2, 1, 1);
           root.center.add(this.oweight, 4, 3, 1, 1);
           root.center.add(this.obias, 4, 4, 1, 1);
           root.center.add(this.output_val, 4, 5, 1, 1);
           
           root.center.add(this.tick_wrong_image, 5, 5, 1, 1);   

           root.center.add(this.output_scroller, 6, 1, 1, 4);
           
           root.center.add(box, 0, 6, 10, 1);          
           
           root.center.add(box2, 0, 7, 10, 1);
           
           
           
           
           
           

            root.current_view = this.view_index;
            return true;
        }
        return false;
    }
    @Override
    public boolean disableView(ContentPane root){
        if(root.current_view == this.view_index){
            root.center.getChildren().removeAll(container_title, input_title, output_title, inode_label);
            root.center.getChildren().removeAll(inode, input_image, onode_label, onode, tick_wrong_image, output_scroller);
            root.center.getChildren().removeAll(obias, learning_rate_label, learning_rate, learning_rate_slider, output_val_label, output_val, box, box2);
            root.center.getChildren().removeAll(iweight_label, iweight, oweight_label, oweight, ibias_label, ibias, obias_label);
            
           /* for(int i=0; i < this.column_constraints.length; i++){
                root.center.getColumnConstraints().remove(this.column_constraints[i]);
            }*/
            return true;
        }
        return false;
        
    }
}
