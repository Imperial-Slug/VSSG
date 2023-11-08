package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class Ship extends Sprite {

    private Vector2 position;
    private float speed;
    private boolean active;

    private Texture texture;
    Texture greenLaserTexture = new Texture("laser_green.png");
    Texture redShipTexture = new Texture("red_ship.png");

    public Ship(Texture texture, Vector2 position, float speed) {
        super(texture);
        this.position = position;
        this.speed = speed;
        this.texture = texture;
        active = true;
    }

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
        //System.out.println("Speed is "+speed);
    }

//////////////////////////
    public Ship spawnShip(Vector2 position, ObjectSet<Ship> ships) {


        Ship ship = new Ship(redShipTexture, position,  75);
        ship.setPosition(position.x, position.y);
        ship.setScale(0.08f);
        ships.add(ship);
        System.out.println("Ship spawned!");
        return ship;
    }

    //////////////////////////
    public Laser fireLaser(Ship ship) {
        float offsetX = -10;
        float offsetY = -1.5f;

        Vector2 laserPosition = new Vector2(ship.getX() + ship.getOriginX() + offsetX, ship.getY() + ship.getOriginY() + offsetY);
        Laser laser = new Laser(greenLaserTexture, laserPosition.x, laserPosition.y, ship.getRotation(), 500);
        laser.setPosition(laserPosition.x, laserPosition.y);
        laser.setScale(0.5f);
        return laser;
    }




public boolean isActive(){

    return active;
}


}
