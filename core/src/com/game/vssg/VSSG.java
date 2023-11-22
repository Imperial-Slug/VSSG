package com.game.vssg;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
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
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.Box;

public class VSSG implements ApplicationListener {

//	private ObjectSet<Ship> ships;
	private ObjectSet<PlayerShip> playerShips;
	private ObjectSet<CpuShip> cpuShips;

	private ObjectSet<Laser> lasers;
	private SpriteBatch batch;
	Sound laserSound1;

	///////////////////////////////
	private Texture redShipTexture;
	private Texture greenLaserTexture;

	private OrthographicCamera camera;
	private Viewport viewport;
	//Vector3 tp = new Vector3();
	boolean dragging;

	////////////////////////////////
  InputProcessor inputManager;

	@Override
	public void create () {
		// Get number of processors for multithreading purposes.
		int processors = Runtime.getRuntime().availableProcessors();
		Gdx.app.debug("Get number of processors.","Cores: " + processors);

		// Load assets.
		redShipTexture = new Texture("red_ship.png");
		greenLaserTexture = new Texture("laser_green.png");
		laserSound1 = Gdx.audio.newSound(Gdx.files.internal("short_laser_blast.wav"));

		// Setup camera, viewport, controls input.
		camera = new OrthographicCamera();
		camera.setToOrtho(false, 800, 480);
		camera.position.x = (float) Gdx.graphics.getWidth() /2;
		camera.position.y = (float) Gdx.graphics.getHeight() /2;
		viewport = new ExtendViewport(800, 480, camera);
		Gdx.input.setInputProcessor(inputManager);

		// Prepare SpriteBatch and lists for keeping track of/accessing game objects.
		batch = new SpriteBatch();
		cpuShips = new ObjectSet<>();
		playerShips = new ObjectSet<>();
		lasers = new ObjectSet<>();

		//Set scales for textures.
		float redShipScale = 0.08f;
	    float speed = 75;

		// Initial ship's details.
		Vector2 vector2 = new Vector2((float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
		PlayerShip playerShip = new PlayerShip(redShipTexture, vector2, speed);
		playerShip.setScale(redShipScale);
		playerShip.setRotation(0);

		// Add the new ship to the Ship list.
		playerShips.add(playerShip);
		Gdx.app.debug("ships.add(ship)","ships list has "+playerShips.size);


	}

	@Override
	public void render () {
		//super.render();
		ScreenUtils.clear(0, 0, 0, 1);

		camera.update();
		// Set the batch's projection matrix to the camera's combined matrix
		batch.setProjectionMatrix(camera.combined);

		handleInput();

		Iterator<PlayerShip> playerIter = playerShips.iterator();
		Iterator<CpuShip> cpuIter = cpuShips.iterator();

		Iterator<Laser> laserIter = lasers.iterator();


		while (playerIter.hasNext()) {

			Ship ship = playerIter.next();
			ship.update(Gdx.graphics.getDeltaTime());

			if (!ship.isActive()) {
				playerIter.remove();
			}
		}

		while (cpuIter.hasNext()) {

			Ship ship = cpuIter.next();
			ship.update(Gdx.graphics.getDeltaTime());

			if (!ship.isActive()) {
				cpuIter.remove();
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

		for (Ship playerShip : playerShips) {
			playerShip.draw(batch);
		}

		for (Ship cpuShip : cpuShips) {
			cpuShip.draw(batch);
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
		  for (Ship ship : playerShips) {
			  ship.rotate(+1f);
		  }
	  }


	  // Rotate the sprite with right arrow key
	  if (InputManager.isDPressed()) {
		  for (Ship ship : playerShips) {
			  ship.rotate(-1f);
		  }
	  }

	  // Shoot lasers
	  if (InputManager.isSpacePressed()) {

		  for (Ship ship : playerShips) {
			Laser laser = ship.fireLaser(greenLaserTexture, ship);
			lasers.add(laser);
			laserSound1.play(0.5f);

			  if (laserSound1 == null) {
				  Gdx.app.error("Sound", "Sound file not loaded!");
			  }
		  }

	  }


	  if (InputManager.isLeftMousePressed()) {
		  Vector2 position = new Vector2((float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
		  Ship ship = new Ship(redShipTexture, position, 75);
		  ship.spawnCpuShip(redShipTexture, position, cpuShips);
		  Gdx.app.debug("Left Mouse Press","Left mouse pressed!");
	  }

	  //Speed up.
	  if (InputManager.isWPressed()) {
		  for (Ship ship : playerShips) {
			  ship.setSpeed(ship.getSpeed()+2);
		  }
	  }

	  // Slow down.
	  if (InputManager.isSPressed()) {
		  for (Ship ship : playerShips) {
			  ship.setSpeed(ship.getSpeed()-2);
		  }
	  }

	  float cameraSpeed = 100;
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




}
