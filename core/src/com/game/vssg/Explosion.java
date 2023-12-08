package com.game.vssg;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;


public class Explosion {
    private ShapeRenderer shapeRenderer;
    private Texture texture1;
    private Texture texture2;
    private Texture texture3;
    private int magnitude;


    public Explosion (Texture texture1, int magnitude) {
        this.texture1 = texture1;
        this.magnitude = magnitude;

    }

    public Explosion (Texture texture1, Texture texture2, int magnitude) {

        this.texture1 = texture1;
        this.texture2 = texture2;
        this.magnitude = magnitude;
    }

    public Explosion (Texture texture1, Texture texture2, Texture texture3, int magnitude) {
        this.texture1 = texture1;
        this.texture2 = texture2;
        this.texture3 = texture3;
        this.magnitude = magnitude;
    }


}
