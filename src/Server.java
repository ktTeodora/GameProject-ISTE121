/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the game server
* Date: 02/23/2022
*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Server extends Application {

    public static final int PORT_NUMBER = 11009;

    // Attributes
    Scene scene;
    Stage stage;
    TextArea taLog;
    ServerThread server;

    /**
     * @param args
     */
    public static void main(String[] args) {

        launch(args);
    } // end of main

    /**
     * @param _stage
     * @throws Exception
     */
    @Override
    public void start(Stage _stage) throws Exception {

        stage = _stage;
        _stage.setTitle("The frozen Tale - Server Side");

        // root
        VBox root = new VBox(8);

        taLog = new TextArea();
        taLog.setPrefHeight(450);
        taLog.setEditable(false);
        taLog.setWrapText(true);

        root.getChildren().add(taLog);

        scene = new Scene(root, 300, 450);

        // connect stage with the Scene and show it, finalization
        stage.setScene(scene);
        stage.show();

        server = new ServerThread();
        server.start();

    } // start method end

    // ***************** ANNONYMOUS INNER CLASS *******************
    class ServerThread extends Thread {

        // Attributes
        ServerSocket sSocket;
        boolean selectedHost = false;

        // storing all clients who connected
        // store in a vector because vectors are synchronized and thread safe!!
        public Vector<ClientThread> clients = new Vector<>();

        // run method for server thread
        public void run() {

            try {

                sSocket = new ServerSocket(PORT_NUMBER);

                // while loop to make the server expect additional connections
                while (true) {

                    printToLog("Waiting for connection...");

                    Socket cSocket = sSocket.accept();

                    printToLog("Connection accepted.");

                    // if there are no clients who are already connected once we connect, we are the
                    // host!!
                    boolean isHost = clients.size() == 0;

                    // this - current object instance we are executing the code in
                    ClientThread client = new ClientThread(cSocket, this, isHost);

                    printToLog("" + clients.size());

                    clients.add(client);

                    client.start();

                }

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }
        }

    } // end of server thread

    // ***************** ANNONYMOUS INNER CLASS *******************
    class ClientThread extends Thread {

        // Attribute
        Socket cSocket;
        DataInputStream dis;
        DataOutputStream dos;

        // boolean to be passed through from the server thread to decide whether the
        // connected client is the host or not!!
        boolean isHost;

        // storing the chosen character of the player
        String chosenCharacter;

        // lets us access the client thread from the server
        ServerThread server;

        // constructor
        // boolean for host added to the constructor
        // passing ServerThread as a parameter so that we can send messages to all
        // clients
        public ClientThread(Socket _cSocket, ServerThread server, boolean isHost) {

            this.cSocket = _cSocket;
            this.isHost = isHost;

            // allows us to access the clients array from the server in the client thread
            this.server = server;
        } // end of constructor

        // run method for client thread
        public void run() {

            try {
                dos = new DataOutputStream(new BufferedOutputStream(cSocket.getOutputStream()));
                dis = new DataInputStream(new BufferedInputStream(cSocket.getInputStream()));

                while (!cSocket.isClosed()) {

                    String message = dis.readUTF();
                    Vector<ClientThread> clients = server.clients;

                    switch (message) {

                        case "CONNECT":
                            printToLog("Connected.");

                            // taking information about the chosen character of the remote player
                            chosenCharacter = dis.readUTF();

                            // connection established - we need to know if the connected client is the host
                            // or not!!
                            dos.writeUTF("CONNECTION_ACCEPTED");

                            // server lets the client know if they are the owner - if owner; they will be
                            // responisble for thing sin the game, such as ghosts
                            dos.writeBoolean(isHost);

                            // if character is not the host, we will send through the host's character!!
                            // if there is a host, send the host's character to the new player!!
                            if (!isHost) {

                                dos.writeUTF(clients.get(0).chosenCharacter);
                            }

                            // log the host on the server
                            printToLog("CONNECTION ACCEPTED - IS HOST: " + isHost + "- IN GAME CHARACTER: "
                                    + chosenCharacter);
                            dos.flush();

                            for (int i = 0; i < clients.size(); i++) {

                                ClientThread client = clients.get(i);

                                // if the client of the current loop iteration is the one that sent the message,
                                // then don't send message!!
                                if (client != this) {

                                    client.sendPlayerConnect(chosenCharacter);
                                }
                            } // for-loop end

                            break;

                        case "PLAYER_UPDATE":

                            // receiving updates from the remote player
                            double xPos = dis.readDouble();
                            double yPos = dis.readDouble();
                            double xSpeed = dis.readDouble();
                            double ySpeed = dis.readDouble();

                            for (int i = 0; i < clients.size(); i++) {

                                ClientThread client = clients.get(i);

                                if (client != this) {

                                    client.sendPlayerUpdates(xPos, yPos, xSpeed, ySpeed);

                                } // end of if-check for the local player

                            } // end of for-loop

                            break;

                        case "GHOST_UPDATE":
                            // only the host can send ghost updates!!

                            // receiving updates from the host
                            int index = dis.readInt();
                            double _xPos = dis.readDouble();
                            double _yPos = dis.readDouble();
                            double _xSpeed = dis.readDouble();
                            double _ySpeed = dis.readDouble();

                            // loop through the clients array and send the ghost update
                            for (int i = 0; i < clients.size(); i++) {

                                ClientThread client = clients.get(i);

                                if (client != this) {

                                    client.sendGhostUpdates(index, _xPos, _yPos, _xSpeed, _ySpeed);
                                }
                            } // for-loop for clients array end

                            break;

                        case "CHAT_MESSAGE":

                            // receiving message from the server
                            String chatMessage = dis.readUTF();
                            printToLog("Chat Message: " + chatMessage);

                            // loop through the clients array and send the message
                            for (int i = 0; i < clients.size(); i++) {

                                ClientThread client = clients.get(i);

                                if (client != this) {

                                    client.sendChatMessage(chatMessage);
                                }
                            } // for-loop for clients array end

                            break;

                        case "GAME_LOST":

                            // loop through the clients array and send the message
                            for (int i = 0; i < clients.size(); i++) {

                                ClientThread client = clients.get(i);

                                if (client != this) {

                                    client.sendGameLost();
                                }
                            } // for-loop for clients array end

                            printToLog("GAME LOST");

                            break;

                        case "GAME_WON":

                            // loop through the clients array and send the message
                            for (int i = 0; i < clients.size(); i++) {

                                ClientThread client = clients.get(i);

                                if (client != this) {

                                    client.sendGameWon();
                                }
                            } // for-loop for clients array end

                            printToLog("GAME WON");

                            break;

                        case "PLAYER_DISCONNECT":

                            // loop through the clients array and send the message
                            for (int i = 0; i < clients.size(); i++) {

                                ClientThread client = clients.get(i);

                                // if (client != this) {

                                client.disconnect();
                                // }
                            } // for-loop for clients array end

                            printToLog("PLAYER DISCONNECTED");

                            server.clients.clear();

                            break;

                    } // end of switch statement

                } // while loop end

            } catch (IOException ioe) {

                ioe.printStackTrace();
            } // en dof catch

        } // end of run

        // method that notifies the client that another player has joined
        // when connecting, take the value of the chosen character
        public void sendPlayerConnect(String character) {

            try {
                dos.writeUTF("NEW_PLAYER_CONNECT");

                // send locally chosen character to other players
                dos.writeUTF(character);
                dos.flush();
            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of send player connect method

        // method to send player updates from the server to the other clients
        public void sendPlayerUpdates(double xPos, double yPos, double xSpeed, double ySpeed) {

            try {
                dos.writeUTF("PLAYER_UPDATE");

                dos.writeDouble(xPos);
                dos.writeDouble(yPos);
                dos.writeDouble(xSpeed);
                dos.writeDouble(ySpeed);

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of send player updates method

        // method to send ghost updates from the host to other clients
        public void sendGhostUpdates(int index, double xPos, double yPos, double xSpeed, double ySpeed) {

            try {
                dos.writeUTF("GHOST_UPDATE");

                dos.writeInt(index);
                dos.writeDouble(xPos);
                dos.writeDouble(yPos);
                dos.writeDouble(xSpeed);
                dos.writeDouble(ySpeed);

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }
        }

        // send chat messages to the clients!!
        public void sendChatMessage(String message) {

            try {

                // sending the command to the client
                dos.writeUTF("CHAT_MESSAGE");

                // sending to the clients!!
                dos.writeUTF(message);

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of send chat message method

        // send game lost message out to clients
        public void sendGameLost() {

            try {
                dos.writeUTF("GAME_LOST");

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }
        } // end of send game lost message

        // send game won message out to clients
        public void sendGameWon() {

            try {
                dos.writeUTF("GAME_WON");

                dos.flush();

            } catch (IOException ioe) {

                ioe.printStackTrace();
            }

        } // end of send game won message

        // disconnect method
        public void disconnect() {

            try {
                dos.writeUTF("PLAYER_DISCONNECT");

                dos.flush();
            } catch (IOException ioe) {

                ioe.printStackTrace();
            } finally {

                // remove us from the server's clients
                // boolean removed = server.clients.remove(this);

                // printToLog("" + removed);
                // closing the socket automatically closes dos && dis!!
                try {
                    cSocket.close();
                } catch (IOException ioe) {

                    ioe.printStackTrace();
                }
            }
        } // end of disconnect

    } // end of client thread

    /**
     * prints messages to the log
     * 
     * @param message the message
     */
    // method to print to server taLog
    public void printToLog(String message) {

        Platform.runLater(new Runnable() {

            @Override
            public void run() {

                taLog.appendText(message + "\n");

            } // end of run

        }); // end of platform run later

    } // end of print to log

} // end of Server class
