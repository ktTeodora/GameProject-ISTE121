import java.util.ArrayList;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class ThrowballSnowball {

    // speed of the ball
    double ySpeed = 0.0;
    double xSpeed = 0.0;

    // ball position
    Point2D position;

    Image snowballImage = new Image("file:graphics/SnowballSpin.gif", 25.0, 25.0, true, true);

    public ThrowballSnowball(Point2D position, double xSpeed, double ySpeed) {

        this.position = position;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;

        FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();
        // map.getCThread()

    }

    // update
    public void update(GraphicsContext gc) {

        position = position.add(new Point2D(xSpeed, ySpeed));

        gc.drawImage(snowballImage, position.getX(), position.getY());

        FrozenMapScene map = (FrozenMapScene) Game.getCurrentScene();

        // making an array list of ghosts
        ArrayList<Ghost> ghosts = map.getGhosts();

        for (int i = 0; i < ghosts.size(); i++) {

            Ghost ghost = ghosts.get(i);

            if (ghost.getIsDead()) {

                continue;
            }

            if (ghost.getGhostPosition().distance(position) <= 25.0) {

                ghost.setIsDead(true);

            }

        }
    }

}
