package com.game.vssg;

import static com.game.vssg.VSSG.isPaused;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Sprite {

    private final Vector2 position;
    private float speed;
    private boolean active;
    private final Rectangle hitbox;
    private int despawnCounter;
    private Ship ship;


    public Laser(Texture texture, float x, float y, float shipRotation, float speed, int despawnCounter, Ship ship) {
        super(texture);
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.setRotation(shipRotation);
        this.hitbox = getBoundingRectangle();
        this.despawnCounter = despawnCounter;
        this.ship = ship;
        active = true;
    }

    public void update(float delta, long WORLD_WIDTH, long WORLD_HEIGHT, Ship ship) {
        if (!isPaused) {

            if (active) {
                Vector2 velocity = new Vector2(speed, 0).setAngleDeg(getRotation());
                position.add(velocity.x * delta, velocity.y * delta);
                this.setDespawnCounter(this.getDespawnCounter() + 1);
                // Check if the laser is out of screen bounds and deactivate it if necessary
                if (position.x >= WORLD_WIDTH || position.y >= WORLD_HEIGHT || this.despawnCounter > 600) {
                    active = false;
                }

                setPosition(position.x, position.y);
            }
        }
    }


    void updateHitBox(Laser laser) {
        float laserScale = 1f;
        float scaledWidth = laser.getWidth() * laserScale;
        float scaledHeight = laser.getHeight() * laserScale;

        hitbox.set(laser.getX(), laser.getY(), scaledWidth, scaledHeight);
    }

    public float getSpeed() {

        return this.speed;
    }

    public void setSpeed(float s) {
        this.speed = s;

    }

    public boolean isActive() {

        return active;
    }

    public void setInactive(Laser laser) {
        laser.active = false;

    }

    public Rectangle getHitbox() {

        return this.hitbox;
    }

    public void setDespawnCounter(int despawnCounter) {

        this.despawnCounter = despawnCounter;
    }

    public int getDespawnCounter() {

        return this.despawnCounter;
    }

    public Ship getShip() {
        return this.ship;
    }

    public void setShip(Ship ship) {
        this.ship = ship;
    }
}