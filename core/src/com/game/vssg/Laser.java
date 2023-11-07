package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;

public class Laser extends Sprite {

    private float x, y;
    private float speed;
    private boolean active;

    public Laser(Texture texture, float shipX, float shipY, float shipRotation, float speed) {
        super(texture);
        this.x = shipX;
        this.y = shipY;
        this.setPosition(shipX, shipY);
        this.setRotation(shipRotation);
        this.speed = speed;
        this.setScale(1f);
        active = true;
      //  System.out.println("Read shipX, Y: "+shipX+" "+shipY);

    }

    public void update(float delta) {

        if (active) {
            // Calculate the movement vector based on rotation
            float deltaX = speed * MathUtils.cosDeg(getRotation());
            float deltaY = speed * MathUtils.sinDeg(getRotation());

            // Update the laser's position based on the movement vector
            x += deltaX * delta;
            y += deltaY * delta;

            // Check if the laser is out of screen bounds and deactivate it if necessary
            if (x > Gdx.graphics.getWidth() || y > Gdx.graphics.getHeight()) {
                active = false;
               // System.out.println("Laser was deleted.");
            }

            // Update the sprite's position
            setPosition(x, y);

        }

    }


    public boolean isActive(){

        return active;
    }

}
