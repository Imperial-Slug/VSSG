package com.game.vssg;

import static com.game.vssg.VSSG.WORLD_HEIGHT;
import static com.game.vssg.VSSG.WORLD_WIDTH;
import static com.game.vssg.VSSG.isPaused;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;


public class Explosion extends Sprite {
    private final Vector2 position;
    private boolean active;
    private float speed;
    private final float duration;


    public Explosion(Texture texture1, Vector2 position, float speed, float duration) {
        super(texture1);
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
if(!isPaused){

        explosion.setOrigin(0, explosion.getHeight() / 2f);

        explosions.add(explosion);
}
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

    public static void explode(Texture explosionTexture1, Vector2 position, float speed, ObjectSet<Explosion> explosions, Sound explosionSound, float duration, float magnitude) {

        Explosion explosion = new Explosion(explosionTexture1, position, speed, duration);
        explosion.setOrigin(explosion.getWidth() / 2, explosion.getHeight() / 2f);
        explosion.setRotation(10);
        explosion.setSpeed(70);
        explosion.spawnExplosion(explosion, explosions);
        explosionSound.play(0.04f);
        explosion.setScale(magnitude);
    }

}
