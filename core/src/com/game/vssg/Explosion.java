package com.game.vssg;

import static com.game.vssg.VSSG.WORLD_HEIGHT;
import static com.game.vssg.VSSG.WORLD_WIDTH;
import static com.game.vssg.VSSG.isPaused;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;


public class Explosion extends Sprite {
    private Vector2 position;
    private float magnitude;
    private boolean active;
    private float speed;
    private float duration;


    public Explosion(Texture texture1, float magnitude, Vector2 position, float speed, float duration) {
        super(texture1);
        this.magnitude = magnitude;
        this.duration = duration;
        this.position = position;
        this.speed = speed;
        this.active = true;
    }


    float speedCounter = 0;
    int durationCounter = 0;

    public void update(float delta) {
        if (!isPaused) {
            if (active) {
                Vector2 velocity = new Vector2(speed, 0).setAngleDeg(getRotation());
                position.add(velocity.x * delta, velocity.y * delta);

                if (durationCounter <= duration) {
                    durationCounter++;
                } else {
                    this.active = false;
                }

                this.setRotation(this.getRotation() + 20f);

                if (speedCounter <= 44) {
                    this.setSpeed(this.getSpeed() + speedCounter);
                    // Negative values make it go downwards.
                    speedCounter += 1f;
                } else {
                    speedCounter = 0;
                    speed = 10;
                }

                if (position.x > WORLD_WIDTH || position.y > WORLD_HEIGHT) {
                    active = false;
                }

                setPosition(position.x, position.y);
            }
        }
    }

    public void spawnExplosion(Explosion explosion, ObjectSet<Explosion> explosions) {

        explosion.magnitude = magnitude;
        explosion.position = position;
        explosion.duration = duration;
        explosion.setPosition(position.x, position.y);
        explosion.setScale(magnitude);
        explosion.setOrigin(0, explosion.getHeight() / 2f);

        explosions.add(explosion);
    }

    public void setSpeed(float speed) {
        this.speed = speed;

    }

    public float getSpeed() {

        return speed;
    }

    public boolean isActive() {
        return active;
    }

    public static void explode(OrthographicCamera camera, Texture explosionTexture1, float magnitude, Vector2 position, float speed, ObjectSet<Explosion> explosions, Sound explosionSound, float duration, float rotation) {

        Explosion explosion = new Explosion(explosionTexture1, magnitude, position, speed, duration);
        explosion.setOrigin(explosion.getWidth() / 2, explosion.getHeight() / 2f);
        explosion.setRotation(rotation);
        explosion.spawnExplosion(explosion, explosions);
        explosionSound.play(0.3f);
    }

}
