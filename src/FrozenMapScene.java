/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the frozen map scene
* Date: 02/23/2022
*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class FrozenMapScene extends GameScene {

    // Attributes
    private GraphicsContext gc;
    Canvas canvas;
    Player localPlayer;
    Player remotePlayer;
    ArrayList<Ghost> ghosts;
    TextArea taChat;
    TextField tfChat;

    // items count and images
    // ice cube
    int iceCubeCount;
    Image iceCubesCount = new Image("file:graphics/IceCubeCollect.png", 35.0, 35.0, true, true);
    // fish
    int totalFishCount = 0;

    // ArrayList<Collectibles> collectibles;

    // create separate arrays for each item
    ArrayList<IceCube> iceCubeArray;
    ArrayList<Fish> fishArray;
    ArrayList<Gingerbread> gingerbreadArray;
    ArrayList<Snowball> snowballArray;
    Skates skates;
    ArrayList<ThrowballSnowball> throwSnowballs;

    // stops the loading screen
    Boolean finishedLoading = false;

    // boolean to stop duplicate timers from starting
    boolean returnToMenuTimerRunning = false;

    // client thread connection
    ClientThread cThread;
    boolean isHost;

    Image background = new Image("file:graphics/MapRevised.png");
    public static Image collisionMap = new Image("file:graphics/CollisionMap2.png");
    Image loadingScreen = new Image("file:graphics/LoadingScreen.gif");
    Image win = new Image("file:graphics/YouWin.gif");
    Image lose = new Image("file:graphics/YouLose.gif");
    Image level2 = new Image("file:graphics/Level2.png");

    @Override
    protected void setupScene() {

        // create the Layout where we can put scene elements, the main layout
        // layout are used for automatic positioning elements inside the scene, for
        // example, TOP, LEFT, RIGHT, CENTER
        BorderPane root = new BorderPane();

        // canvas
        canvas = new Canvas(Game.RES_WIDTH, Game.RES_HEIGHT);

        // using input on the canvas
        // text field is otherwise taking the input!!
        canvas.requestFocus();
        // when the canvas is clicked on, it will get input focus
        canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, (e) -> canvas.requestFocus());
        canvas.setFocusTraversable(false);

        // get graphics content from the canvas so that we can manipulate the elements
        // in it
        this.gc = canvas.getGraphicsContext2D();

        // adding VBox to the border pane to implement the chat
        VBox chatBox = new VBox();

        // setting up a text area for chat
        taChat = new TextArea();
        taChat.setEditable(false);
        // stops the arrow keys from putting the focus on the chat
        taChat.setFocusTraversable(false);
        // set the height so we have space for the input area at the bottom
        taChat.setMinHeight(Game.RES_HEIGHT - 20);
        // input text field
        tfChat = new TextField();
        // stops the arrow keys from putting the focus on the chat
        tfChat.setFocusTraversable(false);

        chatBox.getChildren().addAll(taChat, tfChat);

        // create a scene with a specific size (width, height), connnect with the layout
        scene = new Scene(root, Game.RES_WIDTH + 200, Game.RES_HEIGHT);

        // adding the canvas to the root
        // setting the canvas to the left of the border pane - setting the chat to the
        // right of the border pane
        root.setLeft(canvas);
        root.setRight(chatBox);

        // load the game faster by adding this separate thread to load the game objects
        Thread loadScene = new Thread(new Runnable() {

            public void run() {

                long startMillis = System.currentTimeMillis();

                // adding player

                // TESTING FOR CHARACTERS
                localPlayer = new Player(scene, Game.getChosenPlayer(), true);
                // localPlayer = new Player(scene, ChoosePlayer.BEAR);

                // instantiate the ghosts array right before adding the ghosts to it
                ghosts = new ArrayList<>();

                // adding the ghosts
                ghosts.add(new Ghost(new Point2D(570, 740), GhostColour.LILAC));
                ghosts.add(new Ghost(new Point2D(960, 110), GhostColour.VANILLA));
                ghosts.add(new Ghost(new Point2D(100, 180), GhostColour.BLUE));
                ghosts.add(new Ghost(new Point2D(80, 700), GhostColour.TEAL));
                ghosts.add(new Ghost(new Point2D(1000, 600), GhostColour.PINK));

                spawnCollectibles();

                finishedLoading = true;

                long endMillis = System.currentTimeMillis();

                long loadingTimeSeconds = (endMillis - startMillis) / 1000;

                System.out.println("Loading time: " + loadingTimeSeconds + " seconds.");

                // connecting this client to the server and sending the connection message
                // through the sendConnection method to the server
                cThread = new ClientThread();
                cThread.start();
                cThread.sendConnect();

                System.out.println("Client started - waiting for player 2");

            } // end of run for loadScene thread

        }); // end of Thread to spawn collectibles

        loadScene.start();

        // adding collectibles
        // spawnCollectibles();

    } // end of the method that sets up the scene

    // method to spawn collectibles onto the map
    public void spawnCollectibles() {

        // instantiate the array list for collectibles
        // collectibles = new ArrayList<>();

        // instantiate the array lists for all collectible items
        iceCubeArray = new ArrayList<>();
        gingerbreadArray = new ArrayList<>();
        snowballArray = new ArrayList<>();
        fishArray = new ArrayList<>();
        throwSnowballs = new ArrayList<>();

        // looping through all X pixels on the map
        for (int x = 1; x < Game.RES_WIDTH; x++) {

            // looping through all Y pixels on the map
            for (int y = 1; y < Game.RES_HEIGHT; y++) {

                // variable to store the pixel colour for pixelReader
                Color pixelColor = collisionMap.getPixelReader().getColor(x, y);

                // if the pixel is pink, spawn an ice cube
                if (pixelColor.toString().equals("0xff00ffff")) {

                    // add ice cubes to the array
                    iceCubeArray.add(new IceCube(new Point2D(x, y)));
                } // end of if check of the pixel colour - pink

                // if the pixel is cyan, spawn a gingerbread
                if (pixelColor.toString().equals("0x00ffeeff")) {

                    // add gingerbreads to the array
                    gingerbreadArray.add(new Gingerbread(new Point2D(x, y)));
                } // end of if check of the pixel colour - cyan

                // if the pixel is yellow, spawn a snowball
                if (pixelColor.toString().equals("0xfff700ff")) {

                    // add snowball to the array
                    snowballArray.add(new Snowball(new Point2D(x, y)));
                }

                // if the pixel is green, spawn a fish
                if (pixelColor.toString().equals("0x22ff00ff")) {

                    // add fish to the array
                    fishArray.add(new Fish(new Point2D(x, y)));
                }

                // if the pixel is blue, spawn the skates
                if (pixelColor.toString().equals("0x001effff")) {

                    // add skates
                    skates = new Skates(new Point2D(x, y));
                }

            } // end of loop through pixels on the y-axis

        } // end of loop through pixels on the x-axis

    } // end of method that spwans the collectibles

    @Override
    public void update() {

        // checking if the map has finished loading everything && the remote player has
        // connected - drawing the loading
        // screen until it does!!
        if (!finishedLoading || remotePlayer == null) {

            gc.drawImage(loadingScreen, 0, 0);

            // don't update anything else if not finished loading
            return;
        } // end of if-check for loading the map

        // drawing the map
        gc.drawImage(background, 0, 0);

        // terminate if player's lives equal to 0
        if (localPlayer.getLives() == 0) {

            gc.drawImage(lose, 0, 0);

            if (!returnToMenuTimerRunning) {

                cThread.sendDisconnect();

                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {

                        Game.switchScene(new Menu());
                        ;
                    }
                };

                timer.schedule(task, 15000);

                returnToMenuTimerRunning = true;

            }

            return;
        } // end of life check

        // end the game if the player has won
        if (localPlayer.getWin() == true) {

            gc.drawImage(win, 0, 0);

            if (!returnToMenuTimerRunning) {

                cThread.sendDisconnect();

                Timer timer = new Timer();
                TimerTask task = new TimerTask() {

                    @Override
                    public void run() {
                        if (Game.getCurrentLevel() == 2) {

                            Game.switchScene(new Menu());
                        } else {

                            Game.setCurrentLevel(2);
                            Game.switchScene(new FrozenMapScene());

                        }
                    }
                };

                timer.schedule(task, 15000);

                returnToMenuTimerRunning = true;

            }

            return;
        } // end of win check

        // ********** FOR COLLISION TESTING ***********
        // gc.drawImage(collisionMap, 0, 0);

        // loop to update all collectibles
        // for (int i = 0; i < collectibles.size(); i++) {

        // collectibles.get(i).update(gc, localPlayer);
        // } // spawning all collectibles in the array

        // loop to update all arrays for collectible items
        // ice cubes
        for (int i = 0; i < iceCubeArray.size(); i++) {

            iceCubeArray.get(i).update(gc, localPlayer, remotePlayer);
        }
        // gingerbread
        for (int i = 0; i < gingerbreadArray.size(); i++) {

            gingerbreadArray.get(i).update(gc, localPlayer, remotePlayer);
        }
        // snowball
        for (int i = 0; i < snowballArray.size(); i++) {

            snowballArray.get(i).update(gc, localPlayer, remotePlayer);
        }
        // fish
        for (int i = 0; i < fishArray.size(); i++) {

            fishArray.get(i).update(gc, localPlayer, remotePlayer);
        }
        // skates
        skates.update(gc, localPlayer, remotePlayer);

        for (int i = 0; i < throwSnowballs.size(); i++) {

            throwSnowballs.get(i).update(gc);
        }

        // updating the player's position and draws them on canvas
        localPlayer.update(gc);

        // sending information from our local player to the server so it can be
        // replicated to other clients!!!
        if (cThread != null) {

            cThread.sendUpdatePlayer(localPlayer);
        }

        // checking if there is a 2nd player
        if (remotePlayer != null) {

            remotePlayer.updateRemote(gc);
        } // end of if to check for player 2

        // loop through the ghosts array
        for (int i = 0; i < ghosts.size(); i++) {

            // only the host controls the ghost movement
            if (isHost) {

                ghosts.get(i).update(gc, localPlayer);

                cThread.sendGhostUpdate(ghosts.get(i), i);
            } else {

                ghosts.get(i).updateRemote(gc, localPlayer);
            }
        } // drawing all ghosts in the array loop end

        // keeping ice cubes count on the screen
        gc.drawImage(iceCubesCount, 42, 47 - iceCubesCount.getHeight());
        gc.fillText("" + iceCubeCount, 80, 35);

        if (Game.currentLevel == 2) {

            gc.drawImage(level2, 0, 0);
        }

    } // end of update

    /************************* ADD ICECUBE **************************/
    public void addIce() {

        iceCubeCount++;

        if (iceCubeCount == iceCubeArray.size()) {

            // ************************ GAME TESTING *************************
            // if (iceCubeCount == 1) {

            for (int i = 0; i < fishArray.size(); i++) {

                // once all the ice cubes are picked up, reveal the fish
                fishArray.get(i).pickedUp = false;
            }
        }
    } // end of the add ice method

    // ******************* ADD FISH COUNT *********************
    // this is the total fish count of both players combined which is used to check
    // the win condition
    public void addFish() {

        totalFishCount++;
    }

    /**
     * Returns the total amount of fish between the two players
     * 
     * @return int the total fish
     */
    // ********************** GETTER FOR FISH COUNT **********************
    public int getFishCount() {

        return totalFishCount;
    } // end of get fish count

    /**
     * Returns the list of ghosts
     * 
     * @return ArrayList<Ghost> list of ghosts
     */
    // ************************ GETTER FOR GHOSTS ARRAY ***************************
    public ArrayList<Ghost> getGhosts() {

        return ghosts;
    }

    /**
     * Adds a new snowball to the thrown snowballs list
     * 
     * @param position start position
     * @param xSpeed   x speed
     * @param ySpeed   y speed
     */
    // ************************ THROW SNOWBALL **************************
    public void throwBall(Point2D position, double xSpeed, double ySpeed) {

        throwSnowballs.add(new ThrowballSnowball(position, xSpeed, ySpeed));
    }

    /**
     * Called when the snowball hits an object so we can destroy it
     * 
     * @param snowball the snowball
     */
    // ************************* ON HIT ****************************
    public void onBallHit(ThrowballSnowball snowball) {

        throwSnowballs.remove(snowball);
    }

    // ************************* ANNONYMOUS INNER CLASS ***************************
    // ClientThread class
    class ClientThread extends Thread {

        // Attributes
        Socket cSocket;
        DataInputStream dis;
        DataOutputStream dos;

        // constructor
        public ClientThread() {

            // we call the connect method immediately, so the thread may not have started by
            // then
            // moved to the constructor from the run method

            try {
                cSocket = new Socket(Game.getIPAddress(), Server.PORT_NUMBER);

                dos = new DataOutputStream(new BufferedOutputStream(cSocket.getOutputStream()));
                dis = new DataInputStream(new BufferedInputStream(cSocket.getInputStream()));
            } catch (UnknownHostException uhe) {

                uhe.printStackTrace();
            } catch (IOException ioe) {

                ioe.printStackTrace();
            }
        } // constructor end

        public void run() {

            try {

                while (!cSocket.isClosed()) {

                    String message = dis.readUTF();

                    switch (message) {

                        // case when a new player connects
                        case "NEW_PLAYER_CONNECT":

                            // read in string of the player character that is sent from the server and
                            // convert it to the player character enum value!!
                            PlayerCharacter remoteCharacter = PlayerCharacter.valueOf(dis.readUTF());

                            remotePlayer = new Player(scene, remoteCharacter, false);
                            break;

                        // when we have connected to the server (localhost)
                        case "CONNECTION_ACCEPTED":

                            System.out.println("connection accepted");

                            isHost = dis.readBoolean();
                            // if not the host, create a second player because there already is a client
                            // connected to the game and hosting the game
                            if (!isHost) {

                                // read in string of the player character that is sent from the server and
                                // convert it to the player character enum value!!
                                PlayerCharacter hostCharacter = PlayerCharacter.valueOf(dis.readUTF());

                                remotePlayer = new Player(scene, hostCharacter, false);
                            }
                            break;

                        // update player position for the network player
                        case "PLAYER_UPDATE":

                            // reading in the received position & speed
                            double xPos = dis.readDouble();
                            double yPos = dis.readDouble();
                            double xSpeed = dis.readDouble();
                            double ySpeed = dis.readDouble();

                            // update network player position
                            remotePlayer.setPlayerPosition(new Point2D(xPos, yPos));

                            // update character speed of the network player
                            remotePlayer.setXSpeed(xSpeed);
                            remotePlayer.setYSpeed(ySpeed);

                            break;

                        case "GHOST_UPDATE":

                            // reading in the received position & speed
                            int index = dis.readInt();
                            double _xPos = dis.readDouble();
                            double _yPos = dis.readDouble();
                            double _xSpeed = dis.readDouble();
                            double _ySpeed = dis.readDouble();

                            // update the ghost's position
                            Ghost ghost = ghosts.get(index);

                            ghost.setGhostPosition(new Point2D(_xPos, _yPos));
                            ghost.setGhostXSpeed(_xSpeed);
                            ghost.setYSpeed(_ySpeed);

                            break;

                        case "CHAT_MESSAGE":

                            // reading the message sent to chat
                            String chatMessage = dis.readUTF();
                            printToLog(chatMessage);

                            break;

                        case "GAME_LOST":
                            localPlayer.setLives(0);
                            break;

                        case "GAME_WON":

                            localPlayer.setHasWon(true);
                            break;

                        case "PLAYER_DISCONNECT":

                            if (localPlayer.getWin() || localPlayer.getLives() == 0) {

                                break;
                            }

                            else {

                                Platform.runLater(new Runnable() {

                                    @Override
                                    public void run() {

                                        Alert alert = new Alert(AlertType.WARNING, "Sending back to Menu");
                                        alert.setHeaderText("Player Disconnect");
                                        alert.showAndWait();

                                        Game.switchScene(new Menu());
                                    }
                                });
                            }

                            break;

                    } // end of switch statement for managing the messages

                } // while loop end

            } catch (UnknownHostException uhe) {

                uhe.printStackTrace();
            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of run method

        // method that sends the connect message to the server - for our local client
        // connecting to the server
        public void sendConnect() {

            System.out.println("send connect");

            try {

                // client sending the connect message to the server in order to connect
                dos.writeUTF("CONNECT");

                // sending the chosen character of the player to the server
                dos.writeUTF(Game.getChosenPlayer().toString());

                dos.flush();
            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // connect end

        // method for updating player
        public void sendUpdatePlayer(Player player) {

            try {

                dos.writeUTF("PLAYER_UPDATE");

                // getting the current player position & speed and sends it to the server!!
                dos.writeDouble(player.getPinguPos().getX());
                dos.writeDouble(player.getPinguPos().getY());
                dos.writeDouble(player.getXSpeed());
                dos.writeDouble(player.getYSpeed());

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of update player

        // method for updating the ghosts
        // takes an index for the ghosts array as a parameter
        public void sendGhostUpdate(Ghost ghost, int index) {

            try {
                dos.writeUTF("GHOST_UPDATE");

                // getting one ghost's position & speed and sending it to the server!!
                dos.writeInt(index);
                dos.writeDouble(ghost.getGhostPosition().getX());
                dos.writeDouble(ghost.getGhostPosition().getY());
                dos.writeDouble(ghost.getXSpeed());
                dos.writeDouble(ghost.getYSpeed());

                dos.flush();

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        } // end of ghost update method

        // send chat messages to the server
        public void sendChatMessage() {

            // getting the input text from the text field
            String message = tfChat.getText();

            // do not send anything if the message is void!!
            if (message.length() <= 0) {

                return;
            }

            try {

                // sending the command to the server
                dos.writeUTF("CHAT_MESSAGE");

                String messageToSend = Game.getUsername() + ": " + message;

                // sending the message to the server
                dos.writeUTF(messageToSend);

                // log the message in the text area
                printToLog(messageToSend);

                // clear input text field
                tfChat.clear();

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of set chat message method

        // send game won
        public void sendGameWon() {

            try {

                // send a game won message
                dos.writeUTF("GAME_WON");

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }
        } // end of game won method

        // send game lost
        public void sendGameLost() {

            // send a game lost message

            try {
                dos.writeUTF("GAME_LOST");

                dos.flush();
            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of game lost method

        // send disconnect
        public void sendDisconnect() {

            try {
                dos.writeUTF("PLAYER_DISCONNECT");

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of send disconnect

    } // end of ClientThread

    /**
     * @param message
     */
    // print to log method for recording events and messages in the client text area
    public void printToLog(String message) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                taChat.appendText(message + "\n");
            }
        });

    } // end of print to log for the chat

    /************************* SETTERS & GETTERS ************************* */

    // getter for client thread
    public ClientThread getCThread() {

        return cThread;
    }

} // end of FrozenMapScene class
