package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class Ship extends Sprite {

    private Vector2 position;
    private float speed;
    private boolean active;
    private Texture texture;
    private ObjectSet<ShipAction> actionQueue;

    public Ship(Texture texture, Vector2 position, float speed, ObjectSet<ShipAction> actionQueue) {
        super(texture);
        this.texture = texture;
        this.position = position;
        this.speed = speed;
        this.actionQueue = actionQueue;
        active = true;
    }

    // Determining the next position of the ship every frame.
    public void update(float delta) {
        if (active) {
            Vector2 velocity = new Vector2(speed, 0).setAngleDeg(getRotation());
            position.add(velocity.x * delta, velocity.y * delta);

            // Check if the ship is out of screen bounds and deactivate it if necessary
            if (position.x > Gdx.graphics.getWidth() || position.y > Gdx.graphics.getHeight()) {
                active = false;
            }

            // Update the sprite's position
            setPosition(position.x, position.y);
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
        Laser laser = new Laser(texture, laserPosition.x, laserPosition.y, ship.getRotation(), 500);
        laser.setPosition(laserPosition.x, laserPosition.y);
        laser.setScale(0.5f);
        return laser;
    }




    public boolean isActive(){

        return active;
    }



}