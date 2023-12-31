package com.game.vssg;

import static com.badlogic.gdx.graphics.g3d.particles.ParticleShader.AlignMode.Screen;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.Iterator;

public class VSSG implements ApplicationListener {

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
    private final int wrapDivisor = (WORLD_WIDTH / 4096);
    private final float zoomSpeed = 0.004f;
    public static boolean isPaused = false;

    private Texture purpleShipTexture;
    private Texture greenLaserTexture;
    private Texture explosionTexture1;
    private Texture otherShipTexture;
    private Texture blueLaserTexture;
    private Texture redLaserTexture;
    private Texture greenShipTexture;
    private Texture backgroundTexture;
    private Texture exhaustTexture;
    private Texture laser2Texture;
    private Texture explosionTexture2;
    private Sprite tealShipButton;
    private Sprite purpleShipButton;
    private Texture purpleShipButtonTexture;
    private Texture tealShipButtonTexture;
    private Texture purpleCorvetteTexture;
    private BitmapFont font;

    private enum CursorMode {
        MENU_MODE,
        SELECTION_MODE,
        PLAY_MODE
    }

    private CursorMode cursorMode;
    private OrthographicCamera camera;
    private Viewport viewport;
    private boolean shipSpawnTimeout = false;
    private int shipSpawnCounter = 0;
    private TextButton button;
    private int clickTimeout = 0;

    Stage stage;


    ////////////////////////////////

    @Override
    public void create() {

        cursorMode = CursorMode.MENU_MODE;
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();
        loadResources();
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        initObjects(viewportWidth, viewportHeight);
        // Initial ship's details.
        initPlayerShip();

        purpleShipButton.setOrigin(camera.position.x + viewportWidth, camera.position.y + viewportHeight);
        purpleShipButton.setPosition((float) viewport.getScreenX() / 2, (float) viewport.getScreenY() / 2);

        font = new BitmapFont(); // Instantiate the BitmapFont
        font.getData().setScale((viewportHeight/111)*camera.zoom/2);

        Skin skin = new Skin();
        skin.add("default-font", font);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font"); // Set the font
        buttonStyle.fontColor = Color.GREEN; // Set the font color

        button = new TextButton("PAUSED: CLICK HERE TO QUIT", buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        stage.addActor(button);

    }

    @Override
    public void render() {

        // System.out.println("x = "+camera.position.x+" y = "+camera.position.y);
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        handleclickTimeout();
        float deltaTime = Gdx.graphics.getDeltaTime();
        handleInput();
        chooseMode();
        Vector2 buttonPosition = new Vector2(camera.position.x, camera.position.y );

        if (cursorMode == CursorMode.MENU_MODE && button != null) {
            button.setPosition(buttonPosition.x - button.getWidth()/2, buttonPosition.y);
        }
        // Move button off screen until it is needed.
        else {
            if (button != null) {
                button.setPosition(-524288, -524288);
            }
        }



        Iterator<PlayerShip> playerIter = playerShips.iterator();
        Iterator<CpuShip> cpuIter = cpuShips.iterator();
        Iterator<Explosion> explosionIter = explosions.iterator();
        Iterator<Laser> laserIter = lasers.iterator();
        Iterator<CpuShip> copyIter = copiedSet.iterator();

        stage.act(deltaTime);
        checkIterators(playerIter, explosionIter, cpuIter, copyIter, laserIter, deltaTime);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, 0, 0, wrapDivisor, wrapDivisor);
        //font.draw(batch, "Your Text Here", buttonPosition.x, buttonPosition.y);
        checkObjects(deltaTime);
        stage.draw();
        batch.end();
        System.out.println("CURSOR_MODE = " + cursorMode);

    }


    PlayerShip makePlayerShip(CpuShip cpuShip) {
        if(playerShips.isEmpty()){
            System.out.println("makePlayerShip");
            cpuShip.setInactive(cpuShip);
            cpuShip.getTargets().clear();
            return new PlayerShip(cpuShip.getTexture(), cpuShip.getPosition(), cpuShip.getSpeed(), Ship.ActionState.PLAYER_CONTROL,
                    cpuShip.getActionState(), cpuShip.getHitbox(), cpuShip.getActionCounter(), cpuShip.getFaction(), cpuShip.getTargets(),
                    cpuShip.getHp(), cpuShip.getType(), cpuShip.getRotation());
        }
        else    { System.out.println("null");
            return null;}
    }

    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    public void pause() {
    }

    public void resume() {
    }

