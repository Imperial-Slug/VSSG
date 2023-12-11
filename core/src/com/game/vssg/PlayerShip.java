package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class PlayerShip extends Ship {

    public PlayerShip(Texture texture, Vector2 position, float speed, ActionState actionState, Rectangle hitbox, int actionCounter) {
        super(texture, position, speed, actionState, hitbox, actionCounter);
        this.setActionState(actionState);

    }

    void handleActionState(PlayerShip ship) {
        if (ship.getActionState() == Ship.ActionState.U_TURN) {
            if (ship.getActionCounter() <= 90) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(1f);
            } else if (ship.getActionCounter() > 90) {
                ship.setActionState(Ship.ActionState.IDLE);
                ship.setActionCounter(0);
            }
        }
    }

    public void spawnPlayerShip(Texture texture, Vector2 position, ObjectSet<PlayerShip> ships, ActionState actionState, Rectangle hitbox, int actionCounter) {

        PlayerShip playerShip = new PlayerShip(texture, position,  75, actionState, hitbox, actionCounter);
        playerShip.setPosition(position.x, position.y);
        playerShip.setScale(1f);
        ships.add(playerShip);
        Gdx.app.debug("spawnShip()","Ship spawned!");
    }

}