package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class CpuShip extends Ship {



    public CpuShip(Texture texture, Vector2 position, float speed, ActionState actionState, Rectangle hitbox, int actionCounter) {
        super(texture, position, speed, actionState, hitbox, actionCounter);
        this.setActionState(actionState);

    }
    void handleActionState(CpuShip ship) {
        if (ship.getActionState() == CpuShip.ActionState.U_TURN) {
            if (ship.getActionCounter() <= 90) {
                ship.setActionCounter(ship.getActionCounter() + 1);
                ship.rotate(1f);
            } else if (ship.getActionCounter() > 90) {
                ship.setActionState(CpuShip.ActionState.IDLE);
                ship.setActionCounter(0);
            }
        }
    }

    public void spawnCpuShip(Texture texture, Vector2 position, ObjectSet<CpuShip> cpuShips, ActionState actionState, Rectangle hitbox, int actionCounter) {

        CpuShip ship = new CpuShip(texture, position,  50, actionState, hitbox, actionCounter);
        ship.setPosition(position.x, position.y);
        ship.setScale(0.08f*2);
        cpuShips.add(ship);
        Gdx.app.debug("spawnShip()","Ship spawned!");
    }


}