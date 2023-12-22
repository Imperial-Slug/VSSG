package com.game.vssg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;


public class CpuShip extends Ship {


    public CpuShip( Texture texture, Vector2 position, float speed, ActionState actionState,
                   ActionState previousActionState, Rectangle hitbox, int actionCounter, Faction faction,
                   ObjectSet<Ship> targets, int hp) {
        super(texture, position, speed, actionState, previousActionState, faction, targets, hp);
        this.setActionState(actionState, previousActionState);

    }


}