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
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.Box;

public class VSSG extends ApplicationAdapter {

	 //List<Ship> ships;
	private ObjectSet<Ship> ships;
	//List<Laser> lasers;
	private ObjectSet<Laser> lasers;
	SpriteBatch batch;

	///////////////////////////////
	private Texture redShipTexture;
	private Texture greenLaserTexture;
	private OrthographicCamera camera;
	float cameraSpeed = 100;


	////////////////////////////////


	@Override
	public void create () {
		int screenWidth = Gdx.graphics.getWidth();
		int screenHeight = Gdx.graphics.getHeight();

		camera = new OrthographicCamera();
		camera.setToOrtho(false, screenWidth, screenHeight);

		batch = new SpriteBatch();

		//ships = new ArrayList<>();
		//lasers = new ArrayList<>();
		ships = new ObjectSet<>();
		lasers = new ObjectSet<>();


		redShipTexture = new Texture("red_ship.png");
		greenLaserTexture = new Texture("laser_green.png");

		float redShipScale = 0.01f;

		Ship ship = new Ship(redShipTexture, -400, -300, 75);

		ship.setScale(redShipScale);
		ship.setRotation(0);

		ships.add(ship);
		System.out.println("ships list has "+ships.size);

	}

	@Override
	public void render () {
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

		for (Ship ship : ships) {

			ship.draw(batch);

		}

		for (Laser laser : lasers) {

			laser.draw(batch);

		}

		batch.end();

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
			Laser laser = fireLaser(ship);
			lasers.add(laser);
			//System.out.println(laser.getX()+" "+laser.getY());

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
		redShipTexture.dispose();
		greenLaserTexture.dispose();
	}


	Laser fireLaser(Ship ship) {

		//System.out.println("Firing a laser.");

		float laserX = ship.getX();
		float laserY = ship.getY();
		Laser laser = new Laser(greenLaserTexture, laserX, laserY, ship.getRotation(), 500);
		return laser;
	}

}
