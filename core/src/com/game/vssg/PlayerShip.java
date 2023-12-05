package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class PlayerShip extends Ship {

    public PlayerShip(Texture texture, Vector2 position, float speed, ObjectSet<ShipAction> actionQueue) {
        super(texture, position, speed, actionQueue);
    }

    public void spawnPlayerShip(Texture texture, Vector2 position, ObjectSet<PlayerShip> ships, ObjectSet<ShipAction> actionQueue) {

        PlayerShip playerShip = new PlayerShip(texture, position,  75, actionQueue);
        playerShip.setPosition(position.x, position.y);
        playerShip.setScale(0.08f);
        ships.add(playerShip);
        Gdx.app.debug("spawnShip()","Ship spawned!");
    }

}