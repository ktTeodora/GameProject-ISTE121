// Authors: Matea Boderistanac & Doroteja Krtalic
// Course: ISTE-121
// Class: A class to create the basic game movement
// Date: 02/23/2022

/**
* JavaFX Symple GUI, example 1.
* 
* @author David Patric, Alan Mutka
* @version 2205
*/

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.*;

// game controls class
public class Game extends Application {

    // Constant
    public static final int RES_WIDTH = 1138;
    public static final int RES_HEIGHT = 783;

    // Attributes
    static Stage stage;
    static GameScene currentScene;
    // taking the player choice to ChosenPlayer class to start the game with the
    // right character
    // choose player attribute
    static PlayerCharacter chosenPlayer;
    static String username;
    static String ipdAddress = "localhost";

    static int currentLevel = 1;

    public static void main(String[] args) {
        // method inside the Application class, it will setup our program as a JavaFX
        // application
        // then the JavaFX is ready, the "start" method will be called automatically
        launch(args);

    } // end of main

    /**
     * starts the program
     * 
     * @param _stage the stage
     * @throws Exception
     */
    // start method
    @Override
    public void start(Stage _stage) throws Exception {

        stage = _stage;

        ///////////////////////// Setting window properties
        // set the window title
        _stage.setTitle("Imanji - The Frozen Tale");
        _stage.setResizable(false);

        // an animation timer that will update the game loop
        new AnimationTimer() {

            @Override
            public void handle(long arg0) {

                // always call update on the current scene
                if (currentScene != null) {

                    currentScene.update();
                } // ond of if statement for current scene

            } // handle end

        }.start(); // animation timer end

        // set the start scene to the frozen map scene
        switchScene(new Menu());

    } // end of start

    /**
     * switches the scene
     * 
     * @param scene scene
     */
    // method that switches the scenes

    // static method that is not bound by the game object instance
    public static void switchScene(GameScene scene) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                // connect stage with the Scene and show it, finalization
                stage.setScene(scene.getScene());
                stage.show();
                currentScene = scene;

            }

        }); // end of platform run later

    } // end of switch scene method

    /**
     * sets the chosen player
     * 
     * @param _chosenPlayer the chosen player
     */
    // mutators for setting and getting the result of the "choose player" action
    // setter
    public static void setChosenPlayer(PlayerCharacter _chosenPlayer) {

        chosenPlayer = _chosenPlayer;
    } // setter end

    /**
     * gets the chosen player
     * 
     * @return PlayerCharacter the chosen player
     */
    // getter
    public static PlayerCharacter getChosenPlayer() {

        return chosenPlayer;
    } // getter end

    /**
     * gets current scene
     * 
     * @return GameScene the current scene
     */
    // current scene getter
    // we don't hve a handle to the game class, and the variable is static
    public static GameScene getCurrentScene() {

        return currentScene;
    }

    /**
     * sets username
     * 
     * @param _username name to set
     */
    // setter and getter for username string
    // set
    public static void setUsername(String _username) {

        username = _username;
    } // setter end

    /**
     * gets the username
     * 
     * @return String username
     */
    // get
    public static String getUsername() {

        return username;
    } // getter end

    /**
     * Sets the IP Address
     * 
     * @param _ipAddress The IP to set
     */
    public static void setIPAddress(String _ipAddress) {

        ipdAddress = _ipAddress;
    } // setter end

    /**
     * Gets the IP Address
     * 
     * @return String IP Address
     */
    public static String getIPAddress() {

        return ipdAddress;
    }

    /**
     * Sets the current level
     * 
     * @param _currentLevel the current level to set
     */
    public static void setCurrentLevel(int _currentLevel) {

        currentLevel = _currentLevel;
    } // setter end

    /**
     * Gets the current level
     * 
     * @return int current level
     */
    public static int getCurrentLevel() {

        return currentLevel;
    } // getter end

} // end of main method