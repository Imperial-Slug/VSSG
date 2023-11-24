package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class CpuShip extends Ship {


    public CpuShip(Texture texture, Vector2 position, float speed, ObjectSet<ShipAction> actionQueue) {
        super(texture, position, speed, actionQueue);
    }


    public void spawnCpuShip(Texture texture, Vector2 position, ObjectSet<CpuShip> ships, ObjectSet<ShipAction> actionQueue) {

        CpuShip ship = new CpuShip(texture, position,  75, actionQueue);
        ship.setPosition(position.x, position.y);
        ship.setScale(0.08f);
        ships.add(ship);
        Gdx.app.debug("spawnShip()","Ship spawned!");
    }



}
