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

    private float speed;
    private boolean active;
    private ActionState actionState;
    private Faction faction;
    private int actionCounter;
    private boolean isIdle;

    private final Vector2 position;
    private final Rectangle hitbox;
    private final ShapeRenderer shapeRenderer;
    private final float half = 0.5f;
    private final float angleCalc = 360;



    enum Faction {
        PURPLE,
        TEAL
    }


    enum ActionState {
        PLAYER_CONTROL,
        CRUISE,
        LEFT_U_TURN,
        RIGHT_U_TURN,
        CIRCLE,
        QUARTER_LEFT_TURN,
        QUARTER_RIGHT_TURN,
        IDLE,
        READY,
        STOP

    }

    public Ship(Texture texture, Vector2 position, float speed, ActionState actionState, Rectangle hitbox, int actionCounter, Faction faction) {
        super(texture);
        this.position = position;
        this.speed = speed;
        this.actionState = actionState;
        this.hitbox = new Rectangle();
        this.shapeRenderer = new ShapeRenderer();
        this.actionCounter = 0;
        this.faction = faction;
        this.active = true;

    }

    // Determining the next position of the ship every frame.
    public void update(float delta, Ship ship, long WORLD_WIDTH, long WORLD_HEIGHT) {

        if (active) {
            Vector2 velocity = new Vector2(speed, 0).setAngleDeg(getRotation());
            position.add(velocity.x * delta, velocity.y * delta);

            // Check if the ship is out of screen bounds and deactivate it if necessary
            if (position.x > WORLD_WIDTH || position.y > WORLD_HEIGHT) {
                active = false;
            }

            // Update the sprite's position
            setPosition(position.x, position.y);
            updateHitBox(ship);
        }
    }

    public void setFaction(Faction faction) {

        this.faction = faction;
    }

    public Faction getFaction(){

        return this.faction;
    }

    public float getSpeed(){

        return this.speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;

    }


    public Laser fireLaser(Texture texture, Ship ship) {
        float offsetX = -10f;
        float offsetY = -1.5f;

        Vector2 laserPosition = new Vector2(ship.getX() + ship.getOriginX() + offsetX, ship.getY() + ship.getOriginY() + offsetY);
        Laser laser = new Laser(texture, laserPosition.x, laserPosition.y, ship.getRotation(), 500, hitbox, 0);
        laser.setPosition(laserPosition.x, laserPosition.y);
        Rectangle hitbox = laser.getBoundingRectangle();
        laser.setScale(half);
        return laser;
    }



    // Update the bounding box based on the scaled sprite's position and size
    private void updateHitBox(Ship ship) {
        float shipScale = 0.08f;
        float scaledWidth = ship.getWidth() * shipScale;
        float scaledHeight = ship.getHeight() * shipScale;

        // Update the bounding box's position and size to match the scaled sprite
        float hitboxOffset = 59f;
        hitbox.set(ship.getX()+ hitboxOffset, ship.getY()+ hitboxOffset, scaledWidth, scaledHeight);
    }

