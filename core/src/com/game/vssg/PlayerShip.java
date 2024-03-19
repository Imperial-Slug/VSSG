package com.game.vssg;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;

public class PlayerShip extends Ship {

    public PlayerShip(Texture texture, Sprite exhaustTexture, Vector2 position, float speed, ActionState actionState, ActionState previousActionState,
                      Rectangle hitbox, int actionCounter, Faction faction, ObjectSet<Ship> targets, int hp, Type type, float rotation) {
        super(texture, exhaustTexture, position, speed, actionState, previousActionState, faction, targets, hp, type);
        this.setActionState(actionState, previousActionState);

    }




}