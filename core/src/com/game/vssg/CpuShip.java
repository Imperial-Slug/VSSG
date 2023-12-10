package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

import javax.swing.Action;

public class CpuShip extends Ship {



    public CpuShip(Texture texture, Vector2 position, float speed, ObjectSet<ShipAction> actionQueue, ActionState actionState, Rectangle hitbox) {
        super(texture, position, speed, actionQueue, actionState, hitbox);
        this.actionState = actionState;
    }


    public void spawnCpuShip(Texture texture, Vector2 position, ObjectSet<CpuShip> ships, ObjectSet<ShipAction> actionQueue, ActionState actionState, Rectangle hitbox) {

        CpuShip ship = new CpuShip(texture, position,  50, actionQueue, actionState, hitbox);
        ship.setPosition(position.x, position.y);
        ship.setScale(0.08f*2);
        ships.add(ship);
        Gdx.app.debug("spawnShip()","Ship spawned!");
    }




    public void dispose() {
        this.dispose();
    }
}