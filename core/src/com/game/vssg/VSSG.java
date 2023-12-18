package com.game.vssg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Iterator;
import java.util.UUID;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class VSSG implements ApplicationListener {

    // DEBUGGING //
    public static boolean showHitBoxes = false;
    public static boolean mute = false;
    ///////////////

    public static int WORLD_CONSTANT = 32768;
    public static int WORLD_WIDTH = WORLD_CONSTANT;
    public static long WORLD_HEIGHT = WORLD_CONSTANT;
    public static float shipScale = 1f;
    public static float DEFAULT_ZOOM = 2;

    private ObjectSet<PlayerShip> playerShips;
    private ObjectSet<CpuShip> cpuShips;
    private ObjectSet<CpuShip> copiedSet;
    private ObjectSet<Explosion> explosions;
    private ObjectSet<Laser> lasers;
    private SpriteBatch batch;
    private Sound explosionSound1;
    private Sound laserBlast1;
    private Sound laserBlast2;
    private final float worldWidthCentre = (float) WORLD_WIDTH / 2;
    private final float worldHeightCentre = (float) WORLD_HEIGHT / 2;
    private final int wrapDivisor =  (WORLD_WIDTH / 4096);
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
    private Sprite tealShipButton;
    private Sprite purpleShipButton;
    private Texture purpleShipButtonTexture;
    private Texture tealShipButtonTexture;
    private Label label;
    private static enum CursorMode {
        SELECTION_MODE,
        PLAY_MODE
    }

    private CursorMode cursorMode;
    private OrthographicCamera camera;
    private Viewport viewport;
    private boolean shipSpawnTimeout = false;
    private int shipSpawnCounter = 0;
    private boolean laserSpawnTimeout = false;
    public static boolean playerActive = false;
    private int laserSpawnCounter = 0;
    Stage stage;

    ////////////////////////////////

    @Override
    public void create() {
        cursorMode=CursorMode.PLAY_MODE;
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();
        // Get number of processors for future multithreading purposes.
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("Get number of processor cores: " + processors);

        // Load assets.
        purpleShipTexture = new Texture("purple_ship.png");
        otherShipTexture = new Texture("N1.png");
        greenShipTexture = new Texture("teal_ship.png");
        greenLaserTexture = new Texture("laser_green.png");
        redLaserTexture = new Texture("laser_red.png");
        blueLaserTexture = new Texture("laser_blue.png");
        backgroundTexture = new Texture("background.png");
        explosionTexture1 = new Texture("explosion_orange.png");
        exhaustTexture = new Texture("ship_exhaust.png");
        purpleShipButtonTexture = new Texture("purple_ship_button.png");
        tealShipButtonTexture = new Texture("teal_ship_button.png");

        tealShipButton = new Sprite(tealShipButtonTexture);
        purpleShipButton = new Sprite(purpleShipButtonTexture);

        explosionSound1 = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        laserBlast2 = Gdx.audio.newSound(Gdx.files.internal("laser_blast2.wav"));

        // Covers the playable map in the specified texture.  To be modularized for customization.
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        // Setup camera, viewport, controls input.
        camera = new OrthographicCamera();
        camera.setToOrtho(false, (float) WORLD_WIDTH /2, (float) WORLD_HEIGHT /2);
        camera.zoom = DEFAULT_ZOOM;

        viewport = new ExtendViewport(viewportWidth, viewportHeight, camera);
         stage = new Stage(viewport);
        //viewport.apply();

        Gdx.input.setInputProcessor(stage);

        // Prepare SpriteBatch and lists for keeping track of/accessing game objects.
        batch = new SpriteBatch();
        cpuShips = new ObjectSet<>();
        copiedSet = new ObjectSet<>(cpuShips);
        playerShips = new ObjectSet<>();
        explosions = new ObjectSet<>();
        lasers = new ObjectSet<>();

        // Initial ship's details.
        Vector2 vector2 = new Vector2(worldWidthCentre, worldHeightCentre);
        Rectangle hitBox = new Rectangle();
        ObjectSet<Ship> targets = new ObjectSet<>();
        int playerActionCounter = 0;
        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        System.out.println("New ship UUID is: " + uuidAsString);

        PlayerShip playerShip = new PlayerShip(uuid, purpleShipTexture, vector2, 40, Ship.ActionState.PLAYER_CONTROL, Ship.ActionState.PLAYER_CONTROL, hitBox, playerActionCounter, Ship.Faction.PURPLE, targets);
        playerShip.setScale(shipScale);
        playerShip.setRotation(0);
        playerShips.add(playerShip);

        purpleShipButton = new Sprite(purpleShipButtonTexture);
        tealShipButton = new Sprite(tealShipButtonTexture);

        purpleShipButton.setOrigin(camera.position.x + viewportWidth, camera.position.y+viewportHeight);
        purpleShipButton.setPosition((float) viewport.getScreenX() /2, (float) viewport.getScreenY() /2);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 120;
        parameter.color = Color.RED;
        BitmapFont font = generator.generateFont(parameter); // font size 12 pixels
        generator.dispose(); // don't forget to dispose to avoid memory leaks!
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;

        label = new Label("*** IN DEVELOPMENT ***", labelStyle);
        label.setSize(500, 500);
        label.setPosition(-100, 500);

// Optionally, set label position, size, etc.
        stage.addActor(label);

    }
private boolean paused = false;
    @Override
    public void render() {

        // System.out.println("x = "+camera.position.x+" y = "+camera.position.y);
        float deltaTime = Gdx.graphics.getDeltaTime();

        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);
        label.setPosition(-100, -500);

        if (!playerShips.isEmpty()) {
            cursorMode=CursorMode.PLAY_MODE;
        }
        else {
            cursorMode = CursorMode.SELECTION_MODE;
        }

        batch.setProjectionMatrix(camera.combined);
        camera.update();
        handleInput();

       // System.out.println("x = "+purpleShipButton.getX()+" y = "+purpleShipButton.getY()+" Zoom = "+camera.zoom+" camera.position = "+ camera.position.x);
        Iterator<PlayerShip> playerIter = playerShips.iterator();
        Iterator<CpuShip> cpuIter = cpuShips.iterator();
        Iterator<Explosion> explosionIter = explosions.iterator();
        Iterator<Laser> laserIter = lasers.iterator();
        Iterator<CpuShip> copyIter = copiedSet.iterator();

        stage.act(deltaTime);
        stage.draw();
        checkIterators(playerIter, explosionIter, cpuIter, copyIter, laserIter, deltaTime);
        scaleButtons();

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, 0, 0, wrapDivisor, wrapDivisor);
        stage.draw();
        checkObjects(deltaTime);
        batch.end();

    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, false);
    }

    public void pause() {
    }

    public void resume() {
    }

