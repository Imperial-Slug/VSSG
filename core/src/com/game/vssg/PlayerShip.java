package com.game.vssg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.UUID;

public class PlayerShip extends Ship {

    public PlayerShip(UUID uuid, Texture texture, Vector2 position, float speed, ActionState actionState, ActionState previousActionState,
                      Rectangle hitbox, int actionCounter, Faction faction, ObjectSet<Ship> targets, int hp) {
        super(uuid, texture, position, speed, actionState, previousActionState, faction, targets, hp);
        this.setActionState(actionState, previousActionState);

    }


}