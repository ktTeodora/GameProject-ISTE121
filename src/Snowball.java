import java.util.Timer;
import java.util.TimerTask;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Snowball extends Collectibles {

    public Snowball(Point2D position) {

        super(position);
        collectible = new Image("file:graphics/Snowball.gif", 30.0, 30.0, true, true);
        sizeOfCollectible = 30;
    }

    @Override
    void onPickedUp(Player player) {

        player.addSnowball();

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                pickedUp = false;
            }
        };

        timer.schedule(task, 15000);
    } // when picked up method end

} // end of Snowball class
