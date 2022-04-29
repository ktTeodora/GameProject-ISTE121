/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: An abstract class for the game scenes
* Date: 02/23/2022
*/

import javafx.scene.Scene;

public abstract class GameScene {

    // only the classes extending this class can access the scene!!
    protected Scene scene;

    // constructor
    public GameScene() {

        setupScene();
    } // constructor end

    // *********************** METHODS *************************

    // any class that extends this class must implement setupScene!!
    // protected so that only the classes managing the scene can access it!!
    protected abstract void setupScene();

    // publi so that game class can access the update game scene method!!
    public abstract void update();

    // getter for Scene
    public Scene getScene() {

        return scene;
    } // getter end

} // end of game scene class
