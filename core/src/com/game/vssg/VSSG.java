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


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;

public class VSSG extends ApplicationAdapter {

	//int screenWidth = Gdx.graphics.getWidth();
	//int screenHeight = Gdx.graphics.getHeight();
	private List<Ship> ships;
	private SpriteBatch batch;

	///////////////////////////////
	private Texture redShipTexture;
	Sprite redShip;
	float speed;
	float shipX;
	float shipY;
	//Texture blueShip;
	float deltaX;
	float deltaY;
    float shipRotation;
	////////////////////////////////



	@Override
	public void create () {
		batch = new SpriteBatch();

		ships = new ArrayList<>();


		redShipTexture = new Texture("red_ship.png");
		//redShip = new Sprite(redShipTexture);
		//redShip.setPosition(-200, -200);
		float redShipScale = 0.01f;

		Ship ship = new Ship(redShipTexture, -200, -200, 100); // Change the speed as needed

		ship.setScale(redShipScale);
		ship.setRotation(0);

		ships.add(ship);

	}

	@Override
	public void render () {
handleInput();
		ScreenUtils.clear(0, 0, 0, 1);

		Iterator<Ship> iter = ships.iterator();

		while (iter.hasNext()) {

			Ship ship = iter.next();
			ship.update(Gdx.graphics.getDeltaTime());

			if (!ship.isActive()) {
				iter.remove();
			}
		}


		batch.begin();

		for (Ship ship : ships) {

			ship.draw(batch);

		}

		batch.end();

		}	

		private void fireLaser() {
System.out.println("Firing a laser.");

		}

  private void handleInput() {
	  // Rotate the sprite with left arrow key
	  if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

		  for (Ship ship : ships) {
			  ship.rotate(+1f);
		  }
	  }

	  // Rotate the sprite with right arrow key
	  if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
		  for (Ship ship : ships) {
			  ship.rotate(-1f);
		  }
	  }

	  // Shoot lasers
	  if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {

		  fireLaser();

	  }


  }

	@Override
	public void dispose () {
		batch.dispose();
		redShipTexture.dispose();
		//blueShip.dispose();
	}




}
