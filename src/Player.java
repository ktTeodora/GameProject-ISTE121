/*  
*Authors: Matea Boderistanac & Doroteja Krtalic
*Course: ISTE-121
* Class: A class for the controllable player
* Date: 02/23/2022
*/

import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

// setting up an enum for character choice
enum PlayerCharacter {

    PENGUIN,
    BEAR
} // character choice end

public class Player {

    // constant
    private static final double PINGU_SPEED = 2.5;
    private static final double SKATE_SPEED = 4.0;
    private static final String BLACK_HEX_CODE = "0x000000ff";
    private static final String VIOLET_HEX_CODE = "0x7b00ffff";

    // Attribute
    Point2D pinguPosition = new Point2D(10, 440);
    Image currentImage;
    // ice cubes - MOVED TO FROZEN MAP CLASS
    // int iceCubeCount;
    // Image iceCubesCount = new Image("file:graphics/IceCubeCollect.png", 35.0,
    // 35.0, true, true);
    // gingerbread
    int lifeCount = 3; // default lives count
    Image livesCount = new Image("file:graphics/Gingerbread.png", 35.0, 35.0, true, true);
    // fish
    int fishCount = 0;
    Image fishesCount = new Image("file:graphics/Fish.png", 45.0, 45.0, true, true);
    // snowball
    int snowballCount = 0;
    Image snowballsCount = new Image("file:graphics/Snowball.png", 45.0, 45.0, true, true);
    // door

    // win
    boolean hasWon = false;

    // skates
    boolean hasSkates = false;

    // needed for on-key press events in our switch case
    Scene scene;

    boolean upPressed = false;
    boolean rightPressed = false;
    boolean downPressed = false;
    boolean leftPressed = false;

    // x & y speed attributes
    double ySpeed = 0.0f;
    double xSpeed = 0.0f;

    // images as attributes - to be set once a character has been selected
    Image up;
    Image right;
    Image down;
    Image left;
    Image still;
    Image leftSkate;
    Image rightSkate;

    // constructor
    // boolean to only set up input for the local player!!
    public Player(Scene scene, PlayerCharacter player, boolean isLocal) {

        this.scene = scene;

        // switch for the player - choose a character to play with
        switch (player) {

            // loading the images - the numbers are values that decide width and height, the
            // true argument preserves the ratio of the image when scaled down, the 2nd true
            // argument cancels the "smoothe the image" option
            case PENGUIN:
                up = new Image("file:graphics/backPingu.gif", 50.0, 50.0, true, true);
                right = new Image("file:graphics/rightPingu.gif", 50.0, 50.0, true, true);
                down = new Image("file:graphics/frontPingu.gif", 50.0, 50.0, true, true);
                left = new Image("file:graphics/leftPingu.gif", 50.0, 50.0, true, true);
                still = new Image("file:graphics/frontPinguStill.gif", 50.0, 50.0, true, true);
                leftSkate = new Image("file:graphics/LeftPinguSkate.png", 50.0, 50.0, true, true);
                rightSkate = new Image("file:graphics/RightPinguSkate", 50.0, 50.0, true, true);
                break;

            case BEAR:
                up = new Image("file:graphics/backMedo.gif", 50.0, 50.0, true, true);
                right = new Image("file:graphics/rightMedo.gif", 50.0, 50.0, true, true);
                down = new Image("file:graphics/frontMedo.gif", 50.0, 50.0, true, true);
                left = new Image("file:graphics/leftMedo.gif", 50.0, 50.0, true, true);
                still = new Image("file:graphics/bearStill.gif", 50.0, 50.0, true, true);
                leftSkate = new Image("file:graphics/LeftMedoSkate.png", 50.0, 50.0, true, true);
                rightSkate = new Image("file:graphics/RightMedoSkate.png", 50.0, 50.0, true, true);
                break;

        } // end of switch character for the player

        // switch currentImage from null to still
        currentImage = still;

        if (isLocal) {

            setUpInput();

        }
    } // constructor end

