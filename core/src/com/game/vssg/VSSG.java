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
import com.badlogic.gdx.math.Rectangle;
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

	// DEBUGGING //
	public static boolean showHitBoxes = false;

	///////////////

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
	boolean shipSpawnTimeout = false;
	int shipSpawnCounter = 0;
	boolean laserSpawnTimeout = false;
	int laserSpawnCounter = 0;
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
		camera.setToOrtho(false, 1280, 720);
		camera.position.x = (float) Gdx.graphics.getWidth() /2;
		camera.position.y = (float) Gdx.graphics.getHeight() /2;
		viewport = new ExtendViewport(1280, 720, camera);
		viewport.apply();
		Gdx.input.setInputProcessor(inputManager);

		// Prepare SpriteBatch and lists for keeping track of/accessing game objects.
		batch = new SpriteBatch();
		cpuShips = new ObjectSet<>();
		playerShips = new ObjectSet<>();
		lasers = new ObjectSet<>();

		//Set scales for textures.
		float redShipScale = 0.08f;
		float speed = 50;

		// Initial ship's details.
		Vector2 vector2 = new Vector2((float) Gdx.graphics.getWidth() /2, (float) Gdx.graphics.getHeight() /2);
		ObjectSet<ShipAction> actionQueue = new ObjectSet<>();
		Rectangle hitbox = new Rectangle();
		PlayerShip playerShip = new PlayerShip(redShipTexture, vector2, speed, actionQueue, null, hitbox);
		playerShip.setScale(redShipScale);
		playerShip.setRotation(0);

		// Add the new ship to the Ship list.
		playerShips.add(playerShip);
		Gdx.app.debug("ships.add(ship)","ships list has "+playerShips.size);


	}

	@Override
	public void render () {
		//super.render();
		ScreenUtils.clear(0, 0, 0.5f, 1);

		camera.update();
		// Set the batch's projection matrix to the camera's combined matrix
		batch.setProjectionMatrix(camera.combined);

		handleInput();
		float deltaTime = Gdx.graphics.getDeltaTime();
		Iterator<PlayerShip> playerIter = playerShips.iterator();
		Iterator<CpuShip> cpuIter = cpuShips.iterator();

		Iterator<Laser> laserIter = lasers.iterator();


		while (playerIter.hasNext()) {

			PlayerShip playerShip = playerIter.next();
			playerShip.update(deltaTime, playerShip);

			if (!playerShip.isActive()) {
				playerIter.remove();
			}
		}

		while (cpuIter.hasNext()) {

			CpuShip cpuShip = cpuIter.next();
			cpuShip.update(deltaTime, cpuShip);

			if (!cpuShip.isActive()) {
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
			Rectangle laserHitBox = laser.getHitbox();
			laser.update(deltaTime);
			laser.updateHitBox(laser);

			for (CpuShip ship : cpuShips) {
				Rectangle shipHitBox = ship.getHitbox();

				if (showHitBoxes) {
					ship.shapeRenderer.setProjectionMatrix(camera.combined);
					ship.drawBoundingBox();
				}

				if (laserHitBox.overlaps(shipHitBox)) {
					System.out.println("Ship hit.");
					ship.setInactive(ship);

				}

			}



		}

		for (PlayerShip playerShip : playerShips) {
			playerShip.draw(batch);
			playerShip.update(deltaTime, playerShip);
		}

		for (CpuShip cpuShip : cpuShips) {
			cpuShip.draw(batch);
			cpuShip.update(deltaTime, cpuShip);

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

		float cameraSpeed = 150;

		// Rotate the sprite with left arrow key
		if (InputManager.isAPressed()) {
			for (Ship ship : playerShips) {
				ship.rotate(+0.3f);
			}
		}


		// Rotate the sprite with right arrow key
		if (InputManager.isDPressed()) {
			for (Ship ship : playerShips) {
				ship.rotate(-0.3f);
			}
		}

		// Shoot lasers
		if (InputManager.isSpacePressed()) {
			if (!laserSpawnTimeout) {
				for (Ship ship : playerShips) {
					Laser laser = ship.fireLaser(greenLaserTexture, ship);
					lasers.add(laser);
					laserSound1.play(0.5f);
					laserSpawnTimeout = true;
					laserSpawnCounter = 0;

					if (laserSound1 == null) {
						System.out.println("Sound file not loaded!");
					}
				}
			}
		}

		if (shipSpawnTimeout) {
			if (shipSpawnCounter >= 50) {

				shipSpawnTimeout = false;
			}
			else { shipSpawnCounter++; }
		}

		if (laserSpawnTimeout) {
			if (laserSpawnCounter >= 50) {

				laserSpawnTimeout = false;
			}
			else { laserSpawnCounter++; }
		}


		if (InputManager.isLeftMousePressed()) {
			if (!shipSpawnTimeout) {
				Vector2 position = new Vector2((float) camera.position.x, (float) camera.position.y);
				ObjectSet<ShipAction> actionQueue = new ObjectSet<>();
				Ship.ActionState actionState = null;
				Rectangle hitbox = new Rectangle();
				CpuShip cpuShip = new CpuShip(redShipTexture, position, 50, actionQueue, null, hitbox);
				cpuShip.spawnCpuShip(redShipTexture, position, cpuShips, actionQueue, null, hitbox);
				Gdx.app.debug("Left Mouse Press", "Left mouse pressed!");
				shipSpawnTimeout = true;
				shipSpawnCounter = 0;
			}
			else {
				System.out.println("spawnTimeout: "+shipSpawnCounter);
			}
		}


		// Speed up.
		if (InputManager.isWPressed()) {
			for (Ship ship : playerShips) {
				ship.setSpeed(ship.getSpeed()+0.5f);
			}
		}

		// Slow down.
		if (InputManager.isSPressed()) {
			for (Ship ship : playerShips) {
				ship.setSpeed(ship.getSpeed()-0.5f);
			}
		}

		if (InputManager.isEscPressed()) {
			Gdx.app.exit();
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




}