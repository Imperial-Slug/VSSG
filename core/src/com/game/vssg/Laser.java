package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Laser extends Sprite {

    private Vector2 position;
    private Vector2 velocity;
    private boolean active;

    public Laser(Texture texture, Vector2 shipPosition, float shipRotation, float speed) {
        super(texture);
        this.position = new Vector2(shipPosition);
        this.velocity = new Vector2(speed, 0).setAngleDeg(shipRotation);

        this.setRotation(shipRotation);
        active = true;
    }

    public void update(float delta) {
        if (active) {
            position.add(velocity.x * delta, velocity.y * delta);

            // Check if the laser is out of screen bounds and deactivate it if necessary
            if (position.x > Gdx.graphics.getWidth() || position.y > Gdx.graphics.getHeight()) {
                active = false;
            }

            // Update the sprite's position
            setPosition(position.x, position.y);
        }
    }


    public boolean isActive(){

        return active;
    }

}