    // setting up input for keyboard
    private void setUpInput() {

        // on key events - when pressed
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent kEvent) {
                KeyCode code = kEvent.getCode();

                switch (code) {

                    case UP:
                    case W:
                        upPressed = true;
                        break;
                    case RIGHT:
                    case D:
                        rightPressed = true;
                        break;
                    case DOWN:
                    case S:
                        downPressed = true;
                        break;
                    case LEFT:
                    case A:
                        leftPressed = true;
                        break;
                    case ENTER:

                        // send chat message with enter key pressed
                        FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();
                        map.getCThread().sendChatMessage();
                        break;

                    case SPACE:
                        throwSnowball();
                        break;
                } // switch case end

            } // handle end

        }); // on key pressed end

        // on key events - when released
        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {

            public void handle(KeyEvent kEvent) {
                KeyCode code = kEvent.getCode();

                switch (code) {

                    case UP:
                    case W:
                        upPressed = false;
                        break;
                    case RIGHT:
                    case D:
                        rightPressed = false;
                        break;
                    case DOWN:
                    case S:
                        downPressed = false;
                        break;
                    case LEFT:
                    case A:
                        leftPressed = false;
                        break;
                } // switch case end

            } // handle end

        }); // on key released end

    } // end of setup input

    /**
     * update method for checking the current movement of the player and
     * continuously updating its position
     * update is updating the local player
     * 
     * @param gc graphics context from the canvas
     */
    public void update(GraphicsContext gc) {

        // resetting the speed after movement
        // doing it in the beginning because we want to send over the x and y speed
        // after we've done the update!!
        xSpeed = 0.0f;
        ySpeed = 0.0f;

        // diagonal movement - separate if's to move in different directions

        Boolean moving = false; // for static image
        double speed = PINGU_SPEED;

        if (hasSkates) {

            speed = SKATE_SPEED;
        }

        // add conditions for collision along with key conditions
        if (upPressed && canMoveVertical(up, -speed, true)) {

            ySpeed -= speed;

            // setting the image to the set direction as the current image to be drawn in
            // the end of the method
            currentImage = up;

            // is moving
            moving = true;

        } // up end

        if (rightPressed && canMoveHorizontal(right, speed, true)) {

            xSpeed += speed;

            if (hasSkates) {

                currentImage = rightSkate;
            } else {

                // setting the image to the set direction as the current image to be drawn in
                // the end of the method
                currentImage = right;

            }
            // is moving
            moving = true;
        }

        if (downPressed && canMoveVertical(down, speed, true)) {

            ySpeed += speed;

            // setting the image to the set direction as the current image to be drawn in
            // the end of the method
            currentImage = down;

            // is moving
            moving = true;
        }

        if (leftPressed && canMoveHorizontal(left, -speed, true)) {

            xSpeed -= speed;

            if (hasSkates) {

                currentImage = leftSkate;
            } else {

                // setting the image to the set direction as the current image to be drawn in
                // the end of the method
                currentImage = left;
            }

            // is moving
            moving = true;
        }

        // if no movement
        if (!moving) {
            // default state
            currentImage = still;
        }

        // setting the penguin's position
        pinguPosition = pinguPosition.add(xSpeed, ySpeed);

        // drawing the player
        gc.drawImage(currentImage, pinguPosition.getX(), pinguPosition.getY());

        // keeping ice cubes count on screen
        gc.setFont(new Font(20));
        gc.setFill(Color.WHITESMOKE);
        // MOVED TO FROZEN MAP CLASS
        // gc.drawImage(iceCubesCount, 42, 47 - iceCubesCount.getHeight());
        // gc.fillText("" + iceCubeCount, 80, 35);
        // keeping fish count on screen
        gc.drawImage(fishesCount, 290, 46 - fishesCount.getHeight());
        gc.fillText("" + fishCount, 345, 35);

        // keeping life count on screen
        gc.drawImage(livesCount, 120, 47 - livesCount.getHeight());
        gc.fillText("" + lifeCount, 162, 35);

        // keeping snowball count on screen
        gc.drawImage(snowballsCount, 195, 49 - snowballsCount.getHeight());
        gc.fillText("" + snowballCount, 250, 35);

    } // end of update

    /**
     * Updates the remote player
     * 
     * @param gc graphics context from the sc
     */
    // update method for remote player
    public void updateRemote(GraphicsContext gc) {

        // setting the image based on the speed of the network player
        if (ySpeed < 0) {

            currentImage = up;
        } else if (xSpeed > 0) {

            currentImage = right;
        } else if (xSpeed < 0) {

            currentImage = left;
        } else if (ySpeed > 0) {

            currentImage = down;
        } else {
            currentImage = still;
        }

        // drawing the player on the position we received from the server
        gc.drawImage(currentImage, pinguPosition.getX(), pinguPosition.getY());
    }

    // moving left and right
    private boolean canMoveHorizontal(Image sprite, double horizontalSpeed, boolean canCheckVertical) {

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
                int xPixel = (int) (pinguPosition.getX() + sprite.getWidth() + 1);
                int yPixel = (int) (pinguPosition.getY() + i);

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
                        if (canMoveVertical(sprite, PINGU_SPEED, false)) {
                            ySpeed = PINGU_SPEED;
                            return true;
                        } else if (canMoveVertical(sprite, -PINGU_SPEED, false)) { // false to break the recursion
                                                                                   // loop

                            ySpeed = -PINGU_SPEED;
                            return true;
                        } // end of if-else smooth collision

                    }

                    return false;

                } // if black check end

            } // for-loop end

        } // if-move right end

        // if moving left
        if (horizontalSpeed < 0) {

            // loop through all the pixels to the left of the player - in his direction of
            // movement
            for (int i = 0; i < height; i++) {

                // position of the player is to the top left of the sprite
                // sprite + speed
                int xPixel = (int) (pinguPosition.getX() - 1);
                int yPixel = (int) (pinguPosition.getY() + i);

                // if pixel out of bounds
                if (!checkIfPixelInScreenBounds(xPixel, yPixel)) {
                    return false;
                }

                // getting the pixel colour
                Color pixelColour = FrozenMapScene.collisionMap.getPixelReader().getColor(xPixel, yPixel);

                // collision with colour VIOLET - if the colour is violet, we win the game if we
                // have fulfilled the mission
                FrozenMapScene mapScene = (FrozenMapScene) Game.getCurrentScene();

                if (pixelColour.toString().equals(VIOLET_HEX_CODE) && mapScene.getFishCount() == 2 && !hasWon) {

                    hasWon = true;

                    FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();
                    map.getCThread().sendGameWon();

                } // if violet check end

                // collision colour BLACK - if the colour is black we can't move there
                if (pixelColour.toString().equals(BLACK_HEX_CODE)) {

                    // if speed is 0 && if we can do a vertical check - avoiding infinite
                    // recursion!!
                    if (ySpeed == 0.0 && canCheckVertical) {

                        // if the speed is 0, check if we can move on the y-axis to glide against the
                        // walls more smoothly
                        // recursion loop - adding a boolean to confirm the horizontal check !!
                        if (canMoveVertical(sprite, PINGU_SPEED, false)) {
                            ySpeed = PINGU_SPEED;
                            return true;
                        } else if (canMoveVertical(sprite, -PINGU_SPEED, false)) { // false to break the recursion
                                                                                   // loop

                            ySpeed = -PINGU_SPEED;
                            return true;
                        } // end of if-else smooth collision

                    }

                    return false;
                } // if black check end

            } // for-loop end

        } // if-move left end

        return true;

    } // end of move horizontal

    // moving up and down
    private boolean canMoveVertical(Image sprite, double verticalSpeed, boolean canChechHorizontal) {

        int width = (int) sprite.getWidth();
        int height = (int) sprite.getHeight();

        // if moving downwards
        if (verticalSpeed > 0) {

            // loop through all the pixels to the bottom of the player - in his direction of
            // movement
            for (int i = 0; i < width; i++) {

                // position of the player is to the top left of the sprite
                // sprite + speed
                int xPixel = (int) (pinguPosition.getX() + i);
                int yPixel = (int) (pinguPosition.getY() + sprite.getHeight() - 1);

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
                        if (canMoveHorizontal(sprite, PINGU_SPEED, false)) {
                            xSpeed = PINGU_SPEED;
                            return true;
                        } else if (canMoveHorizontal(sprite, -PINGU_SPEED, false)) { // false to break the recursion
                                                                                     // loop

                            xSpeed = -PINGU_SPEED;
                            return true;
                        } // end of if-else smooth collision

                    }

                    return false;
                } // if black check end

            } // for-loop end

        } // if-move down end

        // if moving upwards
        if (verticalSpeed < 0) {

            // loop through all the pixels to the top of the player
            for (int i = 0; i < width; i++) {

                // position of the player is to the top left of the sprite
                // sprite + speed
                int xPixel = (int) (pinguPosition.getX() + i);
                int yPixel = (int) (pinguPosition.getY() + 1);

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
                        if (canMoveHorizontal(sprite, PINGU_SPEED, false)) {
                            xSpeed = PINGU_SPEED;
                            return true;
                        } else if (canMoveHorizontal(sprite, -PINGU_SPEED, false)) { // false to break the recursion
                                                                                     // loop

                            xSpeed = -PINGU_SPEED;
                            return true;
                        } // end of if-else smooth collision

                    }

                    return false;
                } // if black check end

            } // for-loop end

        } // if-move up end

        return true;

    } // end of move vertical

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

    // mOVED TO FROZEN MAP
    /************************* ADD ICECUBE **************************/
    // public void addIce() {

    // iceCubeCount++;
    // } // end of the add ice method

    /************************* ADD LIVES ****************************/
    public void addLife() {

        lifeCount++;
    }// end of the addLives method after collision with gingerbread

    /************************* ADD SNOWBALL ****************************/
    public void addSnowball() {

        snowballCount++;
    } // end of add snowball

    /************************* ADD FISH ****************************/
    public void addFish() {

        fishCount++;
    } // end of add fish

    /************************ ADD SKATES ************************ */
    public void addSkates() {

        hasSkates = true;

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {

                hasSkates = false;
            }
        };

        timer.schedule(task, 6000);
    }

    /****************** COLLISION BETWEEN PLAYER & GHOST ******************/
    public void hitPingu() {

        // resetting the position after the collision
        pinguPosition = new Point2D(500, 500);
        lifeCount--;

        // check the life count
        if (lifeCount <= 0) {

            FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();

            map.getCThread().sendGameLost();

        } // end of life check if-statement

    } // end of the hitPingu method for collision between ghosts and the player

    // throw snowball
    public void throwSnowball() {

        if (snowballCount > 0) {

            snowballCount--;

            final double MULTIPLIER = 1.2;

            // if the pingu is still, throw downwards
            if (xSpeed == 0.0 && ySpeed == 0.0) {

                ySpeed = PINGU_SPEED * MULTIPLIER;

            }

            FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();
            map.throwBall(pinguPosition, xSpeed, ySpeed);
        }
    }

    /************************* MUTATORS ***************************/

    // setter for player position Point2D
    public void setPlayerPosition(Point2D pos) {

        this.pinguPosition = pos;
    } // player position Point2D setter end

    /**
     * gets the players position
     * 
     * @return Point2D players position
     */
    // getter for pingu position Point2D
    public Point2D getPinguPos() {

        return this.pinguPosition;
    } // pingu pos point2d getter end

    /**
     * sets the players x speed
     * 
     * @param _xSpeed x speed to set
     */
    // setter for xSpeed
    public void setXSpeed(double _xSpeed) {

        this.xSpeed = _xSpeed;
    } // xSpeed setter end

    /**
     * gets the players x speed
     * 
     * @return double x speed
     */
    // getter for xSpeed
    public double getXSpeed() {

        return this.xSpeed;
    } // xSpeed getter end

    /**
     * sets the players y speed
     * 
     * @param _ySpeed y speed to set
     */
    // setter for ySpeed
    public void setYSpeed(double _ySpeed) {

        this.ySpeed = _ySpeed;
    } // ySpeed setter end

    /**
     * Gets the players y speed
     * 
     * @return double y speed
     */
    // getter for ySpeed
    public double getYSpeed() {

        return this.ySpeed;
    } // ySpeed getter end

    /**
     * Gets the current image the player is using
     * 
     * @return Image the current player image
     */
    // getter for the current image of the player
    public Image getCurrentImage() {

        return this.currentImage;
    } // current player image getter end

    /**
     * sets the number of lives the player has
     * 
     * @param lives lives to set
     */
    // setter for player lives
    public void setLives(int lives) {

        this.lifeCount = lives;
    } // end of set lives

    /**
     * returns the number of lives the player has
     * 
     * @return int number of lives
     */
    // getter for player lives
    public int getLives() {

        return lifeCount;
    } // end of get lives

    /**
     * returns the number of fish the player has
     * 
     * @return int number of fish
     */
    // getter for fish count
    public int getFish() {

        return fishCount;
    } // end of get fish

    /**
     * Sets whether the game has been won
     * 
     * @param win value to set
     */
    // setter for hasWon boolean
    public void setHasWon(boolean win) {

        hasWon = win;
    } // end of set has won

    /**
     * @return boolean
     */
    // getter for hasWon boolean
    public boolean getWin() {

        return hasWon;
    } // end of get win

} // end of player
