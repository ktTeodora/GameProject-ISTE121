import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

//  abstract class with attributes and methods for all collectibles in the game
public abstract class Collectibles {

    // Attributes
    Point2D position;
    Image collectible;
    int sizeOfCollectible;
    boolean pickedUp = false;

    // Constructor
    public Collectibles(Point2D position) {

        this.position = position;
    } // end of constructor

    // *********************** METHODS *************************

    // update method with distance check
    public void update(GraphicsContext gc, Player localPlayer, Player remotePlayer) {

        // if picked up, early return
        if (pickedUp == true) {

            return;
        }

        // LOCAL PLAYER
        // getting the center position of the collectible
        Point2D collectiblePosition = new Point2D(position.getX() + collectible.getWidth() / 2,
                position.getY() + collectible.getHeight() / 2);

        // if not allowed to pick up then just draw the collectible without doing
        // collision checks!!!s
        if (canPickUp(localPlayer)) {

            // getting the center position of the player
            Point2D playerPosition = new Point2D(
                    localPlayer.getPinguPos().getX() + localPlayer.getCurrentImage().getWidth() / 2,
                    localPlayer.getPinguPos().getY() + localPlayer.getCurrentImage().getHeight() / 2);

            // if the distance between the player and the collectible is less than the size
            // of the collectible - the player picks it up
            if (playerPosition.distance(collectiblePosition) < sizeOfCollectible) {

                pickedUp = true;

                // calls for item effects
                onPickedUp(localPlayer);

            } // distance check

        }

        // REMOTE PLAYER
        if (remotePlayer != null && canPickUp(remotePlayer)) {
            // getting the center position of the player
            Point2D playerPosition = new Point2D(
                    remotePlayer.getPinguPos().getX() + remotePlayer.getCurrentImage().getWidth() / 2,
                    remotePlayer.getPinguPos().getY() + remotePlayer.getCurrentImage().getHeight() / 2);

            // if the distance between the player and the collectible is less than the size
            // of the collectible - the player picks it up
            if (playerPosition.distance(collectiblePosition) < sizeOfCollectible) {

                pickedUp = true;

                // calls for item effects
                onPickedUp(remotePlayer);

            } // distance check
        }

        // drawing the collectible
        gc.drawImage(collectible, position.getX(), position.getY());

    } // end of update end

    // implemet with each item and add item effects
    abstract void onPickedUp(Player player);

    // method to check whether we can pick up an item
    public boolean canPickUp(Player player) {

        return true;
    }

} // end of Collectibles class