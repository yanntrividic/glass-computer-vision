# glass-computer-vision
A Java 12 Maven project using OpenCV to measure the relative quantity of a fluid contained in a glass.  

**Version:** 1.0  
**Authors:** Cyril Dubos, Thomas Jouen, Erwan Lacoudre and Yann Trividic  
**Context:** This program was developed for the Image Processing course of the Université de Paris. The teacher for the course was Pr. Nicole Vincent.  
**License:** MIT License

![Main window of the app](https://raw.githubusercontent.com/yanntrividic/glass-computer-vision/main/good_evaluation.png)

## Contents

This repository contains three folders:
* `src`, the source code of the project,
* `resources`, three image sets (training, validation, test),
* `doc`, the Javadoc of the project, compiled into a set of HTML files.


## Minimum configuration required

For this project to run on your machine, you'll need to have installed Java SE 12.


## Run the program

Once the JAR file is downloaded from the `tags` section of this page, open a terminal and change your directory to the folder where the file is. To run the file, execute the following command : `java -jar FILENAME.jar`. By doing so, the program starts and loads the training dataset. You can change the image set by adding a parameter at the end of the command. Three possibilities are available : `train` for the training set, `validation` for the validation set, `test` for the testing set.

For example, if you want to see the unbiased results of our program, please use this command `java -jar FILENAME.jar test` and see how we did for yourself.

TODO: explain that the image sets must be downloaded


## User instructions

The program's window is divided in four parts:
* The **top** of the window is where will be displayed textual information about the image currently selected,
* The **center** part holds the selected image and displays the resulting image after processing,
* The **right** part contains sliders that are parameters for our algorithms. Please refer to the code documentation to understand them fully.
* The **bottom** of the window contains the buttons that the user can click to compute the image or to change the selected image.

Changing the value of the sliders about the `ELLIPSE PARAMETERS` labels will result into the display of the intermediary process related to this parameter. For example, if the changes the value of one of the masking parameters' sliders, the new mask will be displayed directly. It implies that if the user wants to see the full result (ellipse and evalution) one of the `Compute image` buttons must be clicked.

The evaluation (...) [@Erwan]


## References

ADD BIBLIO