package com.game.vssg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class VSSG implements ApplicationListener {

    // DEBUGGING //
    public static boolean showHitBoxes = false;
    public static boolean mute = false;
    public static int zoom = 1;
    ///////////////

    private ObjectSet<PlayerShip> playerShips;
    private ObjectSet<CpuShip> cpuShips;
    private ObjectSet<Explosion> explosions;
    private ObjectSet<Laser> lasers;
    private SpriteBatch batch;
    private Sound laserSound1;
    private Sound explosionSound1;


    ///////////////////////////////
    private Texture purpleShipTexture;
    private Texture greenLaserTexture;
    private Texture explosionTexture1;
    private Texture otherShipTexture;

    private Texture blueLaserTexture;
    private Texture redLaserTexture;
    private Texture greenShipTexture;

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
    public void create() {
        // Get number of processors for future multithreading purposes.
        int processors = Runtime.getRuntime().availableProcessors();
        Gdx.app.debug("Get number of processors.", "Cores: " + processors);

        // Load assets.
        purpleShipTexture = new Texture("purple_ship.png");
        otherShipTexture = new Texture("N1.png");
        greenShipTexture = new Texture("green_ship.png");
        greenLaserTexture = new Texture("laser_green.png");
        redLaserTexture = new Texture("laser_red.png");
        blueLaserTexture = new Texture("laser_blue.png");


        laserSound1 = Gdx.audio.newSound(Gdx.files.internal("short_laser_blast.wav"));
        explosionSound1 = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        explosionTexture1 = new Texture("explosion_orange.png");


        // Setup camera, viewport, controls input.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);
        camera.position.x = (float) Gdx.graphics.getWidth() / 2;
        camera.position.y = (float) Gdx.graphics.getHeight() / 2;
        viewport = new ExtendViewport(1280, 720, camera);
        viewport.apply();
        Gdx.input.setInputProcessor(inputManager);

        // Prepare SpriteBatch and lists for keeping track of/accessing game objects.
        batch = new SpriteBatch();
        cpuShips = new ObjectSet<>();
        playerShips = new ObjectSet<>();
        explosions = new ObjectSet<>();
        lasers = new ObjectSet<>();

        //Set scales for textures.
        float purpleShipScale = 0.08f * 2;
        float speed = 50;

        // Initial ship's details.
        Vector2 vector2 = new Vector2((float) Gdx.graphics.getWidth() / 2, (float) Gdx.graphics.getHeight() / 2);
        Rectangle hitBox = new Rectangle();
        int playerActionCounter = 0;
        PlayerShip playerShip = new PlayerShip(purpleShipTexture, vector2, speed, null, hitBox, playerActionCounter, Ship.Faction.PURPLE);
        playerShip.setScale(purpleShipScale);
        playerShip.setRotation(0);

        // Add the new ship to the Ship list.
        playerShips.add(playerShip);
        Gdx.app.debug("ships.add(ship)", "ships list has " + playerShips.size);


    }

    @Override
    public void render() {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        camera.update();
        // Set the batch's projection matrix to the camera's combined matrix
        batch.setProjectionMatrix(camera.combined);

        handleInput();
        float deltaTime = Gdx.graphics.getDeltaTime();
        Iterator<PlayerShip> playerIter = playerShips.iterator();
        Iterator<CpuShip> cpuIter = cpuShips.iterator();
        Iterator<Explosion> explosionIter = explosions.iterator();
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

        while (explosionIter.hasNext()) {

            Explosion explosion = explosionIter.next();
            explosion.update(deltaTime, explosion);

            if (!explosion.isActive()) {
                explosionIter.remove();
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
                    ship.getShapeRenderer().setProjectionMatrix(camera.combined);
                    ship.drawBoundingBox();
                }

                if (laserHitBox.overlaps(shipHitBox)) {
                    Vector2 position = new Vector2(laser.getX() - 40, laser.getY() - 65);
                    Explosion.explode(camera, explosionTexture1, 10, position, 30, explosions, explosionSound1);
                    System.out.println("Ship hit.");
                    ship.setInactive(ship);
                    laser.setInactive(laser);

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
            cpuShip.handleActionState(cpuShip);
        }

        for (Explosion explosion : explosions) {
            explosion.draw(batch);
            explosion.update(deltaTime, explosion);
        }

        batch.end();

    }


    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    public void pause() {
    }

    public void resume() {
    }


    private void handleInput() {

        float cameraSpeed = 200;

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


        if (InputManager.isRightMousePressed()) {
            Vector2 position = new Vector2(camera.position.x, camera.position.y);
            Explosion.explode(camera, explosionTexture1, 10, position, 30, explosions, explosionSound1);

        }


        if (shipSpawnTimeout) {
            if (shipSpawnCounter >= 50) {

                shipSpawnTimeout = false;
            } else {
                shipSpawnCounter++;
            }
        }

        if (laserSpawnTimeout) {
            if (laserSpawnCounter >= 50) {

                laserSpawnTimeout = false;
            } else {
                laserSpawnCounter++;
            }
        }

        int actionCounter = 0;

        if (InputManager.isLeftMousePressed()) {
            if (!shipSpawnTimeout) {
                Vector2 position = new Vector2(camera.position.x, camera.position.y);
                CpuShip.ActionState actionState = Ship.ActionState.IDLE;
                Rectangle hitBox = new Rectangle();
                CpuShip cpuShip = new CpuShip(greenShipTexture, position, 50, actionState, hitBox, actionCounter, Ship.Faction.TEAL);
                cpuShip.spawnCpuShip(greenShipTexture, position, cpuShips, actionState, hitBox, actionCounter, Ship.Faction.TEAL);
                Gdx.app.debug("Left Mouse Press", "Left mouse pressed!");
                shipSpawnTimeout = true;
                shipSpawnCounter = 0;
            } else {
                System.out.println("spawnTimeout: " + shipSpawnCounter);
            }
        }

        // Speed up.
        if (InputManager.isWPressed()) {
            for (PlayerShip playerShip : playerShips) {
                playerShip.setSpeed(playerShip.getSpeed() + 0.5f);
            }
        }

        // Slow down.
        if (InputManager.isSPressed()) {
            for (PlayerShip playerShip : playerShips) {
                playerShip.setSpeed(playerShip.getSpeed() - 0.5f);
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
    public void dispose() {
        batch.dispose();
        purpleShipTexture.dispose();
        greenLaserTexture.dispose();
        blueLaserTexture.dispose();
        redLaserTexture.dispose();
        greenShipTexture.dispose();
        explosionSound1.dispose();
        laserSound1.dispose();
        explosionTexture1.dispose();
        otherShipTexture.dispose();

    }


}