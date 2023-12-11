package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.Random;

public class Ship extends Sprite {

    private final Vector2 position;
    private float speed;
    private boolean active;
    private ActionState actionState;
    private final Rectangle hitbox;
    private final ShapeRenderer shapeRenderer;
    private int actionCounter;
    private boolean isIdle;



    enum ActionState {
        U_TURN,
        CIRCLE,
        DOWN,
        UP,
        RIGHT,
        LEFT,
        IDLE,
        STOP

    }

    public Ship(Texture texture, Vector2 position, float speed, ActionState actionState, Rectangle hitbox, int actionCounter) {
        super(texture);
        this.position = position;
        this.speed = speed;
        this.actionState = actionState;
        this.hitbox = new Rectangle();
        this.shapeRenderer = new ShapeRenderer();
        this.actionCounter = 0;
        this.active = true;

    }

    // Determining the next position of the ship every frame.
    public void update(float delta, Ship ship) {

        if (active) {
            Vector2 velocity = new Vector2(speed, 0).setAngleDeg(getRotation());
            position.add(velocity.x * delta, velocity.y * delta);

            // Check if the ship is out of screen bounds and deactivate it if necessary
            if (position.x > Gdx.graphics.getWidth() || position.y > Gdx.graphics.getHeight()) {
                active = false;
            }

            // Update the sprite's position
            setPosition(position.x, position.y);
            updateHitBox(ship);
        }
    }

    public float getSpeed(){

        return this.speed;
    }

    public void setSpeed(float s) {
        this.speed = s;

    }


    public Laser fireLaser(Texture texture, Ship ship) {
        float offsetX = -10f;
        float offsetY = -1.5f;

        Vector2 laserPosition = new Vector2(ship.getX() + ship.getOriginX() + offsetX, ship.getY() + ship.getOriginY() + offsetY);
        Laser laser = new Laser(texture, laserPosition.x, laserPosition.y, ship.getRotation(), 500, hitbox);
        laser.setPosition(laserPosition.x, laserPosition.y);
        Rectangle hitbox = laser.getBoundingRectangle();
        laser.setScale(0.5f);
        return laser;
    }



    // Update the bounding box based on the scaled sprite's position and size
    private void updateHitBox(Ship ship) {
        float shipScale = 0.08f;
        float scaledWidth = ship.getWidth() * shipScale;
        float scaledHeight = ship.getHeight() * shipScale;

        // Update the bounding box's position and size to match the scaled sprite
       hitbox.set(ship.getX()+59f, ship.getY()+59f, scaledWidth, scaledHeight);
    }

void handleActionState(Ship ship) {
  ship.handleUTurn(ship);
  ship.handleCircle(ship);
  ship.handleIdle(ship);
}

    public void handleUTurn(Ship ship) {
        if (ship.getActionState() == Ship.ActionState.U_TURN) {
            if (ship.getActionCounter() <= 180) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(1f);
            } else if (ship.getActionCounter() > 180) {
                ship.setActionState(Ship.ActionState.IDLE);
                ship.setActionCounter(0);
            }
        }
    }

    public void handleCircle(Ship ship) {
        if (ship.getActionState() == ActionState.CIRCLE) {
            if (ship.getActionCounter() <= 360*2) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(0.5f);
            } else if (ship.getActionCounter() > 360*2) {
                ship.setActionState(Ship.ActionState.IDLE);
                ship.setActionCounter(0);
            }
        }
    }

    public void handleIdle(Ship ship) {

        if (ship.getActionState()== ActionState.IDLE) {
            ship.isIdle = true;
            if (ship.isIdle) {
                ship.setSpeed(20);
                if (ship.actionCounter == 0) {
                    Random rand = new Random();
                    int rand_int = rand.nextInt(10);
                    if (rand_int >= 5) {
                        ship.setActionState(ActionState.U_TURN);
                    } else if (rand_int < 5) {
                        ship.setActionState(ActionState.CIRCLE);
                    }


                }
            }
        }
        if (ship.isIdle) {

        }
    }


public Rectangle getHitbox() {

        return this.hitbox;

}

    public boolean isActive(){

        return active;
    }

    public void setInactive(Ship ship){
        ship.active = false;

    }

    public void drawBoundingBox() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.RED);

        float x = hitbox.x;
        float y = hitbox.y;
        float width = hitbox.width;
        float height = hitbox.height;

        shapeRenderer.rect(x, y, width, height);

        shapeRenderer.end();
    }

    // Dispose of the ShapeRenderer when it's no longer needed
    public void dispose() {
        shapeRenderer.dispose();
    }


public ShapeRenderer getShapeRenderer(){

        return this.shapeRenderer;
}

public ActionState getActionState() {
        return this.actionState;

}

    public void setActionState(ActionState actionState){
        this.actionState = actionState;

    }


    public int getActionCounter() {
        return this.actionCounter;

    }

    public void setActionCounter(int actionCounter){
        this.actionCounter = actionCounter;

    }


}