package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class PlayerShip extends Ship {

    public PlayerShip(Texture texture, Vector2 position, float speed, ActionState actionState, Rectangle hitbox, int actionCounter, Faction faction) {
        super(texture, position, speed, actionState, hitbox, actionCounter, faction);
        this.setActionState(actionState);

    }



    public void spawnPlayerShip(Texture texture, Vector2 position, ObjectSet<PlayerShip> ships, ActionState actionState, Rectangle hitbox, int actionCounter, Faction faction) {

        PlayerShip playerShip = new PlayerShip(texture, position,  75, actionState, hitbox, actionCounter, faction);
        playerShip.setPosition(position.x, position.y);
        playerShip.setScale(1f);
        ships.add(playerShip);
        Gdx.app.debug("spawnShip()","Ship spawned!");
    }

}