void handleActionState(Ship ship) {
    ship.handleIdle(ship);
    ship.handleLeftUTurn(ship);
    ship.handleRightUTurn(ship);
    ship.handleCircle(ship);
    ship.handleQuarterLeftTurn(ship);
    ship.handleQuarterRightTurn(ship);
    ship.handleStop(ship);
    ship.handleReady(ship);
    ship.handleCruise(ship);
    ship.lookForEnemy(ship);

}

    public void handleCruise(Ship ship) {
        if (ship.getActionState() == Ship.ActionState.CRUISE) {
            if (ship.getActionCounter() <= angleCalc) {
                ship.setActionCounter(ship.getActionCounter() + 1);
            } else if (ship.getActionCounter() > angleCalc) {
                if (ship.isIdle) {
                    ship.setActionState(ActionState.IDLE);
                    ship.setActionCounter(0);
                }
                else {
                    ship.setActionState(Ship.ActionState.READY);
                    ship.setActionCounter(0);
                }
            }
        }
    }

    public void handleLeftUTurn(Ship ship) {
        if (ship.getActionState() == Ship.ActionState.LEFT_U_TURN) {
            if (ship.getActionCounter() <= angleCalc) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(half);
            } else if (ship.getActionCounter() > angleCalc) {
                if (ship.isIdle) {
                    ship.setActionState(ActionState.IDLE);
                    ship.setActionCounter(0);
                }
                else {
                        ship.setActionState(Ship.ActionState.READY);
                        ship.setActionCounter(0);
                    }
                }

        }
    }

    public void handleRightUTurn(Ship ship) {
        if (ship.getActionState() == Ship.ActionState.RIGHT_U_TURN) {
            if (ship.getActionCounter() <= angleCalc) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(-half);
            } else if (ship.getActionCounter() > angleCalc) {
                if (ship.isIdle) {
                    ship.setActionState(ActionState.IDLE);
                    ship.setActionCounter(0);
                }
                else {
                    ship.setActionState(Ship.ActionState.READY);
                    ship.setActionCounter(0);
                }
            }
        }
    }

    public void handleCircle(Ship ship) {
        if (ship.getActionState() == ActionState.CIRCLE) {
            if (ship.getActionCounter() <= angleCalc*4) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(0.25f);
            } else if (ship.getActionCounter() > 180*4) {
                if (ship.isIdle) {
                    ship.setActionState(ActionState.IDLE);
                    ship.setActionCounter(0);
                }
                else {
                    ship.setActionState(Ship.ActionState.READY);
                    ship.setActionCounter(0);
                }
            }
        }
    }

    public void handleQuarterLeftTurn(Ship ship) {
        if (ship.getActionState() == ActionState.QUARTER_LEFT_TURN) {
            if (ship.getActionCounter() <= 180*4) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(0.25f);
            } else if (ship.getActionCounter() > 180*4) {
                if (ship.isIdle) {
                    ship.setActionState(ActionState.IDLE);
                    ship.setActionCounter(0);
                }
                else {
                    ship.setActionState(Ship.ActionState.READY);
                    ship.setActionCounter(0);
                }
            }
        }
    }

    public void handleQuarterRightTurn(Ship ship) {
        if (ship.getActionState() == ActionState.QUARTER_RIGHT_TURN) {
            if (ship.getActionCounter() <= 180*4) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(-0.25f);
            } else if (ship.getActionCounter() > 180*4) {
                if (ship.isIdle) {
                    ship.setActionState(ActionState.IDLE);
                    ship.setActionCounter(0);
                }
                else {
                    ship.setActionState(Ship.ActionState.READY);
                    ship.setActionCounter(0);
                }
            }
        }
    }

    public void handleReady(Ship ship) {
        if (ship.getActionState() == ActionState.READY) {
            if (ship.getActionCounter() == 0) {
                ship.lookForEnemy(ship);
                ship.checkWalls(ship);
            }
        }
    }

    public void handleStop(Ship ship) {
        if (ship.getActionState() == ActionState.STOP) {
            if (ship.getActionCounter() <= 0) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.setSpeed(0);
            } else if (ship.getActionCounter() >= 1) {
                ship.setActionState(Ship.ActionState.READY);
                ship.setActionCounter(0);
            }
        }
    }

    public void handleIdle(Ship ship) {

        if (ship.getActionState() == ActionState.IDLE) {
            ship.isIdle = true;
            ship.setSpeed(20);
            if (ship.actionCounter == 0) {
                Random rand = new Random();
                int rand_int = rand.nextInt(10);
                if (rand_int == 1) {
                    ship.setActionState(ActionState.LEFT_U_TURN);
                } else if (rand_int == 2) {
                    ship.setActionState(ActionState.CIRCLE);
                } else if (rand_int == 3) {
                    ship.setActionState(ActionState.QUARTER_LEFT_TURN);
                }
                else if (rand_int == 4){
                    ship.setActionState(ActionState.RIGHT_U_TURN);

                }
                else if (rand_int == 5 || rand_int == 6) {
                    ship.setActionState(ActionState.QUARTER_RIGHT_TURN);
                }
            }
                else { ship.setActionState(ActionState.CRUISE);}
        }
    }

    void lookForEnemy(Ship ship) {
        System.out.println("Looking for enemy...");
    }

    void checkWalls(Ship ship) {
        if (ship.position.x >= Gdx.graphics.getWidth()-50 || ship.position.y >= Gdx.graphics.getHeight()-50)  {
        System.out.println("Boundary x alert");

        }
        if (ship.position.x <= 50 || ship.position.y <= 50){

            System.out.println("Boundary y alert");

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