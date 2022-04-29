/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for choose player scene
* Date: 02/23/2022
*/

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.crypto.dsig.TransformException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

public class ChoosePlayer extends GameScene {

    TextField tfUsername;
    TextField tfIP;

    @Override
    protected void setupScene() {

        // root
        GridPane root = new GridPane();
        // customize root
        root.setAlignment(Pos.CENTER);
        root.setVgap(25);
        root.setHgap(10);

        root.setBackground(new Background(new BackgroundFill(Color.ALICEBLUE, CornerRadii.EMPTY, Insets.EMPTY)));

        // create a scene with a specific size (width, height), connnect with the layout
        scene = new Scene(root, Game.RES_WIDTH, Game.RES_HEIGHT);

        // css stylesheet
        scene.getStylesheets().add("style.css");

        // GUI items
        // Label lblUsername = new Label("Username:");
        Image username = new Image("file:graphics/Username.png", 180, 80, true, true);
        ImageView usernnameView = new ImageView(username);
        tfUsername = new TextField();
        tfUsername.setPrefWidth(50);

        // Label lblIP = new Label("IP Address:");
        Image ipAddress = new Image("file:graphics/IPAddress.png", 180, 80, true, true);
        ImageView ipAddressView = new ImageView(ipAddress);
        tfIP = new TextField("localhost");
        tfIP.setPrefWidth(50);

        // load xml file in-case we have text field values from last session
        readXMLFile();

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

                // write the input values to xml so we can load next time
                writeXMLFile();

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

                // write the input values to xml so we can load next time
                writeXMLFile();

                // set to game map
                Game.switchScene(new FrozenMapScene());
            }

        }); // end of btn set on action event

        // adding elements to the root
        root.add(btnPingu, 0, 0);
        root.add(btnMedo, 1, 0);
        root.add(usernnameView, 0, 1);
        root.add(tfUsername, 1, 1);
        root.add(ipAddressView, 0, 2);
        root.add(tfIP, 1, 2);

        // return to the menu scene on "Esc" key press
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            @Override
            public void handle(KeyEvent _keyEvent) {

                KeyCode code = _keyEvent.getCode();
                // if "Esc", switch to Menu
                if (code == KeyCode.ESCAPE) {
                    // save text fields to xml when leaving this scene
                    writeXMLFile();
                    Game.switchScene(new Menu());
                }
            } // end of handle for return to menu

        }); // end of on key pressed to return to menu

    } // end of setUpScene method

    @Override
    public void update() {
        // TODO Auto-generated method stub

    }

    // adapted from https://mkyong.com/java/how-to-create-xml-file-in-java-dom/
    private void writeXMLFile() {
        String username = tfUsername.getText();
        String IP = tfIP.getText();

        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

            // create the root element <settings>
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("settings");
            doc.appendChild(rootElement);

            // create the <username> element, store value from text field
            // and append to the root element <settings>
            Element elementUsername = doc.createElement("username");
            elementUsername.setTextContent(username);
            rootElement.appendChild(elementUsername);

            // create the <IP> element, store the passed in IP from the text field
            // and append to the root element <settings>
            Element ipElement = doc.createElement("IP");
            ipElement.setTextContent(IP);
            rootElement.appendChild(ipElement);

            // create the xml file
            FileOutputStream output = new FileOutputStream("user-settings.xml");
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // set the xml style
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);

            transformer.transform(source, result);

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (TransformerConfigurationException tce) {
            tce.printStackTrace();
        } catch (TransformerException te) {
            te.printStackTrace();
        }
    }

    private void readXMLFile() {

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {

            // parse file
            DocumentBuilder db = dbf.newDocumentBuilder();

            File settingsFile = new File("user-settings.xml");

            // if the settings file doesn't exist then early out, we have nothing to load
            if (!settingsFile.exists()) {
                return;
            }

            Document doc = db.parse(settingsFile);

            doc.getDocumentElement().normalize();

            // get <settings> node
            NodeList list = doc.getElementsByTagName("settings");

            for (int i = 0; i < list.getLength(); i++) {
                Node node = list.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {

                    Element element = (Element) node;

                    // get <username> tag value
                    String username = element.getElementsByTagName("username").item(0).getTextContent();

                    // get <IP> tag value
                    String IP = element.getElementsByTagName("IP").item(0).getTextContent();

                    tfUsername.setText(username);
                    tfIP.setText(IP);

                }
            }

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (SAXException se) {
            se.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }

} // end of ChoosePlayer class
