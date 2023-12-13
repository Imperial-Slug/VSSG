package com.game.vssg;

import static com.game.vssg.VSSG.playerActive;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class PlayerShip extends Ship {

    public PlayerShip(Texture texture, ShipPart exhaust, Vector2 position, float speed, ActionState actionState, Rectangle hitbox, int actionCounter, Faction faction) {
        super(texture, exhaust, position, speed, actionState, hitbox, actionCounter, faction);
        this.setActionState(actionState);

    }



    public void spawnPlayerShip(Texture texture, ShipPart exhaust, Vector2 position, ObjectSet<PlayerShip> ships, ActionState actionState, Rectangle hitbox, int actionCounter, Faction faction) {
        if (!playerActive) {
            PlayerShip playerShip = new PlayerShip(texture, exhaust, position, 0, actionState, hitbox, actionCounter, faction);
            playerShip.setPosition(position.x, position.y);
            ships.add(playerShip);
        }
        else { System.out.println("Player already created!"); }
        }


}