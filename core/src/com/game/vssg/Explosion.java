package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;


public class Explosion extends Sprite {
    private Vector2 position;
    private ShapeRenderer shapeRenderer;
    private Texture texture2;
    private Texture texture3;
    private int magnitude;
    private boolean active;
    private float speed;

    private Texture texture1;

    ///// Constructors ////////

    public Explosion (Texture texture1, int magnitude, Vector2 position, float speed) {
        super(texture1);
        this.magnitude = magnitude;
        this.texture1 = texture1;

        this.position = position;
        this.speed = speed;
        active = true;
    }

    public Explosion (Texture texture1, Texture texture2, int magnitude, Vector2 position, float speed) {

        this.texture1 = texture1;
        this.texture2 = texture2;
        this.magnitude = magnitude;

        this.position = position;
        this.speed = speed;
        this.active = true;
    }

    public Explosion (Texture texture1, Texture texture2, Texture texture3, int magnitude, Vector2 position, float speed) {
        this.texture1 = texture1;
        this.texture2 = texture2;
        this.texture3 = texture3;
        this.magnitude = magnitude;

        this.position = position;
        this.speed = speed;
        this.active = true;
    }

/////////////////

    public void update(float delta, Explosion explosion) {

        if (active) {
            Vector2 velocity = new Vector2(speed, 0).setAngleDeg(getRotation());
            position.add(velocity.x * delta, velocity.y * delta);

            if (position.x > Gdx.graphics.getWidth() || position.y > Gdx.graphics.getHeight()) {
                active = false;
            }

            setPosition(position.x, position.y);
        }
    }

    public void spawnExplosion(Texture texture, Vector2 position, int magnitude, float speed, ObjectSet<Explosion> explosions) {
        this.magnitude = magnitude;
        this.position = position;
        Explosion explosion = new Explosion(texture1, 10, position, speed);
        explosion.setPosition(position.x, position.y);
        explosion.setScale(0.08f);
        explosions.add(explosion);
    }


    public boolean isActive() {
        return active;
    }
}
