package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.awt.Button;

public class InputManager {

    public static boolean isAPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.A);
    }

    public static boolean isDPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.D);
    }

    public static boolean isSpacePressed() {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }

    // Camera controls.
    public static boolean isLeftPressed() { return Gdx.input.isKeyPressed(Input.Keys.LEFT); }
    public static boolean isRightPressed() { return Gdx.input.isKeyPressed(Input.Keys.RIGHT); }

    public static boolean isUpPressed() { return Gdx.input.isKeyPressed(Input.Keys.UP); }

    public static boolean isDownPressed() { return Gdx.input.isKeyPressed(Input.Keys.DOWN); }

    public static boolean isWPressed() { return Gdx.input.isKeyPressed(Input.Keys.W); }
    public static boolean isSPressed() { return Gdx.input.isKeyPressed(Input.Keys.S); }

    public static boolean isLeftMousePressed() { return Gdx.input.isButtonPressed(Input.Buttons.LEFT); }


}