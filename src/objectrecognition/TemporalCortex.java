/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import commontools.ApplicationTools;
import java.time.LocalDateTime;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import objectrecognition.ContentPane;

/**
 *
 * @author User
 */
public class TemporalCortex {

    private ContentPane root;
    private LocalDateTime current;
    private LocalDateTime start;
    private long[] run_time;
    private boolean reading_time = false;
    public TemporalCortex(){
        start = ApplicationTools.getTime();
        
    }
    public LocalDateTime getTime() {        
        reading_time = true;
        LocalDateTime res = current;
        reading_time = false;
        return res;
    }

    public void setPane(ContentPane root) {
        this.root = root;
    }

    public void run() {
        if (!this.reading_time) {
            current = ApplicationTools.getTime();
            run_time = ApplicationTools.timeElapsedLong(start, current);
            final String res = ApplicationTools.formatTime(current);
            final String ran = run_time[0] + "Y-" + run_time[1] + "M-" + run_time[2] + "D -- " + run_time[3] + "h:" + run_time[4] + "m:" + run_time[5] + "s";
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    root.time.setText(res); //Set current time
                    root.run_time.setText(ran); //Set Run time
                }
            });
        }

    }

}
