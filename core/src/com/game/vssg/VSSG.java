package com.game.vssg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL32;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private float zoomSpeed = 0.005f;
    public static boolean isPaused = false;

    private Texture purpleShipTexture;
    private Texture greenLaserTexture;
    private Texture explosionTexture1;
    private Texture otherShipTexture;
    private Texture blueLaserTexture;
    private Texture redLaserTexture;
    private Texture tealShipTexture;
    private Texture backgroundTexture;
    private Texture laser2Texture;
    private Texture explosionTexture2;
    private Sprite tealShipButton;
    private Sprite exhaust;
    private Sprite purpleShipButton;
    private Texture purpleShipButtonTexture;
    private Texture exhaustTexture;
    private Texture tealShipButtonTexture;
    private Texture purpleCorvetteTexture;
    private BitmapFont font;
    private CursorMode cursorMode;
    private OrthographicCamera camera;
    private Viewport viewport;
    private boolean shipSpawnTimeout = false;
    private int shipSpawnCounter = 0;
    private TextButton button;
    private TextButton button2;
    private TextButton button3;
    private TextButton quitButton;
    private TextButton quitButton2;
    private int clickTimeout = 0;
    private GameMode gameMode;
    private int score = 0;
    private ShapeRenderer healthBarShapeRenderer;
    private Iterator<PlayerShip> playerIter;
    private Iterator<CpuShip> cpuIter;
    private Iterator<Explosion> explosionIter;
    private Iterator<Laser> laserIter;
    private Iterator<CpuShip> copyIter;

    private enum CursorMode {
        MENU_MODE,
        SELECTION_MODE,
        PLAY_MODE
    }

