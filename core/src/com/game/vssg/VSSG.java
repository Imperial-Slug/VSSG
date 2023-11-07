package com.game.vssg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;

public class VSSG implements ApplicationListener {

	 //List<Ship> ships;
	private ObjectSet<Ship> ships;
	//List<Laser> lasers;
	private ObjectSet<Laser> lasers;
	private SpriteBatch batch;

	///////////////////////////////
	private Texture redShipTexture;
	private Texture greenLaserTexture;
	private OrthographicCamera camera;
	private float cameraSpeed = 100;
	private Viewport viewport;

	////////////////////////////////


	@Override
	public void create () {

		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		viewport = new ExtendViewport(800, 480, camera);
		batch = new SpriteBatch();

		ships = new ObjectSet<>();
		lasers = new ObjectSet<>();


		redShipTexture = new Texture("red_ship.png");
		greenLaserTexture = new Texture("laser_green.png");

		float redShipScale = 0.01f;

		Ship ship = new Ship(redShipTexture, -200, -200, 75);
		//ship.setPosition(500, 500);

		ship.setScale(redShipScale);
		ship.setRotation(0);

		ships.add(ship);
		System.out.println("ships list has "+ships.size);


	}

	@Override
	public void render () {
		//super.render();
		ScreenUtils.clear(0, 0, 0, 1);

		camera.update();
		// Set the batch's projection matrix to the camera's combined matrix
		batch.setProjectionMatrix(camera.combined);

		handleInput();


		Iterator<Ship> iter = ships.iterator();
		Iterator<Laser> laserIter = lasers.iterator();


		while (iter.hasNext()) {

			Ship ship = iter.next();
			ship.update(Gdx.graphics.getDeltaTime());

			if (!ship.isActive()) {
				iter.remove();
			//	System.out.println("ship iterator removed");
			}
		}

		while (laserIter.hasNext()) {

			Laser laser = laserIter.next();
			laser.update(Gdx.graphics.getDeltaTime());

			if (!laser.isActive()) {
				laserIter.remove();
			}
		}

		batch.begin();



		for (Laser laser : lasers) {

			laser.draw(batch);

		}
		for (Ship ship : ships) {

			ship.draw(batch);

		}
		batch.end();

		}

	public void resize (int width, int height) {
		viewport.update(width, height);
	}

	public void pause () {
	}

	public void resume () {
	}


  private void handleInput() {
	  // Rotate the sprite with left arrow key
	  if (InputManager.isAPressed()) {
		  for (Ship ship : ships) {
			  ship.rotate(+1f);
		  }
	  }

	  // Rotate the sprite with right arrow key
	  if (InputManager.isDPressed()) {
		  for (Ship ship : ships) {
			  ship.rotate(-1f);
		  }
	  }

	  // Shoot lasers
	  if (InputManager.isSpacePressed()) {

		  for (Ship ship : ships) {
			Laser laser = ship.fireLaser(ship);
			lasers.add(laser);
			System.out.println(laser.getOriginX()+" "+laser.getOriginY());

		  }
	  }


	  if (InputManager.isLeftPressed()) {
		  camera.translate(-cameraSpeed * Gdx.graphics.getDeltaTime(), 0);
	  }
	  if (InputManager.isRightPressed()) {
		  camera.translate(cameraSpeed * Gdx.graphics.getDeltaTime(), 0);
	  }
	  if (InputManager.isUpPressed()) {
		  camera.translate(0, cameraSpeed * Gdx.graphics.getDeltaTime());
	  }
	  if (InputManager.isDownPressed()) {
		  camera.translate(0, -cameraSpeed * Gdx.graphics.getDeltaTime());
	  }



  }

	@Override
	public void dispose () {
		batch.dispose();


	}




}
