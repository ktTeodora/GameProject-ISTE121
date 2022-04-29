/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the ice cube collectible
* Date: 02/23/2022
*/

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class IceCube extends Collectibles {

    public IceCube(Point2D position) {

        super(position);
        collectible = new Image("file:graphics/IceCube.gif", 30.0, 30.0, true, true);
        sizeOfCollectible = 30;
    } // end of ice cube constructor

    /**
     * Called when the ice cube is picked up
     * 
     * @param player the player
     */
    @Override
    void onPickedUp(Player player) {

        // getting the current game scene from the game class, since ice only spawns in
        // frozen map, cast it to frozen map!!
        FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();
        map.addIce();
    } // when picked up method end

} // end of IceCube class