    void initPlayerShip() {

        Vector2 vector2 = new Vector2(worldWidthCentre, worldHeightCentre);
        Rectangle hitBox = new Rectangle();
        ObjectSet<Ship> targets = new ObjectSet<>();
        int playerActionCounter = 0;
        PlayerShip playerShip = new PlayerShip(purpleCorvetteTexture, vector2, 40, Ship.ActionState.PLAYER_CONTROL, Ship.ActionState.PLAYER_CONTROL,
                hitBox, playerActionCounter, Ship.Faction.PURPLE, targets, 100, Ship.Type.CORVETTE, 90);

        playerShip.setScale(shipScale);
        playerShip.setRotation(0);
        playerShips.add(playerShip);
    }

    void initObjects(float viewportWidth, float viewportHeight) {

        camera = new OrthographicCamera();
        camera.setToOrtho(false, (float) WORLD_WIDTH / 2, (float) WORLD_HEIGHT / 2);
        camera.zoom = DEFAULT_ZOOM;
        viewport = new ExtendViewport(viewportWidth, viewportHeight, camera);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);
        // Prepare SpriteBatch and lists for keeping track of/accessing game objects.
        batch = new SpriteBatch();
        cpuShips = new ObjectSet<>();
        copiedSet = new ObjectSet<>(cpuShips);
        playerShips = new ObjectSet<>();
        explosions = new ObjectSet<>();
        lasers = new ObjectSet<>();
        purpleShipButton = new Sprite(purpleShipButtonTexture);
        tealShipButton = new Sprite(tealShipButtonTexture);


    }

    void loadResources() {

        purpleShipTexture = new Texture("N1.png");
        otherShipTexture = new Texture("N1.png");
        greenShipTexture = new Texture("teal_ship.png");
        greenLaserTexture = new Texture("laser_green.png");
        redLaserTexture = new Texture("laser_red.png");
        blueLaserTexture = new Texture("laser_blue.png");
        laser2Texture = new Texture("laser2.png");
        backgroundTexture = new Texture("background.png");
        explosionTexture1 = new Texture("explosion_orange.png");
        explosionTexture2 = new Texture("explosion2.png");
        exhaustTexture = new Texture("ship_exhaust.png");
        purpleShipButtonTexture = new Texture("purple_ship_button.png");
        tealShipButtonTexture = new Texture("teal_ship_button.png");
        purpleCorvetteTexture = new Texture("bigship.png");

        tealShipButton = new Sprite(tealShipButtonTexture);
        purpleShipButton = new Sprite(purpleShipButtonTexture);

        explosionSound1 = Gdx.audio.newSound(Gdx.files.internal("explosion.wav"));
        laserBlast1 = Gdx.audio.newSound(Gdx.files.internal("laserblast1.wav"));
        laserBlast2 = Gdx.audio.newSound(Gdx.files.internal("laser_blast2.wav"));


    }

    void relinquishControl(PlayerShip playerShip) {

        CpuShip cpuShip = new CpuShip(playerShip.getTexture(), playerShip.getPosition(), playerShip.getSpeed(),
                Ship.ActionState.IDLE, Ship.ActionState.IDLE, playerShip.getHitbox(), playerShip.getActionCounter(), playerShip.getFaction(),
                playerShip.getTargets(), playerShip.getHp(), playerShip.getType(), playerShip.getRotation());

        cpuShip.setRotation(playerShip.getRotation());
        cpuShip.setSpeed(playerShip.getSpeed());
        playerShip.setInactive(playerShip);
        playerShips.remove(playerShip);
        cpuShips.add(cpuShip);
        copiedSet.add(cpuShip);
        //System.out.println("CURSOR_MODE = " + cursorMode);

    }

    void chooseMode() {

        if (isPaused) {
            cursorMode = CursorMode.MENU_MODE;
        } else if (playerShips.notEmpty() && !isPaused) {
            cursorMode = CursorMode.PLAY_MODE;
        } else if (playerShips.isEmpty() && !isPaused) {
            cursorMode = CursorMode.SELECTION_MODE;
        }
    }


    private void handleInput() {

        float cameraSpeed = camera.zoom * 2048;

        if (InputManager.isMiddlePressed()){
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Vector3 unprojected = camera.unproject(new Vector3(mouseX, mouseY, 0));

            Vector2 position = new Vector2(unprojected.x, unprojected.y);

             Explosion.explode(explosionTexture1, position, 60, explosions, explosionSound1, 100, 0.7f);
            for (CpuShip cpuShip : cpuShips) {
                if (cpuShip.getHitbox().contains(position)) {

                    if (playerShips.isEmpty()) {
                        if(playerShips.isEmpty()){
                            System.out.println("makePlayerShip");
                            cpuShip.setInactive(cpuShip);
                            cpuShip.getTargets().clear();
                            PlayerShip playerShip = new PlayerShip(cpuShip.getTexture(), cpuShip.getPosition(), cpuShip.getSpeed(), Ship.ActionState.PLAYER_CONTROL,
                                    cpuShip.getActionState(), cpuShip.getHitbox(), cpuShip.getActionCounter(), cpuShip.getFaction(), cpuShip.getTargets(),
                                    cpuShip.getHp(), cpuShip.getType(), cpuShip.getRotation());
                            playerShip.setActionState(Ship.ActionState.PLAYER_CONTROL, Ship.ActionState.PLAYER_CONTROL);
                            playerShips.add(playerShip);
                        }



                    }
                }

            }
        }


        if (InputManager.isAPressed()) {
            for (Ship ship : playerShips) {
                ship.rotate(+0.7f);
            }
        }

        if (InputManager.isDPressed()) {
            for (Ship ship : playerShips) {
                ship.rotate(-0.7f);
            }
        }

        if (InputManager.isSpacePressed()) {
            Texture laserTexture = null;
            for (Ship ship : playerShips) {
                if (!ship.getLaserSpawnTimeout()) {
                    if(ship.getType()== Ship.Type.CORVETTE){
                      laserTexture = laser2Texture;
                    }
                    else if(ship.getType() == Ship.Type.FIGHTER){
                        if(ship.getFaction()== Ship.Faction.TEAL){
                            laserTexture = redLaserTexture;
                        }
                        else if(ship.getFaction() == Ship.Faction.PURPLE) {
                            laserTexture = greenLaserTexture;
                        }

                    }

                    Laser laser = ship.fireLaser(laserTexture, ship);
                    laser.setShip(ship);
                    lasers.add(laser);
                    laserBlast1.play(1f);
                    ship.setLaserSpawnTimeout(true);
                    ship.setLaserSpawnCounter(0);
                }
            }
        }

        if (InputManager.isRightMousePressed()) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Vector2 position = new Vector2(mouseX, mouseY);
            spawnShip(purpleShipTexture, position);
        }


        if (shipSpawnTimeout) {
            if (shipSpawnCounter >= 30) {
                shipSpawnTimeout = false;
            } else {
                shipSpawnCounter++;
            }
        }

