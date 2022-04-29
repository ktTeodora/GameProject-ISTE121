import java.util.Random;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

// enum for differentiating ghosts based on colour
// outside of the class' scope makes the enum values global!!!
enum GhostColour {

    LILAC,
    VANILLA,
    TEAL,
    BLUE,
    PINK
} // enum colour valuesof ghosts end

public class Ghost {

    // enum for direction options for the ghost
    // enum - a type;
    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    } // enum direction end

    // constants
    private static final float GHOST_SPEED = 1.5f;
    private static final String BLACK_HEX_CODE = "0x000000ff";

    // Attribute
    Point2D ghostPosition;
    // direction to update the position
    Direction ghostDirection;

    // ghost speed
    double ySpeed = 0.0f;
    double xSpeed = 0.0f;

    // attributes for loading ghost images depending on their direction of movement
    Image ghostLeft;
    Image ghostRight;

    // size of the ghost for the collision
    int sizeOfGhost = 60;

    // constructor that takes Point2D as the parameter to set up the spawn location
    // of the ghost
    // adding the enum as a parameter to discern the ghosts
    public Ghost(Point2D position, GhostColour colour) {

        this.ghostPosition = position;
        this.ghostDirection = randomDirection();

        // loading the images - the numbers are values that decide width and height, the
        // true argument preserves the ratio of the image when scaled down, the 2nd true
        // argument cancels the "smoothe the image" option

        // switch case for colour differentiation
        switch (colour) {
            case BLUE:

                ghostLeft = new Image("file:graphics/ghostLeftBlue.gif", 60.0, 60.0, true, true);
                ghostRight = new Image("file:graphics/ghostRightBlue.gif", 60.0, 60.0, true, true);
                break;

            case LILAC:

                ghostLeft = new Image("file:graphics/ghostLeftLilac.gif", 60.0, 60.0, true, true);
                ghostRight = new Image("file:graphics/ghostRightLilac.gif", 60.0, 60.0, true, true);

                break;

            case PINK:

                ghostLeft = new Image("file:graphics/ghostLeftPink.gif", 60.0, 60.0, true, true);
                ghostRight = new Image("file:graphics/ghostRightPink.gif", 60.0, 60.0, true, true);

                break;

            case TEAL:

                ghostLeft = new Image("file:graphics/ghostLeftTeal.gif", 60.0, 60.0, true, true);
                ghostRight = new Image("file:graphics/ghostRightTeal.gif", 60.0, 60.0, true, true);

                break;

            case VANILLA:

                ghostLeft = new Image("file:graphics/ghostLeftVanilla.gif", 60.0, 60.0, true, true);
                ghostRight = new Image("file:graphics/ghostRightVanilla.gif", 60.0, 60.0, true, true);

                break;

        } // end of switch case for colour choice of the ghosts

    } // constructor end

    // update method to track and update the position of a ghost
    public void update(GraphicsContext gc, Player player) {

        // resets the speed after it has been applied to the given position
        // doing it in the beginning because we want to send over the x and y speed
        // after we've done the update!!
        xSpeed = 0;
        ySpeed = 0;

        switch (ghostDirection) {
            case DOWN:
                if (canMoveVertical(ghostLeft, GHOST_SPEED, true)) {

                    // if can move vertically - speed adds the ghost's speed
                    ySpeed += GHOST_SPEED;
                } else {

                    // if cannot move vertically, choose a random direction
                    ghostDirection = randomDirection();
                } // if direction check end

                break;
            case LEFT:

                if (canMoveHorizontal(ghostLeft, -GHOST_SPEED, true)) {

                    // if can move horizontally - speed sets the ghost's speed
                    xSpeed -= GHOST_SPEED;
                } else {

                    // if cannot move horizontally, choose a random direction
                    ghostDirection = randomDirection();
                } // if direction check end

                break;
            case RIGHT:

                if (canMoveHorizontal(ghostRight, GHOST_SPEED, true)) {

                    // if can move horizontally - speed sets the ghost's speed
                    xSpeed += GHOST_SPEED;
                } else {

                    // if cannot move horizontally, choose a random direction
                    ghostDirection = randomDirection();
                } // if direction check end

                break;
            case UP:

                if (canMoveVertical(ghostRight, -GHOST_SPEED, true)) {

                    // if can move vertically - speed adds the ghost's speed
                    ySpeed -= GHOST_SPEED;
                } else {

                    // if cannot move vertically, choose a random direction
                    ghostDirection = randomDirection();
                } // if direction check end

                break;

        } // direction switch case end

        // move on x or y axis
        ghostPosition = ghostPosition.add(xSpeed, ySpeed);

        // left by default
        Image ghost = ghostLeft;

        // right movement check - switch to the right sprite
        if (xSpeed > 0) {

            ghost = ghostRight;
        }

        gc.drawImage(ghost, ghostPosition.getX(), ghostPosition.getY());

        // setting up collision between ghosts and the player
        // getting the center position of the ghost
        Point2D ghostPositionCentre = new Point2D(ghostPosition.getX() + ghost.getWidth() / 2,
                ghostPosition.getY() + ghost.getHeight() / 2);

        // getting the center position of the player
        Point2D playerPosition = new Point2D(player.getPinguPos().getX() + player.getCurrentImage().getWidth() / 2,
                player.getPinguPos().getY() + player.getCurrentImage().getHeight() / 2);

        // if the distance between the player and the collectible is less than the size
        // of the collectible - the player picks it up
        if (playerPosition.distance(ghostPositionCentre) < sizeOfGhost) {

            // effect of collision
            player.hitPingu();
        } // end of if-statement that checks for collision

    } // end of update

    // update method for remote clients
    public void updateRemote(GraphicsContext gc, Player player) {

        // left by default
        Image ghost = ghostLeft;

        // right movement check - switch to the right sprite
        if (xSpeed > 0) {

            ghost = ghostRight;
        }

        gc.drawImage(ghost, ghostPosition.getX(), ghostPosition.getY());

        // setting up collision between ghosts and the player
        // getting the center position of the ghost
        Point2D ghostPositionCentre = new Point2D(ghostPosition.getX() + ghost.getWidth() / 2,
                ghostPosition.getY() + ghost.getHeight() / 2);

        // getting the center position of the player
        Point2D playerPosition = new Point2D(player.getPinguPos().getX() + player.getCurrentImage().getWidth() / 2,
                player.getPinguPos().getY() + player.getCurrentImage().getHeight() / 2);

        // if the distance between the player and the collectible is less than the size
        // of the collectible - the player picks it up
        if (playerPosition.distance(ghostPositionCentre) < sizeOfGhost) {

            // effect of collision
            player.hitPingu();
        } // end of if-statement that checks for collision
    }

    // we need to know the ghost's position and keep updating the direction of it's
    // movements upon collision without manually controlling it
    // random selection !! returns an enum (4 options only)
    public Direction randomDirection() {

        Random random = new Random();
        int randomNumber = random.nextInt(4); // 4 options
        return Direction.values()[randomNumber];

    } // end of randomDirection

    // moving left and right
    public boolean canMoveHorizontal(Image sprite, double horizontalSpeed, boolean canCheckVertical) {

        // getting width and height of the player
        int width = (int) sprite.getWidth();
        int height = (int) sprite.getHeight();

        // if moving right
        if (horizontalSpeed > 0) {

            // loop through all the pixels to the right of the player - in his direction of
            // movement
            for (int i = 0; i < height; i++) {

                // position of the player is to the top left of the sprite
                // sprite + speed
                int xPixel = (int) (ghostPosition.getX() + sprite.getWidth() + 1);
                int yPixel = (int) (ghostPosition.getY() + i);

                // if pixel out of bounds
                if (!checkIfPixelInScreenBounds(xPixel, yPixel)) {
                    return false;
                }

                // getting the pixel colour
                Color pixelColour = FrozenMapScene.collisionMap.getPixelReader().getColor(xPixel, yPixel);

                // collision colour BLACK - if the colour is black we can't move there
                if (pixelColour.toString().equals(BLACK_HEX_CODE)) {

                    // if speed is 0 && if we can do a vertical check - avoiding infinite
                    // recursion!!
                    if (ySpeed == 0.0 && canCheckVertical) {

                        // if the speed is 0, check if we can move on the y-axis to glide against the
                        // walls more smoothly
                        // recursion loop - adding a boolean to confirm the horizontal check !!
                        if (canMoveVertical(sprite, GHOST_SPEED, false)) {
                            ySpeed = GHOST_SPEED;
                            return false;
                        } else if (canMoveVertical(sprite, -GHOST_SPEED, false)) { // false to break the recursion
                                                                                   // loop

                            ySpeed = -GHOST_SPEED;
                            return false;
                        } // end of if-else smooth collision

                    } // if speed & can move vertical check end

                    return false;
                } // if black check end

            } // for-loop end

        } // if-move right end

        /********************************************** */

        // if moving left
        if (horizontalSpeed < 0) {

            // loop through all the pixels to the left of the player - in his direction of
            // movement
            for (int i = 0; i < height; i++) {

                // position of the player is to the top left of the sprite
                // sprite + speed
                int xPixel = (int) (ghostPosition.getX() - 1);
                int yPixel = (int) (ghostPosition.getY() + i);

                // if pixel out of bounds
                if (!checkIfPixelInScreenBounds(xPixel, yPixel)) {
                    return false;
                }

                // getting the pixel colour
                Color pixelColour = FrozenMapScene.collisionMap.getPixelReader().getColor(xPixel, yPixel);

                // collision colour BLACK - if the colour is black we can't move there
                if (pixelColour.toString().equals(BLACK_HEX_CODE)) {

                    // if speed is 0 && if we can do a vertical check - avoiding infinite
                    // recursion!!
                    if (ySpeed == 0.0 && canCheckVertical) {

                        // if the speed is 0, check if we can move on the y-axis to glide against the
                        // walls more smoothly
                        // recursion loop - adding a boolean to confirm the horizontal check !!
                        if (canMoveVertical(sprite, GHOST_SPEED, false)) {
                            ySpeed = GHOST_SPEED;
                            return false;
                        } else if (canMoveVertical(sprite, -GHOST_SPEED, false)) { // false to break the recursion
                                                                                   // loop

                            ySpeed = -GHOST_SPEED;
                            return false;
                        } // end of if-else smooth collision

                    } // if speed & can move vertical check end

                    return false;
                } // if black check end

            } // for-loop end

        } // if-move left end

        return true;

    } // end of move horizontal

    /********************************************** */
    /********************************************** */

    // moving up and down
    public boolean canMoveVertical(Image sprite, double verticalSpeed, boolean canChechHorizontal) {

        int width = (int) sprite.getWidth();
        int height = (int) sprite.getHeight();

        // if moving downwards
        if (verticalSpeed > 0) {

            // loop through all the pixels to the bottom of the player - in his direction of
            // movement
            for (int i = 0; i < width; i++) {

                // position of the player is to the top left of the sprite
                // sprite + speed
                int xPixel = (int) (ghostPosition.getX() + i);
                int yPixel = (int) (ghostPosition.getY() + sprite.getHeight() - 1);

                // if pixel out of bounds
                if (!checkIfPixelInScreenBounds(xPixel, yPixel)) {
                    return false;
                }

                // get the pixel colour
                Color pixelColour = FrozenMapScene.collisionMap.getPixelReader().getColor(xPixel, yPixel);

                // collision colour BLACK - if the colour is black we can't move there
                if (pixelColour.toString().equals(BLACK_HEX_CODE)) {

                    // if speed is 0 && if we can do a horizontal check - avoiding infinite
                    // recursion!!
                    if (xSpeed == 0.0 && canChechHorizontal) {

                        // if the speed is 0, check if we can move on the y-axis to glide against the
                        // walls more smoothly
                        // recursion loop - adding a boolean to confirm the horizontal check !!
                        if (canMoveHorizontal(sprite, GHOST_SPEED, false)) {
                            xSpeed = GHOST_SPEED;
                            return false;
                        } else if (canMoveHorizontal(sprite, -GHOST_SPEED, false)) { // false to break the recursion
                                                                                     // loop

                            xSpeed = -GHOST_SPEED;
                            return false;
                        } // end of if-else smooth collision

                    } // if speed & can move horizontal check end

                    return false;
                } // if black check end

            } // for-loop end

        } // if-move down end

        /********************************************** */

        // if moving upwards
        if (verticalSpeed < 0) {

            // loop through all the pixels to the top of the player
            for (int i = 0; i < width; i++) {

                // position of the player is to the top left of the sprite
                // sprite + speed
                int xPixel = (int) (ghostPosition.getX() + i);
                int yPixel = (int) (ghostPosition.getY() + 1);

                // if pixel out of bounds
                if (!checkIfPixelInScreenBounds(xPixel, yPixel)) {
                    return false;
                }

                // get the pixel colour
                Color pixelColour = FrozenMapScene.collisionMap.getPixelReader().getColor(xPixel, yPixel);

                // collision colour BLACK - if the colour is black we can't move there
                if (pixelColour.toString().equals(BLACK_HEX_CODE)) {

                    // if speed is 0 && if we can do a horizotnal check - avoiding infinite
                    // recursion!!
                    if (xSpeed == 0.0 && canChechHorizontal) {

                        // if the speed is 0, check if we can move on the y-axis to glide against the
                        // walls more smoothly
                        // recursion loop - adding a boolean to confirm the horizontal check !!
                        if (canMoveHorizontal(sprite, GHOST_SPEED, false)) {
                            xSpeed = GHOST_SPEED;
                            return false;
                        } else if (canMoveHorizontal(sprite, -GHOST_SPEED, false)) { // false to break the recursion
                                                                                     // loop

                            xSpeed = -GHOST_SPEED;
                            return false;
                        } // end of if-else smooth collision

                    } // if speed & can move horizontal check end

                    return false;
                } // if black check end

            } // for-loop end

        } // if-move up end

        return true;

    } // end of move vertical

    /********************************************** */
    /********************************************** */

    // checking if pixel is in screen bounds
    private boolean checkIfPixelInScreenBounds(int pixelX, int pixelY) {

        if (pixelX < 0 || pixelX >= Game.RES_WIDTH) {
            return false;
        } // end of if-width

        if (pixelY < 0 || pixelY >= Game.RES_HEIGHT) {
            return false;
        } // end of if-height

        return true;
    } // end of check if pixel in screen bounds

    /************************* MUTATORS ***************************/

    // setting ghost xSpeed
    public void setGhostXSpeed(double xSpeed) {

        this.xSpeed = xSpeed;
    } // setter for xSpeed end

    // getting ghost xSpeed
    public double getXSpeed() {

        return this.xSpeed;
    } // getter for xSpeed end

    // setting ghost ySpeed
    public void setYSpeed(double ySpeed) {

        this.ySpeed = ySpeed;
    } // setter from ySpeed end

    // getting ghost ySpeed
    public double getYSpeed() {

        return this.ySpeed;
    } // getter for ySpeed end

    // setting ghost's position
    public void setGhostPosition(Point2D position) {

        this.ghostPosition = position;
    } // setter for ghost position end

    // getting ghost's position
    public Point2D getGhostPosition() {

        return this.ghostPosition;
    } // getter for ghost position

} // end of ghost class