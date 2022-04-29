/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the fish collectibles
* Date: 02/23/2022
*/

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

    /**
     * Called when fish is picked up
     * 
     * @param player the player
     */
    @Override
    void onPickedUp(Player player) {

        player.addFish();

        FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();
        map.addFish();
    } // when picked up method end

    /**
     * Check for whether the fish can be picked up
     * 
     * @param player the player
     * @return boolean whether can be picked up
     */
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