// For player ship only during runtime. CpuShip laser timing is handled differently.
        if (!playerShips.isEmpty()) {
            if (playerShips.first().getLaserSpawnTimeout()) {
                if (playerShips.first().getLaserSpawnCounter() >= 50) {

                    playerShips.first().setLaserSpawnTimeout(false);
                } else {
                    playerShips.first().setLaserSpawnCounter(playerShips.first().getLaserSpawnCounter() + 1);
                }
            }
        }

        if (InputManager.isLeftMousePressed()) {
            if(!isPaused) {
                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.input.getY();
                Vector2 position = new Vector2(mouseX, mouseY);
                spawnShip(greenShipTexture, position);


            }
        }

        float speedLimit = 700f;
        if (InputManager.isWPressed()) {
            if (!isPaused) {
                for (PlayerShip playerShip : playerShips) {

                    if (playerShip.getSpeed() < speedLimit && playerShip.getSpeed() >= 0) {
                        playerShip.setSpeed(playerShip.getSpeed() + 1);
                    }
                }
            }
        }

        if (InputManager.isSPressed()) {
            if (!isPaused) {
                for (PlayerShip playerShip : playerShips) {
                    if (playerShip.getSpeed() <= speedLimit && playerShip.getSpeed() > 0) {
                        playerShip.setSpeed(playerShip.getSpeed() - 1);
                    }
                }
            }
        }

        if (InputManager.isEscPressed()) {
            if (clickTimeout > 100) {
                pauseGame();
                clickTimeout = 0;
            } else clickTimeout++;
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
           System.out.println(camera.zoom);
            if (camera.zoom > 0.2f) {
                zoomIn();
            }
        }

        if (InputManager.isEPressed()) {
            if (camera.zoom < 23) {
                zoomOut();
            }
        }


        if (InputManager.isCPressed()) {
            if (!playerShips.isEmpty()) {
                relinquishControl(playerShips.first());
            }

        }
    }

    void pauseGame() {
        isPaused = !isPaused;
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

            if (playerShip.isActive()) {
                playerIter.remove();
            }
        }

        while (cpuIter.hasNext()) {

            CpuShip cpuShip = cpuIter.next();
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);

            if (cpuShip.isActive()) {
                cpuIter.remove();
            }
        }

        while (copyIter.hasNext()) {

            CpuShip cpuShip = copyIter.next();
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);

            if (cpuShip.isActive()) {
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
            laser.update(deltaTime, WORLD_WIDTH, WORLD_HEIGHT, laser.getShip());

            if (!laser.isActive()) {
                laserIter.remove();
            }
        }

    }

    void checkLaserCollision(Rectangle laserHitBox, Rectangle shipHitBox, Laser laser, Ship ship) {
        if (laserHitBox.overlaps(shipHitBox) && laser.getShip().getFaction() != ship.getFaction()) {
            Vector2 position = new Vector2(ship.getX()+ship.getWidth()/2, laser.getY() - ship.getHeight()/2);
            Explosion.explode(explosionTexture1, position, 300, explosions, explosionSound1, 20, 0.33f);
            ship.decreaseHp(10);
            laser.setInactive(laser);
            if (ship.getHp() <= 0) {
                ship.setInactive(ship);
                Explosion.explode(explosionTexture1, position, 300, explosions, explosionSound1, 100, 0.7f);
            }
        }
    }

    public void checkObjects(float deltaTime) {
        for (Laser laser : lasers) {

            // Determine laser's scale based on its texture.
            if(laser.getTexture() == laser2Texture) {
                laser.setScale(0.8f);
            }
            else {laser.setScale(3);}
            //

            laser.draw(batch);
            Rectangle laserHitBox = laser.getHitbox();
            laser.update(deltaTime, WORLD_WIDTH, WORLD_HEIGHT, laser.getShip());
            laser.updateHitBox(laser);

            for (CpuShip cpuShip : cpuShips) {
                Rectangle shipHitBox = cpuShip.getHitbox();
                checkLaserCollision(laserHitBox, shipHitBox, laser, cpuShip);
            }

            for (PlayerShip playerShip : playerShips) {
                Rectangle shipHitBox = playerShip.getHitbox();
                checkLaserCollision(laserHitBox, shipHitBox, laser, playerShip);
            }
        }

//////////////////

        for (PlayerShip playerShip : playerShips) {
            playerShip.draw(batch);
            playerShip.update(deltaTime, playerShip, WORLD_WIDTH, WORLD_HEIGHT);
            playerShip.handleActionState(playerShip, laser2Texture, greenLaserTexture, blueLaserTexture, redLaserTexture, lasers, laserBlast2);
            camera.position.x = playerShip.getX() + playerShip.getWidth() / 2;
            camera.position.y = playerShip.getY() + playerShip.getHeight() / 2;
        }
        ///////////////////

        for (CpuShip cpuShip : cpuShips) {
            cpuShip.draw(batch);
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);
            cpuShip.handleActionState(cpuShip, laser2Texture, greenLaserTexture, blueLaserTexture, redLaserTexture, lasers, laserBlast2);
            for (Ship target : cpuShip.getTargets()) {

                if (target != null) {
                    if (target.isActive()) {
                        cpuShip.getTargets().remove(target);
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
            explosion.update(deltaTime);

            explosion.draw(batch);
        }
    }


    CpuShip.Faction assignFactionByTexture(Texture shipTexture) {
        CpuShip.Faction faction = null;
        if (shipTexture == greenShipTexture) {
            faction = CpuShip.Faction.TEAL;
        } else if (shipTexture == purpleShipTexture) {
            faction = CpuShip.Faction.PURPLE;
        } else if (shipTexture == otherShipTexture) {
            faction = CpuShip.Faction.PURPLE;
        }
        return faction;
    }

    void spawnShip(Texture shipTexture, Vector2 mouseClickPosition) {
        if (!shipSpawnTimeout) {

            Vector3 unprojected = camera.unproject(new Vector3(mouseClickPosition.x, mouseClickPosition.y, 0));
            Vector2 position = new Vector2(unprojected.x, unprojected.y);

            CpuShip.ActionState actionState = Ship.ActionState.IDLE;
            Rectangle hitBox = new Rectangle();
            int actionCounter = 0;
            ObjectSet<Ship> targets = new ObjectSet<>();
            CpuShip.Faction faction = assignFactionByTexture(shipTexture);
            CpuShip cpuShip = new CpuShip(shipTexture, position, 400f, actionState, Ship.ActionState.IDLE, hitBox, actionCounter, faction, targets, 100, Ship.Type.FIGHTER, 90);
            cpuShip.setPosition(position.x, position.y);
            cpuShip.setScale(shipScale);
            cpuShip.setType(Ship.Type.FIGHTER);
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
        laser2Texture.dispose();
        greenShipTexture.dispose();
        explosionSound1.dispose();
        laserBlast2.dispose();
        laserBlast1.dispose();
        explosionTexture1.dispose();
        otherShipTexture.dispose();
        exhaustTexture.dispose();
        purpleCorvetteTexture.dispose();
        explosionTexture2.dispose();
        stage.dispose();
        font.dispose();

    }




    void handleclickTimeout() {
        if (clickTimeout < 300) {

            clickTimeout++;
        } else {
            clickTimeout = 0;
        }
    }

}