void scaleButtons(){

    purpleShipButton.setScale(camera.zoom/2);
    tealShipButton.setScale(camera.zoom/2);

}


void relinquishControl(PlayerShip playerShip){

        CpuShip cpuShip = new CpuShip(playerShip.getUuid(), playerShip.getTexture(), playerShip.getPosition(), playerShip.getSpeed(), Ship.ActionState.IDLE, Ship.ActionState.IDLE, playerShip.getHitbox(), playerShip.getActionCounter(), playerShip.getFaction(), playerShip.getTargets());
        playerShip.setInactive(playerShip);
        playerShips.remove(playerShip);
        cpuShips.add(cpuShip);
        copiedSet.add(cpuShip);
        System.out.println("CURSOR_MODE = "+cursorMode);

}




    private void handleInput() {

        float cameraSpeed = camera.zoom * 2048;

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

                    Laser laser = ship.fireLaser(blueLaserTexture, ship);
                    laser.setShip(ship);
                    lasers.add(laser);

                    laserBlast2.play(2.0f);
                    laserSpawnTimeout = true;
                    laserSpawnCounter = 0;


                }
            }
        }




        if (InputManager.isRightMousePressed()) {

            if (!playerShips.isEmpty()) {
                relinquishControl(playerShips.first());
            }
        }

        if (shipSpawnTimeout) {
            if (shipSpawnCounter >= 90) {

                shipSpawnTimeout = false;
            } else {
                shipSpawnCounter++;
            }
        }
