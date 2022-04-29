/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the how to play scene
* Date: 02/23/2022
*/

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

public class HowToPlay extends GameScene {

    @Override
    protected void setupScene() {

        BorderPane root = new BorderPane();

        // create a scene with a specific size (width, height), connnect with the layout
        scene = new Scene(root, Game.RES_WIDTH, Game.RES_HEIGHT);

        Image howToPlay = new Image("file:graphics/HowToPlay.gif");
        ImageView howToPlayView = new ImageView(howToPlay);

        // add to the root
        root.getChildren().add(howToPlayView);

        // return to the menu scene on "Esc" key press
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent _keyEvent) {

                KeyCode code = _keyEvent.getCode();
                // if "Esc", switch to Menu
                if (code == KeyCode.ESCAPE) {

                    Game.switchScene(new Menu());
                }
            } // end of handle for return to menu

        }); // end of on key pressed to return to menu

    } // end of set up scene method

    @Override
    public void update() {

    }

} // end of how to play class
