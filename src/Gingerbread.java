/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the gingerbread collectibles
* Date: 02/23/2022
*/

import javafx.geometry.Point2D;
import javafx.scene.image.Image;

public class Gingerbread extends Collectibles {

    public Gingerbread(Point2D position) {
        super(position);
        collectible = new Image("file:graphics/Gingerbread.gif", 45.0, 45.0, true, true);
        sizeOfCollectible = 45;
    }

    /**
     * Called when gingerbread is picked up
     * 
     * @param player the player
     */
    @Override
    void onPickedUp(Player player) {

        player.addLife();
    }

} // end of Gingerbread class
