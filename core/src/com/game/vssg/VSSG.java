package com.game.vssg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


import javax.swing.Box;

public class VSSG extends ApplicationAdapter {

	//int screenWidth = Gdx.graphics.getWidth();
	//int screenHeight = Gdx.graphics.getHeight();

	private SpriteBatch batch;
	Texture redShipTexture;
	Sprite redShip;
	float speed;
	float shipX;
	float shipY;
	//Texture blueShip;
	float deltaX;
	float deltaY;
    float shipRotation;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		redShipTexture = new Texture("red_ship.png");
		redShip = new Sprite(redShipTexture);
		redShip.setPosition(-200, -200);
		float redShipScale = 0.25f;
		redShip.setScale(redShipScale);
		redShip.setRotation(0);



	//	blueShip = new Texture("blue_ship.png");
	}

	@Override
	public void render () {
handleInput();
		ScreenUtils.clear(0, 0, 0, 1);
		//redShip.setRotation(redShip.getRotation());
		speed = 200;
		shipRotation = redShip.getRotation();

		deltaX = speed * MathUtils.cosDeg(shipRotation);
		deltaY = speed * MathUtils.sinDeg(shipRotation);

		shipX = redShip.getX() + (deltaX * Gdx.graphics.getDeltaTime());
		shipY = redShip.getY() + (deltaY * Gdx.graphics.getDeltaTime());

		redShip.setPosition(shipX, shipY);

		batch.begin();
	    redShip.draw(batch);
  
		//batch.draw(blueShip, 25, 25);


		batch.end();



		}	

		

  private void handleInput() {
	  // Rotate the sprite with left arrow key
	  if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
		  redShip.rotate(1f); // Adjust the rotation speed as needed
	  }

	  // Rotate the sprite with right arrow key
	  if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
		  redShip.rotate(-1f); // Adjust the rotation speed as needed
	  }
  }

	@Override
	public void dispose () {
		batch.dispose();
		redShipTexture.dispose();
		//blueShip.dispose();
	}
}
