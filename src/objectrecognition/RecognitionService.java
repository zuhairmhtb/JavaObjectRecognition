/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package objectrecognition;

import java.io.File;
import java.util.ArrayList;
import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import trainingdata.ImageData;

/**
 *
 * @author User
 */
public class RecognitionService extends Service<Void> {

    private WelcomeView welcome_view;

    private ObjectRecognition or;
    private ContentPane root;
    private MainView main_view;
    private ConnectionView connection_view;

    private DemoRun demo;
    private Configuration conf;

    public final void setConfig(WelcomeView welcome_view, MainView main_view, ConnectionView connection_view, ObjectRecognition or, ContentPane root, DemoRun demo, Configuration conf) {
        this.welcome_view = welcome_view;
        this.connection_view = connection_view;

        this.or = or;
        this.root = root;
        this.main_view = main_view;

        this.demo = demo;
        this.conf = conf;
    }

    @Override
    protected Task<Void> createTask() {
        return new Task<Void>() {
            public void updateProgress(double end) throws Exception {
                for (double i = welcome_view.welcome_progress.getProgress(); i <= end; i += 0.001) {
                    welcome_view.welcome_progress.setProgress(i);
                    Thread.sleep(10);
                }
            }

            public void updateNotification(String text) {
                welcome_view.progress_notification.setText(text);
            }

            public void print(String text) {

                root.displayInOutput(text);
                System.out.println(text);

            }

            @Override
            protected Void call() throws Exception {
                updateNotification("Preparing training data for the neural network/-");
                conf.prepareTrainDataset(conf);
                updateProgress(0.4);

                updateNotification("Preparing test data for recognition of " + conf.object_type + "/-");
                conf.createTestDataset(conf);
                updateProgress(0.6);

                updateNotification("Creating neural network of the application/-");
                or = new ObjectRecognition(true, root, main_view, conf);
                updateProgress(0.8);

                updateNotification("Creating multilayered perceptron for the neural network/-");
                or.configureMLP();
                updateProgress(1);

                print("Loaded test images from " + conf.test_image_dir + " of size " + conf.test_data.size());
                updateNotification("Preparing to run neural network...Please wait");
                demo.setConfig(or, root, main_view, connection_view, conf);
                print("Enabling main view of the interface");
                //demo_thread.start();
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        if (root.current_view != main_view.view_index) {
                            for (MyView view : root.views) {
                                if (root.current_view == view.view_index) {
                                    print("Disabling welcome view");
                                    view.disableView(root);
                                    break;
                                }
                            }
                            print("Enabling main view");
                            main_view.enableView(root);

                        }
                    }
                });

                print("Running demo");
                demo.run();
                return null;
            }
        };
    }

}
