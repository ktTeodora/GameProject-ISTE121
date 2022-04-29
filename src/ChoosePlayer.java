import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class ChoosePlayer extends GameScene {

    @Override
    protected void setupScene() {

        // root
        GridPane root = new GridPane();
        // customize root
        root.setAlignment(Pos.CENTER);
        root.setVgap(5);
        root.setHgap(10);

        // create a scene with a specific size (width, height), connnect with the layout
        scene = new Scene(root, Game.RES_WIDTH, Game.RES_HEIGHT);

        // css stylesheet
        scene.getStylesheets().add("style.css");

        // GUI items
        Label lblUsername = new Label("Username:");
        TextField tfUsername = new TextField();
        Label lblIP = new Label("IP Address:");
        TextField tfIP = new TextField("localhost");
        // pingu
        Image pingu = new Image("file:graphics/frontPinguStill.gif", 100.0, 100.0, true, true);
        ImageView pinguView = new ImageView(pingu);
        Button btnPingu = new Button("", pinguView);
        btnPingu.getStyleClass().add("pinguBtn");
        // game start with the selected character
        btnPingu.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                Game.setCurrentLevel(1);
                // set the chosen character
                Game.setChosenPlayer(PlayerCharacter.PENGUIN);
                // setting the username
                Game.setUsername(tfUsername.getText());
                // setting the IP address
                Game.setIPAddress(tfIP.getText());
                // set to game map
                Game.switchScene(new FrozenMapScene());
            }

        }); // end of btn set on action event

        // medo
        Image medo = new Image("file:graphics/bearStill.gif", 100.0, 100.0, true, true);
        ImageView medoView = new ImageView(medo);
        Button btnMedo = new Button("", medoView);
        btnMedo.getStyleClass().add("medoBtn");
        // game start with the selected character
        btnMedo.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                Game.setCurrentLevel(1);
                // set the chosen character
                Game.setChosenPlayer(PlayerCharacter.BEAR);
                // setting the username
                Game.setUsername(tfUsername.getText());
                // setting the IP address
                Game.setIPAddress(tfIP.getText());
                // set to game map
                Game.switchScene(new FrozenMapScene());
            }

        }); // end of btn set on action event

        // adding elements to the root
        root.add(btnPingu, 0, 0);
        root.add(btnMedo, 1, 0);
        root.add(lblUsername, 0, 1);
        root.add(tfUsername, 1, 1);
        root.add(lblIP, 0, 2);
        root.add(tfIP, 1, 2);

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

    } // end of setUpScene method

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

} // end of ChoosePlayer class
