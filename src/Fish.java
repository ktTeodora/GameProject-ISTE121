import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Fish extends Collectibles {

    public Fish(Point2D position) {
        super(position);

        // we set it to true to make it invisible until all ice cubes have been
        // collected
        pickedUp = true;
        collectible = new Image("file:graphics/Fish.gif", 35.0, 35.0, true, true);
        sizeOfCollectible = 35;
    }

    @Override
    void onPickedUp(Player player) {

        player.addFish();

        FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();
        map.addFish();
    } // when picked up method end

    @Override
    public boolean canPickUp(Player player) {

        // check if we do or do not have a fish
        // only allowed to pick up if we don't have a fish
        if (player.getFish() == 0) {

            return true;
        }
        return false;
    } // end of can pick up method

} // end of Fish class
