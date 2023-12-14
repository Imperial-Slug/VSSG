package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class CpuShip extends Ship {



    public CpuShip(Texture texture, Vector2 position, float speed, ActionState actionState, ActionState previousActionState, Rectangle hitbox, int actionCounter, Faction faction) {
        super(texture, position, speed, actionState, previousActionState, hitbox, actionCounter, faction);
        this.setActionState(actionState, previousActionState);

    }





    public void spawnCpuShip(Texture texture, Vector2 position, ObjectSet<CpuShip> cpuShips, ActionState actionState, ActionState previousActionState, Rectangle hitbox, int actionCounter, Faction faction) {

        CpuShip ship = new CpuShip(texture, position,  50, actionState, previousActionState, hitbox, actionCounter, faction);
        ship.setPosition(position.x, position.y);
        ship.setScale(0.08f*2);
        ship.setFaction(faction);
        cpuShips.add(ship);
    }


}