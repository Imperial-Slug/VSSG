package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Ship extends Sprite {

private float x, y;
private float speed;
private boolean active;

public Ship(Texture texture, float x, float y, float speed) {
    super(texture);
    this.x = x;
    this.y = y;
    this.speed = speed;
    active = true;

}

public void update(float delta) {

    if (active) {
        // Calculate the movement vector based on rotation
        float deltaX = speed * MathUtils.cosDeg(getRotation());
        float deltaY = speed * MathUtils.sinDeg(getRotation());

        // Update the ship's position based on the movement vector
        x += deltaX * delta;
        y += deltaY * delta;

        // Check if the ship is out of screen bounds and deactivate it if necessary
        if (x > Gdx.graphics.getWidth() || y > Gdx.graphics.getHeight()) {
            active = false;
            System.out.println("Ship was deleted.");
        }

        // Update the sprite's position
        setPosition(x, y);
    }

}




public boolean isActive(){

    return active;
}

}
