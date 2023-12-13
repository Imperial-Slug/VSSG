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
import java.util.Random;

public class VSSG implements ApplicationListener {

    // DEBUGGING //
    public static boolean showHitBoxes = false;
    public static boolean mute = false;
    ///////////////

    public static long WORLD_CONSTANT = 16384;
    public static long WORLD_WIDTH = WORLD_CONSTANT;
    public static long WORLD_HEIGHT = WORLD_CONSTANT;

    private ObjectSet<PlayerShip> playerShips;
    private ObjectSet<CpuShip> cpuShips;
    private ObjectSet<Explosion> explosions;
    private ObjectSet<Laser> lasers;
    private SpriteBatch batch;
    private Sound explosionSound1;
    private Sound laserBlast1;
    private Sound laserBlast2;
    private final float worldWidthCentre = (float) WORLD_WIDTH / 2;
    private final float worldHeightCentre = (float) WORLD_HEIGHT / 2;
    private final float wrapDivisor = (float) WORLD_WIDTH / 4096;
    private final float zoomSpeed = 0.002f;

    private Texture purpleShipTexture;
    private Texture greenLaserTexture;
    private Texture explosionTexture1;
    private Texture otherShipTexture;
    private Texture blueLaserTexture;
    private Texture redLaserTexture;
    private Texture greenShipTexture;
    private Texture backgroundTexture;
    private Texture exhaustTexture;

    private OrthographicCamera camera;
    private Viewport viewport;
    private boolean shipSpawnTimeout = false;
    private int shipSpawnCounter = 0;
    private boolean laserSpawnTimeout = false;
    public static boolean playerActive = false;
    private int laserSpawnCounter = 0;

    ////////////////////////////////
    InputProcessor inputManager;

    @Override
    public void create() {
        // Get number of processors for future multithreading purposes.
        int processors = Runtime.getRuntime().availableProcessors();
       System.out.println("Get number of processor cores: " + processors);

        // Load assets.
        purpleShipTexture = new Texture("purple_ship.png");
        otherShipTexture = new Texture("N1.png");
        greenShipTexture = new Texture("green_ship.png");
        greenLaserTexture = new Texture("laser_green.png");
        redLaserTexture = new Texture("laser_red.png");
        blueLaserTexture = new Texture("laser_blue.png");
        backgroundTexture = new Texture("background.png");
        explosionSound1 = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        laserBlast2 = Gdx.audio.newSound(Gdx.files.internal("laser_blast2.wav"));
        explosionTexture1 = new Texture("explosion_orange.png");
        exhaustTexture = new Texture("ship_exhaust.png");

        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);



        // Setup camera, viewport, controls input.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, WORLD_WIDTH, WORLD_HEIGHT);
        camera.position.x = worldWidthCentre;
        camera.position.y = worldHeightCentre;
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();
        viewport = new ExtendViewport(viewportWidth, viewportHeight, camera);
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
        float speed = 40;


        // Initial ship's details.
        Vector2 vector2 = new Vector2(worldWidthCentre, worldHeightCentre);
        Rectangle hitBox = new Rectangle();
        int playerActionCounter = 0;
        PlayerShip playerShip = new PlayerShip(purpleShipTexture, vector2, speed, null, hitBox, playerActionCounter, Ship.Faction.PURPLE);
        playerShip.setScale(purpleShipScale);
        playerShip.setRotation(0);

