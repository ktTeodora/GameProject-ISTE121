import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.VBox;

public class Menu extends GameScene {

    @Override
    protected void setupScene() {

        // root pane
        VBox root = new VBox(8);
        root.setAlignment(Pos.CENTER);

        // background image
        Image menuBackground = new Image("file:graphics/StartUpMenu.gif", Game.RES_WIDTH, Game.RES_HEIGHT, true, true);
        BackgroundImage menuBgImage = new BackgroundImage(menuBackground, BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        Background menuBgGround = new Background(menuBgImage);

        // setting the background
        root.setBackground(menuBgGround);

        // button text images
        Image startGame = new Image("file:graphics/StartGame.png", 200, 80, true, true);
        ImageView startGameView = new ImageView(startGame);
        Image howToPlay = new Image("file:graphics/HowToPlay.png", 200, 80, true, true);
        ImageView HowToPlayView = new ImageView(howToPlay);
        Image exit = new Image("file:graphics/Exit.png", 200, 80, true, true);
        ImageView exitView = new ImageView(exit);

        // create a scene with a specific size (width, height), connnect with the layout
        scene = new Scene(root, Game.RES_WIDTH, Game.RES_HEIGHT);

        // css stylesheet
        scene.getStylesheets().add("style.css");

        // GUI elements && funtionality
        Button btnGameStart = new Button("", startGameView);
        btnGameStart.getStyleClass().add("startBtn");
        btnGameStart.setPrefWidth(90);
        btnGameStart.setPrefHeight(35);
        btnGameStart.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                // takes us to Choose Player menu
                Game.switchScene(new ChoosePlayer());

            }

        }); // end of start button event

        Button btnHowToPlay = new Button("", HowToPlayView);
        btnHowToPlay.getStyleClass().add("howToPlayBtn");
        btnHowToPlay.setPrefWidth(90);
        btnHowToPlay.setPrefHeight(35);
        btnHowToPlay.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                // takes us to How to Play menu
                Game.switchScene(new HowToPlay());
            }

        }); // end of how to play button event

        Button btnExit = new Button("", exitView);
        btnExit.getStyleClass().add("exitBtn");
        btnExit.setPrefWidth(90);
        btnExit.setPrefHeight(35);
        btnExit.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent arg0) {

                System.exit(0);
            }

        }); // end of exit button event

        root.getChildren().addAll(btnGameStart, btnHowToPlay, btnExit);

    } // end of setup scene method

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

} // end of menu class
