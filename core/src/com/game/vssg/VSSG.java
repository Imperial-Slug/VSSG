package com.game.vssg;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
    Stage stage;

    private ObjectSet<PlayerShip> playerShips;
    private ObjectSet<CpuShip> cpuShips;
    private ObjectSet<CpuShip> cpuShipsCopy;
    private ObjectSet<Explosion> explosions;
    private ObjectSet<Laser> lasers;
    private SpriteBatch batch;
    VSSG.Screen currentScreen = VSSG.Screen.TITLE;
    private Sound explosionSound1;
    private Sound laserBlast1;
    private Sound laserBlast2;
    private final float worldWidthCentre = (float) WORLD_WIDTH / 2;
    private final float worldHeightCentre = (float) WORLD_HEIGHT / 2;
    // Determines how the
    private final int wrapDivisor = (WORLD_WIDTH / 4096);
    private float zoomSpeed = 0.005f;
    public static boolean isPaused = false;
    private int waveNumber = 1;

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
    private TextButton buttonQuitToDesktop;
    private TextButton button2;
    private TextButton button3;
    private TextButton quitButton;
    private TextButton quitButton2;
    private TextButton scoreDisplay;

    private int clickTimeout = 0;
    private GameMode gameMode;
    private int score = 0;
    private ShapeRenderer healthBarShapeRenderer;
    private Iterator<PlayerShip> playerIter;
    private Iterator<CpuShip> cpuIter;
    private Iterator<Explosion> explosionIter;
    private Iterator<Laser> laserIter;


    // This copyIter is for the copy of the CpuShip ObjectSet list so it can be iterated through recursively.
    // If there was only one copy, some of the nested for loops in functions in this program would not be possible.
    private Iterator<CpuShip> copyIter;

    // An enumeration that determines what action is taken when the mouse is clicked based on which state it is in.
    private enum CursorMode {
        MENU_MODE,
        SELECTION_MODE,
        PLAYER_MODE
    }

    // Enumeration determining which gamemode the game is in.
    private enum GameMode {
        ARCADE,
        SANDBOX
    }

    // An enumeration determining which screen is supposed to be shown.
    private enum Screen {
        TITLE, MAIN_GAME, GAME_OVER;
    }

    // Make sure all the ships are de-spawned
    void flushShips() {

        for (PlayerShip playerShip : playerShips) {
            playerShip.setInactive(playerShip);
        }
        for (CpuShip cpuShip : cpuShips) {
            cpuShip.setInactive(cpuShip);
        }

    }



    void prepareHTML5Controls() {

        Gdx.input.setCatchKey(Input.Keys.SPACE, true);
        Gdx.input.setCatchKey(Input.Keys.A, true);
        Gdx.input.setCatchKey(Input.Keys.S, true);
        Gdx.input.setCatchKey(Input.Keys.W, true);
        Gdx.input.setCatchKey(Input.Keys.D, true);
        Gdx.input.setCatchKey(Input.Keys.C, true);
        Gdx.input.setCatchKey(Input.Keys.Q, true);
        Gdx.input.setCatchKey(Input.Keys.E, true);
        Gdx.input.setCatchKey(Input.Keys.F, true);
        Gdx.input.setCatchKey(Input.Keys.T, true);
        Gdx.input.setCatchKey(Input.Keys.Z, true);
        Gdx.input.setCatchKey(Input.Keys.ESCAPE, true);


    }


    // This is a built-in and overridden method of LibGDX that runs at the beginning of the game by default to initialize key variables.
    @Override
    public void create() {
        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        prepareHTML5Controls();

        cursorMode = CursorMode.MENU_MODE;
        float viewportWidth = Gdx.graphics.getWidth();
        float viewportHeight = Gdx.graphics.getHeight();
        loadResources();
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        initObjects(viewportWidth, viewportHeight);
        initPlayerShip();

        camera.zoom = DEFAULT_ZOOM;
        camera.position.x += viewport.getScreenWidth()*2;
        camera.position.y += viewport.getScreenHeight()*2;

        camera.update();
        font = new BitmapFont(); // Instantiate the BitmapFont
        font.getData().setScale((viewportHeight / 111) * camera.zoom / 2);

        Skin skin = new Skin();
        skin.add("default-font", font);

        // Create green button style
        TextButton.TextButtonStyle buttonStyle = createGreenTextButton(skin);

        // Create red button style.
        TextButton.TextButtonStyle buttonStyle2 = createRedButtonStyle(skin);
        scoreDisplay = new TextButton("SCORE: "+score, buttonStyle);

        buttonQuitToDesktop = new TextButton("QUIT TO DESKTOP", buttonStyle);
        buttonQuitToDesktop.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                buttonQuitToDesktop.setStyle(buttonStyle2);
                Gdx.app.exit();
            }
        });

        quitButton2 = createQuitButton2(buttonStyle, buttonStyle2);

        button2 = new TextButton("Arcade Mode", buttonStyle);

        // When ARCADE_MODE is chosen from the main screen.
        button2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Arcade mode initiated.");

                buttonQuitToDesktop.setVisible(false);
                quitButton2.setVisible(false);
                button3.setVisible(false);
                button2.setVisible(false);
                quitButton.setVisible(false);
                scoreDisplay.setVisible(true);
                scoreDisplay.setPosition(camera.position.x + ((float) viewport.getScreenWidth() / 2) * (camera.zoom / 2), camera.position.y + ((viewport.getScreenHeight() - ((float) viewport.getScreenHeight() / 20)) * (camera.zoom / 2)));

                currentScreen = VSSG.Screen.MAIN_GAME;

                gameMode = GameMode.ARCADE;

                if (playerShips.isEmpty()) {
                    initObjects(viewportWidth, viewportHeight);
                    initPlayerShip();
                }

                if (isPaused) {
                    isPaused = false;
                }
                create();
            }
        });

        button3 = new TextButton("Sandbox", buttonStyle);
        button3.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                currentScreen = VSSG.Screen.MAIN_GAME;
                button2 = new TextButton("Arcade Mode", buttonStyle);
                gameMode = GameMode.SANDBOX;

                // Starts the game by calling libGDX's overridden create() method.
                create();

                // Avoid getting stuck on pause for any reason at the beginning.
                if (isPaused) {
                    isPaused = false;
                }

                Vector2 mousePosition = new Vector2((float) WORLD_WIDTH / 2, (float) WORLD_HEIGHT / 2);
                spawnShip(purpleShipTexture, mousePosition);
                makePlayerShip(cpuShips.first());

            }
        });

        quitButton = new TextButton("Quit", buttonStyle);
        quitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                quitButton.setStyle(buttonStyle2);

                // Exit the game when this button is pressed.
                Gdx.app.exit();
            }
        });

        stage.addActor(buttonQuitToDesktop);
        stage.addActor(button2);
        stage.addActor(button3);
        stage.addActor(quitButton);
        stage.addActor(quitButton2);
        stage.addActor(scoreDisplay);


    }

    // Create the TextButtonStyle objects to be used with the TextButtons.
    TextButton.TextButtonStyle createRedButtonStyle(Skin skin) {

        // TODO: Get a custom font in here instead of default-font.
        TextButton.TextButtonStyle buttonStyle2 = new TextButton.TextButtonStyle();
        buttonStyle2.font = skin.getFont("default-font");
        buttonStyle2.fontColor = Color.RED;
        return buttonStyle2;
    }

    // For making the camera move with the cursor when in SELECT_MODE.
    void cursorPushCamera(OrthographicCamera camera) {

        float mouseX = Gdx.input.getX();
        float mouseY = Gdx.input.getY();
        Vector3 unprojected = camera.unproject(new Vector3(mouseX, mouseY, 0));
        Vector2 position = new Vector2(unprojected.x, unprojected.y);
        float x = position.x;
        float y = position.y;
        float cameraX = camera.position.x;
        float cameraY = camera.position.y;

         float upperBoundary = cameraY + (float) viewport.getScreenHeight()/2;
        float lowerBoundary = cameraY - (float) viewport.getScreenHeight()/2;
        float rightBoundary = cameraX + (float) viewport.getScreenWidth()/2;
        float leftBoundary = cameraX - (float) viewport.getScreenWidth()/2;

        // The increment variables are just for easy swapping in and out.
        float incrementBoundaryAlpha = 300;
        float incrementBoundaryBeta = 600;
        float incrementCameraPosition = 6;

        if (x >= rightBoundary) {
            camera.position.x += incrementCameraPosition;
            if (x >= rightBoundary+ incrementBoundaryAlpha){
                camera.position.x += incrementCameraPosition;
                if (x >= rightBoundary+ incrementBoundaryBeta){
                    camera.position.x += incrementCameraPosition;

                }
            }
        }

        if (x <= leftBoundary) {
            camera.position.x -= incrementCameraPosition;
            if (x <= leftBoundary - incrementBoundaryAlpha){
                camera.position.x -= incrementCameraPosition;
                if (x <=leftBoundary - incrementBoundaryBeta){
                    camera.position.x -= incrementCameraPosition;

                }
            }
        }

        if (y >= upperBoundary) {
            camera.position.y += incrementCameraPosition;
            if (y >= upperBoundary + incrementBoundaryAlpha){
                camera.position.y += incrementCameraPosition;
               if(y >= upperBoundary + incrementBoundaryBeta) {
                   camera.position.y += incrementCameraPosition;

               }
            }
        }

        if (y <= lowerBoundary) {
            camera.position.y -= incrementCameraPosition;
            if (y <= upperBoundary - incrementBoundaryAlpha){
                camera.position.y -= incrementCameraPosition;
                if(y <= upperBoundary - incrementBoundaryBeta) {
                    camera.position.y -= incrementCameraPosition;

                    
                }
            }

        }

    }

    TextButton.TextButtonStyle createGreenTextButton(Skin skin) {

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = skin.getFont("default-font");
        buttonStyle.fontColor = Color.GREEN;
        return buttonStyle;
    }

    void handleScreenMode(float deltaTime) {

        if (currentScreen == VSSG.Screen.TITLE && buttonQuitToDesktop != null && button2 != null) {
            placeTitleScreenButtons(deltaTime);
        } else if (currentScreen == VSSG.Screen.MAIN_GAME) {
            clearScreenForMainGame();
            // Check if the cursor is far enough away from center to move the screen if the game is in selection mode.
            if (cursorMode == CursorMode.SELECTION_MODE){
                cursorPushCamera(camera);
            }

            if (gameMode == GameMode.ARCADE) {
                if (cpuShips.isEmpty()) {
                    arcadeModeRefill();
                    scoreDisplay.setVisible(true);

                }
            } else scoreDisplay.setVisible(false);

            handleClickTimeout();
            handleInput();
            chooseMode();
            setButtonPositions();

            playerIter = playerShips.iterator();
            cpuIter = cpuShips.iterator();
            explosionIter = explosions.iterator();
            laserIter = lasers.iterator();
            copyIter = cpuShipsCopy.iterator();

            stage.act(deltaTime);
            checkIterators(playerIter, explosionIter, cpuIter, copyIter, laserIter, deltaTime);

            // Draws the sprites to the screen as a batch.
            batch.begin();
            batch.draw(backgroundTexture, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, 0, 0, wrapDivisor, wrapDivisor);
            checkObjects(deltaTime);
            stage.draw();
            batch.end();
            handlePlayerHealthBar();
            scoreDisplay.setPosition(camera.position.x - ((float) viewport.getScreenWidth() / 4) * (camera.zoom / 2), camera.position.y + ((viewport.getScreenHeight() - ((float) viewport.getScreenHeight() / 20)-150 ) * (camera.zoom / 2)));

        }

    }

    void handlePlayerHealthBar() {

        for (PlayerShip playerShip : playerShips) {

            healthBarShapeRenderer.setProjectionMatrix(camera.combined);
// Define the inside of the rectangle for the healthbar.
            healthBarShapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            healthBarShapeRenderer.setColor(Color.RED);
            healthBarShapeRenderer.rect(camera.position.x + ((float) viewport.getScreenWidth() / 2) * (camera.zoom / 2), camera.position.y + ((viewport.getScreenHeight() - ((float) viewport.getScreenHeight() / 20)) * (camera.zoom / 2)), playerShip.getHp() * 5 * camera.zoom / 2, 50 * camera.zoom / 2);
            healthBarShapeRenderer.end();
// Define the border of the rectangle for the healthbar.
            healthBarShapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            healthBarShapeRenderer.setColor(Color.WHITE);
            healthBarShapeRenderer.rect(camera.position.x + ((float) viewport.getScreenWidth() / 2) * (camera.zoom / 2), camera.position.y + ((viewport.getScreenHeight() - ((float) viewport.getScreenHeight() / 20)) * (camera.zoom / 2)), 500 * camera.zoom / 2, 50 * camera.zoom / 2);
            healthBarShapeRenderer.end();
        }

    }

    TextButton createQuitButton2(TextButton.TextButtonStyle buttonStyle, TextButton.TextButtonStyle buttonStyle2) {

        quitButton2 = new TextButton("QUIT TO MENU", buttonStyle);
        quitButton2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // When the quit button is clicked, set the screen to the TITLE screen.
                quitButton2.setStyle(buttonStyle2);
                currentScreen = VSSG.Screen.TITLE;
                buttonQuitToDesktop.setVisible(false);
                quitButton2.setVisible(false);
                // Eliminate any residual ships that might not have been removed when the game was exited to the pause screen.
                flushShips();

                // Start over by calling the create() method.
               create();
                camera.position.x += (float) viewport.getScreenWidth() /2;
                camera.position.y += (float) viewport.getScreenHeight() /2;
                batch.begin();
               // TODO: Replace this with a more logo-like sprite.
                font.draw(batch, "VSSG", ((float) viewport.getScreenWidth() /5) , (float) viewport.getScreenHeight() /5);
                batch.end();
            }
        });
        return quitButton2;
    }


    void setButtonPositions() {

        Vector2 buttonPosition = new Vector2(camera.position.x, camera.position.y);
        Vector2 quitButton2Position = new Vector2(camera.position.x, camera.position.y);
        if (cursorMode == CursorMode.MENU_MODE && buttonQuitToDesktop != null) {
            populateMenu(buttonPosition, quitButton2Position);
        } else {
            if (buttonQuitToDesktop != null && quitButton2 != null) {
                buttonQuitToDesktop.setVisible(false);
                quitButton2.setVisible(false);

            }
        }
    }

    void populateMenu(Vector2 buttonPosition, Vector2 quitButton2Position) {
        buttonQuitToDesktop.setPosition(buttonPosition.x - buttonQuitToDesktop.getWidth() / 2, buttonPosition.y);
        quitButton2.setPosition(quitButton2Position.x - quitButton2.getWidth() / 2, quitButton2Position.y + 300);
        buttonQuitToDesktop.setVisible(true);
        quitButton2.setVisible(true);
        scoreDisplay.setVisible(false);

    }

    void clearScreenForMainGame() {
        ScreenUtils.clear(0, 0, 0, 1);
        Gdx.gl.glClear(GL32.GL_COLOR_BUFFER_BIT);

    }

    void placeTitleScreenButtons(float deltaTime) {

       clearScreenForMainGame();

        buttonQuitToDesktop.setVisible(false);
        quitButton2.setVisible(false);
        button2.setVisible(true);
        button3.setVisible(true);
        quitButton.setVisible(true);
        scoreDisplay.setVisible(false);
        stage.act(deltaTime);

        batch.begin();
      //  backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        batch.draw(backgroundTexture, -5000f,  -1000f, (float) WORLD_WIDTH /2, (float) WORLD_HEIGHT /2, 0, 0, wrapDivisor, wrapDivisor);

        font.draw(batch, "VSSG", ((Gdx.graphics.getWidth() * 0.4f )) , (Gdx.graphics.getHeight() * 0.9f));
        stage.draw();

        Vector2 button2Position = new Vector2(camera.position.x, camera.position.y - 200);
        button2.setPosition(button2Position.x - button2.getWidth() / 2, button2Position.y);

        Vector2 button3Position = new Vector2(camera.position.x, camera.position.y - 400);
        button3.setPosition(button3Position.x - button3.getWidth() / 2, button3Position.y);

        Vector2 quitbuttonPosition = new Vector2(camera.position.x, camera.position.y - 600);
        quitButton.setPosition(quitbuttonPosition.x - quitButton.getWidth() / 2, quitbuttonPosition.y);

        batch.end();
    }

    PlayerShip makePlayerShip(CpuShip cpuShip) {
        if (playerShips.isEmpty()) {
            System.out.println("makePlayerShip");
            cpuShip.setInactive(cpuShip);
            cpuShipsCopy.remove(cpuShip);
            cpuShip.getTargets().clear();
            return new PlayerShip(cpuShip.getTexture(), cpuShip.getExhaustTexture(), cpuShip.getPosition(), cpuShip.getSpeed(), Ship.ActionState.PLAYER_CONTROL,
                    cpuShip.getActionState(), cpuShip.getHitbox(), cpuShip.getActionCounter(), cpuShip.getFaction(), cpuShip.getTargets(),
                    cpuShip.getHp(), cpuShip.getType(), cpuShip.getRotation());
        } else {
            System.out.println("PlayerShip couldn't be created. Line 365 @ VSSG.java");
            return null;
        }
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
       // explosionTexture2 = new Texture("explosion2.png");
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
        PlayerShip playerShip = new PlayerShip(purpleShipTexture, exhaust, vector2, 40, Ship.ActionState.PLAYER_CONTROL,
                Ship.ActionState.PLAYER_CONTROL, hitBox, playerActionCounter, Ship.Faction.PURPLE, targets, 100, Ship.Type.FIGHTER, 90);

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
        cpuShipsCopy = new ObjectSet<>(cpuShips);
        playerShips = new ObjectSet<>();
        explosions = new ObjectSet<>();
        lasers = new ObjectSet<>();
        purpleShipButton = new Sprite(purpleShipButtonTexture);
        tealShipButton = new Sprite(tealShipButtonTexture);
        batch = new SpriteBatch();

    }

    // Release control of the currently controlled ship.
    void relinquishControl(PlayerShip playerShip) {

        CpuShip cpuShip = new CpuShip(playerShip.getTexture(), playerShip.getExhaustTexture(), playerShip.getPosition(), playerShip.getSpeed(),
                Ship.ActionState.IDLE, Ship.ActionState.IDLE, playerShip.getHitbox(), playerShip.getActionCounter(), playerShip.getFaction(),
                playerShip.getTargets(), playerShip.getHp(), playerShip.getType(), playerShip.getRotation());

        cpuShip.setRotation(playerShip.getRotation());
        cpuShip.setSpeed(playerShip.getSpeed());
        playerShip.setInactive(playerShip);
        playerShips.remove(playerShip);
        playerShip.increaseHp(100 - playerShip.getHp());
        cpuShips.add(cpuShip);
        cpuShipsCopy.add(cpuShip);
    }

    // Determines which state th game should be in, depending on whether a PlayerShip is active and whether the game is paused.
    // When the game is paused, just go to MENU_MODE (put the pause screen up);
    // If it isn't paused, then check if there is a playerShip active.
    // If not, go to SELECTION_MODE so the player can middle-click to take control of another ship.
    void chooseMode() {

        if (isPaused) {
            cursorMode = CursorMode.MENU_MODE;
        } else if (playerShips.notEmpty() && !isPaused) {
            cursorMode = CursorMode.PLAYER_MODE;
        } else if (playerShips.isEmpty() && !isPaused) {
            cursorMode = CursorMode.SELECTION_MODE;
        }
    }

    // Take control of a ship!
    void takeControlOfShip() {
        if (gameMode != GameMode.ARCADE) {
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

    }

    /////////////   C O N T R O L S   /////////////
    private void handleInput() {

        float cameraSpeed = camera.zoom * 2048;

        if (InputManager.isMiddlePressed()) {
            takeControlOfShip();
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

// Space bar behavior
        if (InputManager.isSpacePressed()) {
            playerFireLaser();
        }

        // RIGHT MOUSE BUTTON
        if (InputManager.isRightMousePressed()) {
            playerSpawnPurpleShip();
        }
        if (shipSpawnTimeout) {
            if (shipSpawnCounter >= 20) {
                shipSpawnTimeout = false;
            } else {
                shipSpawnCounter++;
            }
        }

// For player ship only during runtime. CpuShip laser timing is handled differently.
        handlePlayerLaserSpawn();

// LEFT MOUSE BUTTON BEHAVIOR
        if (InputManager.isLeftMousePressed()) {
            playerSpawnTealShip();
        }
        float speedLimit = 600f;
        if (InputManager.isWPressed()) {
            handleSpeedUp(speedLimit);
        }

        if (InputManager.isSPressed()) {
            handleSlowDown(speedLimit);
        }

        if (InputManager.isEscPressed()) {
            pauseNow();
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
            handleZoomIn();
        }

        if (InputManager.isEPressed()) {
            handleZoomOut();
        }


        if (InputManager.isCPressed()) {

            if(gameMode != GameMode.ARCADE) {

                if (!isPaused) {
                    if (!playerShips.isEmpty()) {
                        relinquishControl(playerShips.first());
                    }
                }
            }
        }
    }

    // Creates a ship from the player's input.
    void playerSpawnTealShip() {

        if (gameMode == GameMode.SANDBOX) {
            if (!isPaused) {
                float mouseX = Gdx.input.getX();
                float mouseY = Gdx.input.getY();
                Vector2 position = new Vector2(mouseX, mouseY);
                spawnShip(tealShipTexture, position);
            }
        }
    }

    void playerSpawnPurpleShip() {

        if (gameMode == GameMode.SANDBOX) {
            float mouseX = Gdx.input.getX();
            float mouseY = Gdx.input.getY();
            Vector2 position = new Vector2(mouseX, mouseY);
            spawnShip(purpleShipTexture, position);
        }

    }

    void handlePlayerLaserSpawn() {
        if (!playerShips.isEmpty()) {
            if (playerShips.first().getLaserSpawnTimeout()) {
                if (playerShips.first().getLaserSpawnCounter() >= 20) {

                    playerShips.first().setLaserSpawnTimeout(false);
                } else {
                    playerShips.first().setLaserSpawnCounter(playerShips.first().getLaserSpawnCounter() + 1);
                }
            }
        }
    }

    void handleSpeedUp(float speedLimit) {

        if (!isPaused) {
            for (PlayerShip playerShip : playerShips) {

                if (playerShip.getSpeed() < speedLimit && playerShip.getSpeed() >= 0) {
                    playerShip.setSpeed(playerShip.getSpeed() + 2);
                }
            }
        }
    }

    void handleSlowDown(float speedLimit) {
        if (!isPaused) {
            for (PlayerShip playerShip : playerShips) {
                if (playerShip.getSpeed() <= speedLimit && playerShip.getSpeed() > 0) {
                    playerShip.setSpeed(playerShip.getSpeed() - 2);
                }
            }
        }
    }

    void handleZoomOut() {

        if (!isPaused) {
            if (camera.zoom < 23) {
                zoomOut();
            }
        }
    }

    void handleZoomIn() {
        if (!isPaused) {
            if (camera.zoom > 0.2f) {
                zoomIn();
            }
        }
    }

    void playerFireLaser() {

        Texture laserTexture = null;
        for (Ship ship : playerShips) {
            if (!ship.getLaserSpawnTimeout()) {
                if (ship.getType() == Ship.Type.CORVETTE) {
                    laserTexture = laser2Texture;
                } else if (ship.getType() == Ship.Type.FIGHTER) {
                    if (ship.getFaction() == Ship.Faction.TEAL) {
                        laserTexture = redLaserTexture;
                    } else if (ship.getFaction() == Ship.Faction.PURPLE) {
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

    void pauseNow() {
        if (clickTimeout > 100) {
            pauseGame();
            clickTimeout = 0;
        } else clickTimeout++;

    }


    void pauseGame() {
        isPaused = !isPaused;
    }


    private void zoomIn() {
        camera.zoom -= zoomSpeed * camera.zoom;
        camera.update();
    }

    private void zoomOut() {
        camera.zoom += zoomSpeed * camera.zoom;
        camera.update();
    }


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
            Vector2 position = new Vector2(ship.getX() + ship.getWidth() / 2, laser.getY() - ship.getHeight() / 2);
            Explosion.explode(explosionTexture1, position, 512, explosions, explosionSound1, 16, 0.33f);

            if (ship.getActionState() == Ship.ActionState.PLAYER_CONTROL) {
                ship.decreaseHp(5);

            } else {
                ship.decreaseHp(50);
            }

            laser.setInactive(laser);
            if (ship.getHp() <= 0) {
                ship.setInactive(ship);

                if (ship.getActionState() != Ship.ActionState.PLAYER_CONTROL && gameMode == GameMode.ARCADE) {
                    score = score+1;
                    scoreDisplay.setText("SCORE: "+score);
                    System.out.println("Point acquired! Score = "+score);
                }
                Explosion.explode(explosionTexture1, position, 400, explosions, explosionSound1, 128, 0.7f);
            }
        }
    }


    public void checkObjects(float deltaTime) {
        for (Laser laser : lasers) {

            // Determine laser's scale based on its texture.
            if (laser.getTexture() == laser2Texture) {
                laser.setScale(0.8f);
            } else {
                laser.setScale(3);
            }
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
            playerShip.getExhaustTexture().draw(batch);
            playerShip.handleActionState(playerShip, laser2Texture, greenLaserTexture, blueLaserTexture, redLaserTexture, lasers, laserBlast2);
            camera.position.x = playerShip.getX() + playerShip.getWidth() / 2;
            camera.position.y = playerShip.getY() + playerShip.getHeight() / 2;
        }


        // COMPUTER SHIPS BEHAVIOR /////////////////////////////////////////////
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

            //  To iterate through the CpuShips list while already iterating
            //  through it in the main for loop, we need a copy of that list
            //  to iterate through.  This is why we have the "cpuShipsCopy" ObjectSet<CpuShip>.
            for (CpuShip cpuShip2 : cpuShipsCopy) {
                if (cpuShip2 != null) {
                    cpuShip.detectTargets(cpuShip2, cpuShip.getTargets());
                }
            }
        }
///////////////////////////////////////////////////////


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
            cpuShipsCopy.add(cpuShip);

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

    void arcadeModeRefill() {

        if (cpuShips.isEmpty()) {
            waveNumber++;
            int i = 1;
            while (i < waveNumber) {
                Vector2 position = new Vector2();
                position.x = worldWidthCentre + (i * 1000);
                position.y = worldHeightCentre + (2000);
                Rectangle hitbox = new Rectangle();
                ObjectSet<Ship> targets = new ObjectSet<>();

                CpuShip enemy = new CpuShip(tealShipTexture, exhaust, position, 100, Ship.ActionState.IDLE, Ship.ActionState.IDLE, hitbox,
                        0, Ship.Faction.TEAL, targets, 100, Ship.Type.FIGHTER, 0);

                enemy.setPosition(position.x, position.y);
                enemy.setScale(shipScale);
                enemy.setType(Ship.Type.FIGHTER);
                enemy.setOrigin(enemy.getOriginX(), enemy.getOriginY());

                cpuShips.add(enemy);
                cpuShipsCopy.add(enemy);
                i++;
            }

        }

    }

    // MAIN GAME LOOP: "RENDER LOOP" //////////////////////////////

    @Override
    public void render() {

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        // Measure change in time from last frame / render loop execution.
        float deltaTime = Gdx.graphics.getDeltaTime();
        // Tell the game what to do based on what screen/activity it is supposed ot be running.
        handleScreenMode(deltaTime);

    }
}