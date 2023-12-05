package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Sprite {

    private final Vector2 position;
    private float speed;
    private String colour;
    private boolean active;

    public Laser(Texture texture, float x, float y, float shipRotation, float speed) {
        super(texture);
        this.position = new Vector2(x, y);
        this.speed = speed;
        this.setRotation(shipRotation);
        active = true;
    }

    public void update(float delta) {
        if (active) {
            Vector2 velocity = new Vector2(speed, 0).setAngleDeg(getRotation());
            position.add(velocity.x * delta, velocity.y * delta);

            // Check if the laser is out of screen bounds and deactivate it if necessary
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
    public boolean isActive(){

        return active;
    }



}