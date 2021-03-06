# JavaObjectRecognition
This is a demonstration of Java based Object recognition using MultiLayered Perceptron. The Project is capable of recognizing images through supervised machine learning algorithm. It also contains a Graphical User Interface to monitor the status of the network(e.g. graphical representation of changing weights and biases) and analysis output of the network during training mode(e.g. changing accuracy of the network, changing accuracy of individual output class, input data status, prediction accuracy for each output, etc.). Data in the GUI is updated with each adaptation of the network. Algorithm for the entire network and its functionality is developed from scratch which provides a practical idea about developing a simple Neural Network for pattern recognition.

1. Dataset Structure:
Dataset for the network consist image files(png, jpg, etc.) arranged in a structured directory.

a. The base dataset folder(any name can be given) for a specific type of Classification(e.g. 'digit' folder in the sample dataset for digit recognition) contains two types of data: input image dataset(in 'td') and network's weight and biases(in 'wab' folder) saved while being trained on that specific set of images. Therefore, all data for each new classification are stored in separate directories which enables the network to perform multiple classification just by changing the name of base dataset directory.

b. 'td' folder inside the base dataset directory contains image data of different classes on which the network needs to be trained. The 'td' folder consists of multiple sub-directories named sequentially starting from 0 to n where n is the total number of output classes for the network. Each sub-directory contains images named sequentially from 0 to m where m is the number of images for the specific class. The images must be of same dimension which should be specified in the Configuration.java file of the objectrecognition package. This directory should be included in the base dataset diectory before the networks starts to train.

b. 'wab' folder stores the weights and biases of a classifier after being trained. So, different classification parameters will be stored in different directories by the same network. In this way a single network can train on different type of classification. The base 'wab' folder contains two directories 'w' and 'b' which contains data of the weights and biases of the network respectively. Each file inside these directories contain information about each layer of the network(numbered from zero onwards). These data is generated by the network automatically if not found.
2. Software Packages:
JavaFX, opengl
3. Software Description:
a. Parameters for the network and dataset can be edited in src/objectrecognition/Configuration. 
b. Main file of the application is in src/neuralnetwork/NeuralNetwork.
c. A sample dataset is included in 'digit' folder of the base directory.