private enum GameMode {
        ARCADE,
        SANDBOX
}

    private enum Screen {
        TITLE, MAIN_GAME, GAME_OVER;
    }
    Stage stage;

    VSSG.Screen currentScreen = VSSG.Screen.TITLE;


    @Override
    public void create() {

        cursorMode = CursorMode.MENU_MODE;
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();
        loadResources();
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        initObjects(viewportWidth, viewportHeight);
        initPlayerShip();

        purpleShipButton.setOrigin(camera.position.x + viewportWidth, camera.position.y + viewportHeight);
        purpleShipButton.setPosition((float) viewport.getScreenX() / 2, (float) viewport.getScreenY() / 2);

        font = new BitmapFont(); // Instantiate the BitmapFont
        font.getData().setScale((viewportHeight/111)*camera.zoom/2);

        Skin skin = new Skin();
        skin.add("default-font", font);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.fontColor = Color.GREEN;

        TextButton.TextButtonStyle buttonStyle2 = new TextButton.TextButtonStyle();
        buttonStyle2.font = skin.getFont("default-font");
        buttonStyle2.fontColor = Color.RED;

        button = new TextButton("QUIT TO DESKTOP", buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button.setStyle(buttonStyle2);
                Gdx.app.exit();
            }
        });

        quitButton2 = new TextButton("QUIT TO MENU", buttonStyle);
        quitButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                camera.zoom = DEFAULT_ZOOM;
                camera.update();
                currentScreen = VSSG.Screen.TITLE;
                button.setPosition(-524288, -524288);
                quitButton2.setPosition(-524288, -524288);

                for (PlayerShip playerShip : playerShips) {
                    playerShip.setInactive(playerShip);
                }

                for (CpuShip cpuShip : cpuShips) {
                    cpuShip.setInactive(cpuShip);
                }
                batch.begin();
                font.draw(batch, "         VSSG", (Gdx.graphics.getWidth()*0.25f) - 100, (Gdx.graphics.getHeight() * 0.75f)+512);
                batch.end();


            }
        });

        button2 = new TextButton("Arcade", buttonStyle);
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            System.out.println("Arcade");
                button.setPosition(-524288, -524288);
                quitButton2.setPosition(-524288, -524288);
                button2.setStyle(buttonStyle2);
                currentScreen = VSSG.Screen.MAIN_GAME;
                button2.setStyle(buttonStyle);
                button3.setPosition(-524288, -524288);
                button2.setPosition(-524288, -524288);
                quitButton.setPosition(-524288, -524288);
                Gdx.gl.glClearColor(0, 0, 0, 1);
                Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
                gameMode = GameMode.ARCADE;

                if (playerShips.isEmpty()){
                    initObjects(viewportWidth, viewportHeight);
                    initPlayerShip();
                }

                if(isPaused){
                    isPaused = false;
                }
            create();
            }
        });

        button3 = new TextButton("Sandbox", buttonStyle);
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                button3.setStyle(buttonStyle2);
                currentScreen = VSSG.Screen.MAIN_GAME;
                button3.setStyle(buttonStyle);
                button3.setPosition(-524288, -524288);
                button2.setPosition(-524288, -524288);
                quitButton.setPosition(-524288, -524288);
                ScreenUtils.clear(0, 0, 0, 1);
                Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
                gameMode = GameMode.SANDBOX;

             create();

                if(isPaused){
                    isPaused = false;
                }

                Vector2 mousePosition = new Vector2(WORLD_WIDTH/2, WORLD_HEIGHT/2);
                spawnShip(purpleShipTexture, mousePosition);
                makePlayerShip(cpuShips.first());

            }
        });

        quitButton = new TextButton("Quit", buttonStyle);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                quitButton.setStyle(buttonStyle2);
                Gdx.app.exit();            }
        });



        stage.addActor(button);
        stage.addActor(button2);
        stage.addActor(button3);
        stage.addActor(quitButton);
        stage.addActor(quitButton2);


    }

    @Override
    public void render() {
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        float deltaTime = Gdx.graphics.getDeltaTime();

        if(currentScreen == VSSG.Screen.TITLE && button != null && button2 != null){
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);

            button.setPosition(-524288, -524288);
            quitButton2.setPosition(-524288, -524288);
            stage.act(deltaTime);

            batch.begin();
            stage.draw();

            font.draw(batch, "          VSSG", (Gdx.graphics.getWidth()*0.25f) - 100, (Gdx.graphics.getHeight() * 0.75f)+512);
            Vector2 button2Position = new Vector2(camera.position.x, camera.position.y - 200 );
            button2.setPosition(button2Position.x - button2.getWidth()/2, button2Position.y);

            Vector2 button3Position = new Vector2(camera.position.x, camera.position.y - 400 );
            button3.setPosition(button3Position.x - button3.getWidth()/2, button3Position.y);

            Vector2 quitbuttonPosition = new Vector2(camera.position.x, camera.position.y - 600 );
            quitButton.setPosition(quitbuttonPosition.x - quitButton.getWidth()/2, quitbuttonPosition.y);
            batch.end();
        }
        else if(currentScreen == VSSG.Screen.MAIN_GAME) {
            ScreenUtils.clear(0, 0, 0, 1);
            Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);
            button.setPosition(-524288, -524288);
            quitButton2.setPosition(-524288, -524288);
        // System.out.println("x = "+camera.position.x+" y = "+camera.position.y);

        handleClickTimeout();
        handleInput();
        chooseMode();
        Vector2 buttonPosition = new Vector2(camera.position.x, camera.position.y );
            Vector2 quitButton2Position = new Vector2(camera.position.x, camera.position.y );

            if (cursorMode == CursorMode.MENU_MODE && button != null) {

                button.setPosition(buttonPosition.x - button.getWidth()/2, buttonPosition.y);
                quitButton2.setPosition(quitButton2Position.x - quitButton2.getWidth()/2, quitButton2Position.y + 300);

            }
        // Move button off screen until it is needed.
        else {
            if (button != null) {
                button.setPosition(-524288, -524288);
                quitButton2.setPosition(-524288, -524288);

            }
        }

         playerIter = playerShips.iterator();
         cpuIter = cpuShips.iterator();
         explosionIter = explosions.iterator();
         laserIter = lasers.iterator();
         copyIter = copiedSet.iterator();

        stage.act(deltaTime);
        checkIterators(playerIter, explosionIter, cpuIter, copyIter, laserIter, deltaTime);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, 0, 0, wrapDivisor, wrapDivisor);
       // font.draw(batch, , buttonPosition.x, buttonPosition.y);
            checkObjects(deltaTime);

            stage.draw();

            batch.end();

            for (PlayerShip playerShip : playerShips){
                healthBarShapeRenderer.setProjectionMatrix(camera.combined);

                healthBarShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                healthBarShapeRenderer.setColor(Color.RED);
                healthBarShapeRenderer.rect(camera.position.x+(viewport.getScreenWidth()/2)*(camera.zoom / 2), camera.position.y+((viewport.getScreenHeight()-(viewport.getScreenHeight()/20))*(camera.zoom / 2)), playerShip.getHp()*5*camera.zoom/2 ,50*camera.zoom/2);
                healthBarShapeRenderer.end();

                healthBarShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                healthBarShapeRenderer.setColor(Color.WHITE);
                healthBarShapeRenderer.rect(camera.position.x+(viewport.getScreenWidth()/2)*(camera.zoom / 2), camera.position.y+((viewport.getScreenHeight()-(viewport.getScreenHeight()/20))*(camera.zoom / 2)), 500*camera.zoom/2 ,50*camera.zoom/2);
                healthBarShapeRenderer.end();
            }
        }}


    PlayerShip makePlayerShip(CpuShip cpuShip) {
        if(playerShips.isEmpty()){
            System.out.println("makePlayerShip");
            cpuShip.setInactive(cpuShip);
            copiedSet.remove(cpuShip);
            cpuShip.getTargets().clear();
            return new PlayerShip(cpuShip.getTexture(), cpuShip.getExhaustTexture(), cpuShip.getPosition(), cpuShip.getSpeed(), Ship.ActionState.PLAYER_CONTROL,
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
    void loadResources() {

        purpleShipTexture = new Texture("purple_ship.png");
        otherShipTexture = new Texture("N1.png");
        tealShipTexture = new Texture("teal_ship.png");
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
        healthBarShapeRenderer = new ShapeRenderer();

    }

    void initPlayerShip() {

        Vector2 vector2 = new Vector2(worldWidthCentre, worldHeightCentre);
        Rectangle hitBox = new Rectangle();

        ObjectSet<Ship> targets = new ObjectSet<>();
        int playerActionCounter = 0;
         exhaust = new Sprite(exhaustTexture);
        PlayerShip playerShip = new PlayerShip(purpleShipTexture, exhaust, vector2, 40, Ship.ActionState.PLAYER_CONTROL, Ship.ActionState.PLAYER_CONTROL,
                hitBox, playerActionCounter, Ship.Faction.PURPLE, targets, 100, Ship.Type.FIGHTER, 90);

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
        cpuShips = new ObjectSet<>();
        copiedSet = new ObjectSet<>(cpuShips);
        playerShips = new ObjectSet<>();
        explosions = new ObjectSet<>();
        lasers = new ObjectSet<>();
        purpleShipButton = new Sprite(purpleShipButtonTexture);
        tealShipButton = new Sprite(tealShipButtonTexture);
        batch = new SpriteBatch();


    }

    void relinquishControl(PlayerShip playerShip) {

        CpuShip cpuShip = new CpuShip(playerShip.getTexture(), playerShip.getExhaustTexture(), playerShip.getPosition(), playerShip.getSpeed(),
                Ship.ActionState.IDLE, Ship.ActionState.IDLE, playerShip.getHitbox(), playerShip.getActionCounter(), playerShip.getFaction(),
                playerShip.getTargets(), playerShip.getHp(), playerShip.getType(), playerShip.getRotation());

        cpuShip.setRotation(playerShip.getRotation());
        cpuShip.setSpeed(playerShip.getSpeed());
        playerShip.setInactive(playerShip);
        playerShips.remove(playerShip);
        playerShip.increaseHp(100-playerShip.getHp());
        cpuShips.add(cpuShip);
        copiedSet.add(cpuShip);
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


    private void handleMenuInput() {
        if(InputManager.isSpacePressed() || InputManager.isLeftMousePressed()) {
            currentScreen = VSSG.Screen.MAIN_GAME;
        }
    }

    private void handleInput() {

        float cameraSpeed = camera.zoom * 2048;

        if (InputManager.isMiddlePressed()){
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Vector3 unprojected = camera.unproject(new Vector3(mouseX, mouseY, 0));

            Vector2 position = new Vector2(unprojected.x, unprojected.y);

            for (CpuShip cpuShip : cpuShips) {
                if (cpuShip.getHitbox().contains(position) && playerShips.isEmpty()) {
                     PlayerShip playerShip = makePlayerShip(cpuShip);
                    playerShip.setActionState(Ship.ActionState.PLAYER_CONTROL, Ship.ActionState.PLAYER_CONTROL);
                    playerShip.setRotation(cpuShip.getRotation());
                        playerShips.add(playerShip);
                }

            }
        }

        if (InputManager.isAPressed()) {
            for (Ship ship : playerShips) {
                ship.rotate(+1);
            }
        }

        if (InputManager.isDPressed()) {
            for (Ship ship : playerShips) {
                ship.rotate(-1);
            }
        }

        if (InputManager.isSpacePressed()) {
            Texture laserTexture = null;
            for (Ship ship : playerShips) {
                if (!ship.getLaserSpawnTimeout()) {
                    if(ship.getType() == Ship.Type.CORVETTE){
                      laserTexture = laser2Texture;
                    }
                    else if(ship.getType() == Ship.Type.FIGHTER){
                        if(ship.getFaction() == Ship.Faction.TEAL){
                            laserTexture = redLaserTexture;
                        }
                        else if(ship.getFaction() == Ship.Faction.PURPLE) {
                            laserTexture = greenLaserTexture;
                        }
                    }

                    Laser laser = ship.fireLaser(laserTexture, ship);
                    laser.setShip(ship);
                    lasers.add(laser);
                    laserBlast2.play(1f);
                    ship.setLaserSpawnTimeout(true);
                    ship.setLaserSpawnCounter(0);
                }
            }
        }

        if (InputManager.isRightMousePressed()) {
            if (gameMode == GameMode.SANDBOX) {
                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.input.getY();
                Vector2 position = new Vector2(mouseX, mouseY);
                spawnShip(purpleShipTexture, position);
            }
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
            if (gameMode == GameMode.SANDBOX){
            if(!isPaused) {
                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.input.getY();
                Vector2 position = new Vector2(mouseX, mouseY);
                spawnShip(tealShipTexture, position);
            }
            }
        }

        float speedLimit = 600f;
        if (InputManager.isWPressed()) {
            if (!isPaused) {
                for (PlayerShip playerShip : playerShips) {

                    if (playerShip.getSpeed() < speedLimit && playerShip.getSpeed() >= 0) {
                        playerShip.setSpeed(playerShip.getSpeed() + 2);
                    }
                }
            }
        }

        if (InputManager.isSPressed()) {
            if (!isPaused) {
                for (PlayerShip playerShip : playerShips) {
                    if (playerShip.getSpeed() <= speedLimit && playerShip.getSpeed() > 0) {
                        playerShip.setSpeed(playerShip.getSpeed() - 2);
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
            laser.update(deltaTime, WORLD_WIDTH, WORLD_HEIGHT);

            if (!laser.isActive()) {
                laserIter.remove();
            }
        }

    }

    void checkLaserCollision(Rectangle laserHitBox, Rectangle shipHitBox, Laser laser, Ship ship) {
        if (laserHitBox.overlaps(shipHitBox) && laser.getShip().getFaction() != ship.getFaction()) {
            Vector2 position = new Vector2(ship.getX()+ship.getWidth()/2, laser.getY() - ship.getHeight()/2);
            Explosion.explode(explosionTexture1, position, 512, explosions, explosionSound1, 16, 0.33f);
            ship.decreaseHp(16);
            laser.setInactive(laser);
            if (ship.getHp() <= 0) {
                ship.setInactive(ship);
                Explosion.explode(explosionTexture1, position, 256, explosions, explosionSound1, 128, 0.7f);
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
            laser.update(deltaTime, WORLD_WIDTH, WORLD_HEIGHT);
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


        for (PlayerShip playerShip : playerShips) {
            playerShip.update(deltaTime, playerShip, WORLD_WIDTH, WORLD_HEIGHT);
            playerShip.draw(batch);

         //   if (playerShip.getExhaustTimer() == 10) {
                playerShip.getExhaustTexture().draw(batch);
          //  }


            playerShip.handleActionState(playerShip, laser2Texture, greenLaserTexture, blueLaserTexture, redLaserTexture, lasers, laserBlast2);
            camera.position.x = playerShip.getX() + playerShip.getWidth() / 2;
            camera.position.y = playerShip.getY() + playerShip.getHeight() / 2;
        }


        for (CpuShip cpuShip : cpuShips) {
            cpuShip.update(deltaTime, cpuShip, WORLD_WIDTH, WORLD_HEIGHT);
            cpuShip.draw(batch);
            cpuShip.getExhaustTexture().draw(batch);

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
        if (shipTexture == tealShipTexture) {
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
            Sprite exhaust = new Sprite(exhaustTexture);
            CpuShip cpuShip = new CpuShip(shipTexture, exhaust, position, 200f, actionState, Ship.ActionState.IDLE, hitBox, actionCounter, faction, targets, 100, Ship.Type.FIGHTER, 90);
            cpuShip.setPosition(position.x, position.y);
            cpuShip.setScale(shipScale);
            cpuShip.setType(Ship.Type.FIGHTER);
            cpuShip.setOrigin(cpuShip.getOriginX(), cpuShip.getOriginY());

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
        tealShipButtonTexture.dispose();
        purpleShipButtonTexture.dispose();
        greenLaserTexture.dispose();
        blueLaserTexture.dispose();
        redLaserTexture.dispose();
        laser2Texture.dispose();
        purpleShipTexture.dispose();
        tealShipTexture.dispose();
        explosionSound1.dispose();
        laserBlast2.dispose();
        laserBlast1.dispose();
        explosionTexture1.dispose();
        otherShipTexture.dispose();
        exhaustTexture.dispose();
        purpleCorvetteTexture.dispose();
        explosionTexture2.dispose();
        healthBarShapeRenderer.dispose();
        stage.dispose();
        font.dispose();

    }

    void handleClickTimeout() {
        if (clickTimeout < 300) {

            clickTimeout++;
        } else {
            clickTimeout = 0;
        }
    }
}