        // Add the new ship to the Ship list.
        playerShips.add(playerShip);

    }

    @Override
    public void render() {

        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        handleInput();
        float deltaTime = Gdx.graphics.getDeltaTime();
        Iterator<PlayerShip> playerIter = playerShips.iterator();
        Iterator<CpuShip> cpuIter = cpuShips.iterator();
        Iterator<Explosion> explosionIter = explosions.iterator();
        Iterator<Laser> laserIter = lasers.iterator();


        while (playerIter.hasNext()) {

            PlayerShip playerShip = playerIter.next();
            playerShip.update(deltaTime, playerShip, WORLD_WIDTH, WORLD_HEIGHT);

            if (!playerShip.isActive()) {
                playerIter.remove();
            }
        }

        while (cpuIter.hasNext()) {

            CpuShip cpuShip = cpuIter.next();
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);

            if (!cpuShip.isActive()) {
                cpuIter.remove();
            }
        }

        while (explosionIter.hasNext()) {

            Explosion explosion = explosionIter.next();
            explosion.update(deltaTime);

            if (!explosion.isActive()) {
                explosionIter.remove();
            }
        }

        while (laserIter.hasNext()) {

            Laser laser = laserIter.next();
            laser.update(deltaTime, WORLD_WIDTH, WORLD_HEIGHT, laser.getDespawnCounter(), laser.getShip());

            if (!laser.isActive()) {
                laserIter.remove();
            }
        }

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, 0, 0, (int) wrapDivisor, (int) wrapDivisor);

        for (Laser laser : lasers) {
            laser.setScale(1);
            laser.draw(batch);
            Rectangle laserHitBox = laser.getHitbox();
            laser.update(deltaTime, WORLD_WIDTH, WORLD_HEIGHT, laser.getDespawnCounter(), laser.getShip());
            laser.updateHitBox(laser);

            for (CpuShip ship : cpuShips) {
                Rectangle shipHitBox = ship.getHitbox();

                if (showHitBoxes) {
                    ship.getShapeRenderer().setProjectionMatrix(camera.combined);
                    ship.drawBoundingBox();
                }

                if (laserHitBox.overlaps(shipHitBox) && laser.getShip().getFaction() != ship.getFaction()) {
                    Vector2 position = new Vector2(laser.getX() - 40, laser.getY() - 65);
                    Explosion.explode(camera, explosionTexture1, 0.08f, position, 30, explosions, explosionSound1, 300, 10);
                    System.out.println("Ship hit.");
                    ship.setInactive(ship);
                    laser.setInactive(laser);

                }
            }
        }

        for (PlayerShip playerShip : playerShips) {
            playerShip.draw(batch);
            playerShip.update(deltaTime, playerShip, WORLD_WIDTH, WORLD_HEIGHT);
           playerShip.setOrigin(-64, ((playerShip.getHeight())/2) );

            float playerX = playerShip.getX();
            float playerY = playerShip.getY();

            camera.position.x = playerX;
            camera.position.y = playerY;

            //Explosion for Ship exhaust
            Vector2 position = new Vector2((playerX+(playerShip.getOriginX())), playerY+(playerShip.getOriginY()-64));
            Explosion.explode(camera, exhaustTexture, 0.08f, position, 10, explosions, explosionSound1, 0.5f, playerShip.getRotation());


        }

        for (CpuShip cpuShip : cpuShips) {
            cpuShip.draw(batch);
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);
            cpuShip.handleActionState(cpuShip);
        }

        for (Explosion explosion : explosions) {
            explosion.draw(batch);
            explosion.update(deltaTime);
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

        float cameraSpeed = camera.zoom * 2048;

        //System.out.println("Zoom: "+camera.zoom);

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
        float half = 0.5f;
        if (InputManager.isSpacePressed()) {
            if (!laserSpawnTimeout) {
                for (Ship ship : playerShips) {

                    Laser laser = ship.fireLaser(greenLaserTexture, ship);
                    laser.setShip(ship);
                    lasers.add(laser);

                        laserBlast2.play(1.0f);
                        laserSpawnTimeout = true;
                        laserSpawnCounter = 0;

                }
            }
        }


        if (InputManager.isRightMousePressed()) {
            Vector2 position = new Vector2(camera.position.x, camera.position.y);
            Explosion.explode(camera, explosionTexture1, 0.08f, position, 30, explosions, explosionSound1, 100, 10);

        }


        if (shipSpawnTimeout) {
            if (shipSpawnCounter >= 90) {

                shipSpawnTimeout = false;
            } else {
                shipSpawnCounter++;
            }
        }

        if (laserSpawnTimeout) {
            if (laserSpawnCounter >= 200) {

                laserSpawnTimeout = false;
            } else {
                laserSpawnCounter++;
            }
        }

        int actionCounter = 0;

        if (InputManager.isLeftMousePressed()) {
            if (!shipSpawnTimeout) {
                Vector2 position = new Vector2((float) WORLD_WIDTH /2, (float) WORLD_HEIGHT /2);
                CpuShip.ActionState actionState = Ship.ActionState.IDLE;
                Rectangle hitBox = new Rectangle();
                CpuShip cpuShip = new CpuShip(greenShipTexture, position, 40, actionState, hitBox, actionCounter, Ship.Faction.TEAL);
                cpuShip.spawnCpuShip(greenShipTexture, position, cpuShips, actionState, hitBox, actionCounter, Ship.Faction.TEAL);
                shipSpawnTimeout = true;
                shipSpawnCounter = 0;
            }
        }

        float speedLimit = 200f;
        // Speed up.
        if (InputManager.isWPressed()) {

            for (PlayerShip playerShip : playerShips) {

                if (playerShip.getSpeed() < speedLimit && playerShip.getSpeed() >= 0) {
                    playerShip.setSpeed(playerShip.getSpeed() + half);
                }

            }
        }

        // Slow down.
        if (InputManager.isSPressed()) {
            for (PlayerShip playerShip : playerShips) {
                if (playerShip.getSpeed() <= speedLimit && playerShip.getSpeed() > 0) {
                    playerShip.setSpeed(playerShip.getSpeed() - half);
                }
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



        if (InputManager.isQPressed()) {
           zoomIn();
        }

        if (InputManager.isEPressed()) {
            zoomOut();
        }

    }

    private void zoomIn() {
        camera.zoom -= zoomSpeed * camera.zoom;
        camera.update();
    }

    // Method to zoom out
    private void zoomOut() {
        camera.zoom += zoomSpeed * camera.zoom;
        camera.update();
    }


    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        purpleShipTexture.dispose();
        greenLaserTexture.dispose();
        blueLaserTexture.dispose();
        redLaserTexture.dispose();
        greenShipTexture.dispose();
        explosionSound1.dispose();
        laserBlast2.dispose();
        laserBlast1.dispose();
        explosionTexture1.dispose();
        otherShipTexture.dispose();
        exhaustTexture.dispose();

    }


}