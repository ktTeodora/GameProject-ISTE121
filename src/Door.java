/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the exit door
* Date: 02/23/2022
*/

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Door {

    // Attributes
    Image door = new Image("file:graphics/Door.png");
    Image doorOpen = new Image("file:graphics/DoorOpens.gif");
    Point2D position = new Point2D(0, 357);

    public void update(GraphicsContext gc, Player player) {

        if (player.getFish() == 1) {

            // if we picked up the fish, door opens
            gc.drawImage(doorOpen, 0, 357);
        } else {

            // if we have no fish, door is closed
            gc.drawImage(door, 0, 357);
        }
    } // update method end

} // Door class end
