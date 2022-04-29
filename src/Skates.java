/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the skates collectible
* Date: 02/23/2022
*/

import java.util.Timer;
import java.util.TimerTask;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Skates extends Collectibles {

    public Skates(Point2D position) {
        super(position);

        pickedUp = true;
        collectible = new Image("file:graphics/Skates.gif", 45.0, 45.0, true, true);
        sizeOfCollectible = 45;

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                pickedUp = false;
            }
        };

        timer.schedule(task, 15000);
    }

    /**
     * Called when the skates are picked up
     * 
     * @param player the player
     */
    @Override
    void onPickedUp(Player player) {

        player.addSkates();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                pickedUp = false;
            }
        };

        timer.schedule(task, 15000);
    }

}