// For player ship only during runtime. CpuShip laser timing is handled differently.
        if (laserSpawnTimeout) {
            if (laserSpawnCounter >= 200) {

                laserSpawnTimeout = false;
            } else {
                laserSpawnCounter++;
            }
        }

        int actionCounter = 0;

        if (InputManager.isLeftMousePressed()) {

            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();

            System.out.println("Mouse coordinates: ("+mouseX+", "+mouseY+")" );

            if ((mouseX <= 64 && mouseX >= 0) && (mouseY <= 1400 && mouseY >= 1340)) {
                System.out.println("Purple button CLICKED" );
            }

            if ((mouseX <= 128 && mouseX > 64) && (mouseY <= 1400 && mouseY >= 1340)) {
                System.out.println("Teal button CLICKED" );
            }

            if (!shipSpawnTimeout) {
                Vector2 position = new Vector2(camera.position.x, camera.position.y);
                CpuShip.ActionState actionState = Ship.ActionState.IDLE;
                Rectangle hitBox = new Rectangle();
                ObjectSet<Ship> targets = new ObjectSet<>();
                UUID uuid = UUID.randomUUID();
                String uuidAsString = uuid.toString();
                System.out.println("New ship UUID is: " + uuidAsString);

                CpuShip cpuShip = new CpuShip(uuid, greenShipTexture, position, 400f, actionState, Ship.ActionState.IDLE, hitBox, actionCounter, Ship.Faction.TEAL, targets);
                cpuShip.setPosition(position.x, position.y);
                cpuShip.setScale(shipScale);
                // The copy is for recursive iteration of the CpuShip for-loops during their auto-targeting routine.
                cpuShips.add(cpuShip);
                copiedSet.add(cpuShip);

                shipSpawnTimeout = true;
                shipSpawnCounter = 0;
            }




        }

        float speedLimit = 600f;
        // Speed up.
        if (InputManager.isWPressed()) {

            for (PlayerShip playerShip : playerShips) {

                if (playerShip.getSpeed() < speedLimit && playerShip.getSpeed() >= 0) {
                    playerShip.setSpeed(playerShip.getSpeed() + 1);
                }

            }
        }



        // Slow down.
        if (InputManager.isSPressed()) {
            for (PlayerShip playerShip : playerShips) {
                if (playerShip.getSpeed() <= speedLimit && playerShip.getSpeed() > 0) {
                    playerShip.setSpeed(playerShip.getSpeed() - 1);
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

           spawnShip(purpleShipTexture);

        }

        if (InputManager.isQPressed()) {
           if (cursorMode == CursorMode.SELECTION_MODE) {
                zoomIn();
            }
        }

        if (InputManager.isEPressed()) {
            if (cursorMode == CursorMode.SELECTION_MODE) {
                zoomOut();
            }
        }

    }

    /////////////////////////////////
    private void zoomIn() {
        camera.zoom -= zoomSpeed * camera.zoom;
        camera.update();
    }

    // Method to zoom out
    private void zoomOut() {
        camera.zoom += zoomSpeed * camera.zoom;
        camera.update();
    }

    /////////////////////////////////
    public void checkIterators(Iterator<PlayerShip> playerIter, Iterator<Explosion> explosionIter, Iterator<CpuShip> cpuIter, Iterator<CpuShip> copyIter, Iterator<Laser> laserIter, float deltaTime) {
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

        while (copyIter.hasNext()) {

            CpuShip cpuShip = copyIter.next();
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);

            if (!cpuShip.isActive()) {
                copyIter.remove();

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

    }


    public void checkObjects(float deltaTime) {
        for (Laser laser : lasers) {
            laser.setScale(3);
            laser.draw(batch);
            Rectangle laserHitBox = laser.getHitbox();
            laser.update(deltaTime, WORLD_WIDTH, WORLD_HEIGHT, laser.getDespawnCounter(), laser.getShip());
            laser.updateHitBox(laser);

            for (CpuShip cpuShip : cpuShips) {
                Rectangle shipHitBox = cpuShip.getHitbox();
                if (showHitBoxes) {
                    cpuShip.getShapeRenderer().setProjectionMatrix(camera.combined);
                    cpuShip.drawBoundingBox();
                }
                if (laserHitBox.overlaps(shipHitBox) && laser.getShip().getFaction() != cpuShip.getFaction()) {
                    Vector2 position = new Vector2(laser.getX(), laser.getY() - 64);
                    Explosion.explode(camera, explosionTexture1, 0.7f, position, 30, explosions, explosionSound1, 300, 10);
                    cpuShip.setInactive(cpuShip);
                    laser.setInactive(laser);


                }
                 cpuShip.setActionState(Ship.ActionState.ATTACK, cpuShip.getActionState());

            }


            for (PlayerShip playerShip : playerShips) {
                Rectangle shipHitBox = playerShip.getHitbox();
                if (showHitBoxes) {
                    playerShip.getShapeRenderer().setProjectionMatrix(camera.combined);
                    playerShip.drawBoundingBox();
                }
                if (laserHitBox.overlaps(shipHitBox) && laser.getShip().getFaction() != playerShip.getFaction()) {
                    Vector2 position = new Vector2(laser.getX(), laser.getY() - 64);
                    Explosion.explode(camera, explosionTexture1, 0.7f, position, 30, explosions, explosionSound1, 300, 10);
                    playerShip.setInactive(playerShip);
                    laser.setInactive(laser);


                }
            }

        }


        for (PlayerShip playerShip : playerShips) {
            playerShip.draw(batch);
            playerShip.update(deltaTime, playerShip, WORLD_WIDTH, WORLD_HEIGHT);
            playerShip.handleActionState(playerShip, greenLaserTexture, blueLaserTexture, redLaserTexture, lasers, laserBlast2);
            camera.position.x = playerShip.getX()+playerShip.getWidth()/2;
            camera.position.y = playerShip.getY()+playerShip.getHeight()/2;
        }

        for (CpuShip cpuShip : cpuShips) {
            cpuShip.draw(batch);
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);
            cpuShip.handleActionState(cpuShip, greenLaserTexture, blueLaserTexture, redLaserTexture, lasers, laserBlast2);

            for (Ship target : cpuShip.getTargets()) {
                if (target != null) {
                    if (!target.isActive()) {
                        cpuShip.getTargets().remove(target);
                        System.out.println("TARGET "+target.getUuid()+" REMOVED");


                    }
                }
            }

            for (PlayerShip playerShip : playerShips) {
                cpuShip.detectTargets(playerShip, cpuShip.getTargets());
            }

            for (CpuShip cpuShip2 : copiedSet) {
                cpuShip.detectTargets(cpuShip2, cpuShip.getTargets());
            }

        }


        for (Explosion explosion : explosions) {
            explosion.draw(batch);
            explosion.update(deltaTime);
        }

    }



    void spawnShip(Texture shipTexture){
        if (!shipSpawnTimeout) {
            Vector2 position = new Vector2(camera.position.x, camera.position.y);
            CpuShip.ActionState actionState = Ship.ActionState.IDLE;
            Rectangle hitBox = new Rectangle();
            int actionCounter = 0;
            UUID uuid = UUID.randomUUID();
            String uuidAsString = uuid.toString();
            System.out.println("New ship UUID is: " + uuidAsString);
            ObjectSet<Ship> targets = new ObjectSet<>();
            CpuShip cpuShip = new CpuShip(uuid, shipTexture, position, 400f, actionState, Ship.ActionState.IDLE, hitBox, actionCounter, Ship.Faction.PURPLE, targets);
            cpuShip.setPosition(position.x, position.y);
            cpuShip.setScale(shipScale);
            cpuShips.add(cpuShip);
            copiedSet.add(cpuShip);

            shipSpawnCounter = 0;
            shipSpawnTimeout = true;
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        backgroundTexture.dispose();
        purpleShipTexture.dispose();
        tealShipButtonTexture.dispose();
        purpleShipButtonTexture.dispose();
        greenLaserTexture.dispose();
        blueLaserTexture.dispose();
        redLaserTexture.dispose();
        greenShipTexture.dispose();
        explosionSound1.dispose();
        laserBlast2.dispose();
        //   laserBlast1.dispose();
        explosionTexture1.dispose();
        otherShipTexture.dispose();
        exhaustTexture.dispose();
        stage.dispose();

    }


}