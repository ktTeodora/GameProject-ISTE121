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
    } // when picked up method end

} // end of Snowball class
