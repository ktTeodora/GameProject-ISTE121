import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Gingerbread extends Collectibles {

    public Gingerbread(Point2D position) {
        super(position);
        collectible = new Image("file:graphics/Gingerbread.gif", 45.0, 45.0, true, true);
        sizeOfCollectible = 45;
    }

    @Override
    void onPickedUp(Player player) {

        player.addLife();
    }

} // end of Gingerbread class
