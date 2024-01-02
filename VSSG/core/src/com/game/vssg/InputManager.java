package com.game.vssg;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

public final class InputManager implements InputProcessor {

    public static boolean isAPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.A);
    }

    public static boolean isDPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.D);
    }

    public static boolean isSpacePressed() {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE);
    }

    public static boolean isLeftPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT);
    }

    public static boolean isRightPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT);
    }

    public static boolean isUpPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.UP);
    }

    public static boolean isDownPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.DOWN);
    }

    public static boolean isWPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.W);
    }

    public static boolean isSPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.S);
    }

    public static boolean isLeftMousePressed() { return Gdx.input.isButtonPressed(Input.Buttons.LEFT); }

    public static boolean isEscPressed() {
        return Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
    }

    public static boolean isRightMousePressed() { return Gdx.input.isButtonPressed(Input.Buttons.RIGHT);}

    public static boolean isQPressed() {return Gdx.input.isKeyPressed(Input.Keys.Q); }

    public static boolean isEPressed() {return Gdx.input.isKeyPressed(Input.Keys.E); }

    public static boolean isCPressed() {return Gdx.input.isKeyPressed(Input.Keys.C); }

    public static boolean isPPressed() {return Gdx.input.isKeyPressed(Input.Keys.P); }

    public static boolean isTPressed() {return Gdx.input.isKeyPressed(Input.Keys.T); }
    public static boolean isMiddlePressed() {return Gdx.input.isButtonPressed(Input.Buttons.MIDDLE); }


    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}