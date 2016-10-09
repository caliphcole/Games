package com.dreamj.rocket.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dreamj.rocket.Screen.StartScreen;
import com.dreamj.rocket.TRGame;
import com.dreamj.rocket.TRHelper.Score;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;

import java.util.Iterator;

import aurelienribon.tweenengine.TweenManager;

/**
 * Created by Caliph Cole on 05/21/2015.
 */
public class Renderer implements InputProcessor{


    /*
    Shape Renderer used to test the position of the actual object used to check collisions
     */
    private ShapeRenderer shapeRenderer;
    private ShapeRenderer shapeRenderer1;
    private ShapeRenderer planetRenderer;


    private TweenManager manager;

    private SpriteBatch batch;
    private SpriteBatch batcher;
    private BitmapFont font;
    private Score score;

    private Stage stage;

    private OrthographicCamera camera;
    private Viewport viewport;

    //Touchpad joystick variables
    private Touchpad touchpad;
    private Touchpad.TouchpadStyle touchpadStyle;
    private Skin touchpadskin;
    private Drawable touchBackground, touchKnob;


    //Arrays used for storing planets and meteors after generating them
    private Array<Circle> meteorObjects;
    private Array<Circle> planetObjects;


    private Rectangle spaceshipobject;// rectangle that is used to store the dimensions and cooridinates

    /*
    Objects used for the actual collision detection of the elements on maingame screen
     */
    private Rectangle shipobject;
    private Rectangle shipobjectbottom;
    private Circle meteorobject;
    private Circle planetobject;

    //Textures of elements on the screen
    private Texture meteor, planet1, planet3, planet4, planet5, planet6, planet8, planet9, spaceship1;

    private static Texture background;
    private static Sprite backgroundSprite;


    // short lived sounds for score and crashes
    private Sound scoreBeep, crash;

    private Music mainsoundtrack;

    //store the list of planets
    private Array<Texture> planets;


    private double lastPassTimeMeteor =10;// for the meteor
    private double lastPassTimePlanet = 10;// for the planets

    private float spaceCraftSpeed;// speed for of the spaceship

    private boolean collision = false;// collison dettection boolean


    private int setcount = 0;//handles the spawning of different planets


    TRGame game;
    AdsController adsController;
    GameShareInterface gameShareInterface;


    public void setup(TRGame game, GameShareInterface gameShareInterface, AdsController adsController) {
        this.adsController = adsController;
        this.gameShareInterface = gameShareInterface;
        this.game = game;
        //test
        shapeRenderer = new ShapeRenderer();
        planetRenderer = new ShapeRenderer();
        shapeRenderer1 = new ShapeRenderer();
        //test

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
        viewport = new StretchViewport(480, 800, camera);

        meteorobject = new Circle();
        planetobject = new Circle();
        shipobject = new Rectangle();
        shipobjectbottom = new Rectangle();


        batch = new SpriteBatch();
        batcher = new SpriteBatch();
        score = new Score();


        //create a touchpad skin
        touchpadskin = new Skin();
        //setbackground image
        touchpadskin.add("touchBackground", new Texture(Gdx.files.internal("background.png")));
        //Set Knob image
        touchpadskin.add("touchKnob", new Texture(Gdx.files.internal("knob.png")));

        //Create TouchPad Style
        touchpadStyle = new Touchpad.TouchpadStyle();

        //Create Drawable's from TouchPad skin
        touchBackground = touchpadskin.getDrawable("touchBackground");
        touchKnob = touchpadskin.getDrawable("touchKnob");


        //Apply the Drawables to the Touchpad style
        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        //create new Touchpad with the created style
        touchpad = new Touchpad(10, touchpadStyle);


        //setBounds(x,y,width,height);
        touchpad.setBounds(480/2-135/2, 15, 135, 135);


        //space craft rectangle
        spaceshipobject = new Rectangle();
        spaceshipobject.x = 480 / 2 - 90 / 2;
        spaceshipobject.y = 150;
        spaceshipobject.width = 50;
        spaceshipobject.height = 103;

        //Space objects
        background = new Texture(Gdx.files.internal("mainbackground.jpg"));
        meteor = new Texture(Gdx.files.internal("meteor.png"));
        planet1 = new Texture(Gdx.files.internal("planet1.png"));
        planet3 = new Texture(Gdx.files.internal("planet3.png"));
        planet4 = new Texture(Gdx.files.internal("planet4.png"));
        planet5 = new Texture(Gdx.files.internal("planet5.png"));
        planet6 = new Texture(Gdx.files.internal("planet6.png"));
        planet8 = new Texture(Gdx.files.internal("planet8.png"));
        planet9 = new Texture(Gdx.files.internal("planet9.png"));
        planets = new Array<Texture>();

        //add texture to the planets
        planets.add(planet1);
        planets.add(planet3);
        planets.add(planet4);
        planets.add(planet5);
        planets.add(planet6);
        planets.add(planet8);
        planets.add(planet9);

        spaceship1 = new Texture(Gdx.files.internal("spaceship1.png"));

        //supernova = new Texture(Gdx.files.internal("supernova.png"));
        //blackHole = new Texture(Gdx.files.internal("blackhole.png"));

        backgroundSprite = new Sprite(background);
        backgroundSprite.setSize(480, 800);


        //Sound, short lived
        scoreBeep = Gdx.audio.newSound(Gdx.files.internal("score.wav"));
        crash = Gdx.audio.newSound(Gdx.files.internal("crash.mp3"));

        //Music
        mainsoundtrack = Gdx.audio.newMusic(Gdx.files.internal("mainsoundtrack.mp3"));


        // store the spawn elements
        meteorObjects = new Array<Circle>();
        planetObjects = new Array<Circle>();

        // spawn the first space elements
        //spawnSpaceMeteor();
        //spawnSpacePlanet();

        //Create a Stage and add TouchPad
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        stage.addActor(touchpad);

        spaceCraftSpeed = 5;

        //instantiate font with the external fnt files
        font = new BitmapFont(Gdx.files.internal("data/score.fnt"),
                Gdx.files.internal("data/score_0.png"), false);

        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);

        mainsoundtrack.play();
        mainsoundtrack.setLooping(true);
    }


    public void render() {

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer1.setProjectionMatrix(camera.combined);
        planetRenderer.setProjectionMatrix(camera.combined);
        stage.setViewport(viewport);

        //Move blockSprite with TouchPad
        spaceshipobject.setX(spaceshipobject.getX() + touchpad.getKnobPercentX() * spaceCraftSpeed);
        spaceshipobject.setY(spaceshipobject.getY() + touchpad.getKnobPercentY() * spaceCraftSpeed);


        batch.begin();
        backgroundSprite.draw(batch);

        batch.draw(spaceship1, spaceshipobject.x, spaceshipobject.y);//draw spaceship to screen
        font.draw(batch, "" + score.getCount(), 360, 750);

        //loop through array of meteor and draw
        for (Circle spaceobject : meteorObjects) {

            spaceobject.setRadius(6.0f);
            batch.draw(meteor, spaceobject.x, spaceobject.y);//draw meteor
            //meteorobject.set(spaceobject.x+33, spaceobject.y+32,30.0f);

            // throufh the array of palnets
            //coount is used to handle the list of planets
            int count = setcount;
            Gdx.app.log("planetObjects size:", "" + planetObjects.size);
            for (Circle planetobject : planetObjects) {
                if (count < 7) {
                    batch.draw(meteor, spaceobject.x, spaceobject.y);
                    batch.draw(planets.get(count), planetobject.x, planetobject.y);
                    batch.draw(meteor, spaceobject.x, spaceobject.y);
                } else {
                    count = 0;
                    batch.draw(meteor, spaceobject.x, spaceobject.y);
                    batch.draw(planets.get(count), planetobject.x, planetobject.y);
                    batch.draw(meteor, spaceobject.x, spaceobject.y);
                }


                count++;

            }
        }

        //draw touch pad
        stage.draw();
        batch.end();
        /**
         * Testing code for the collision shapes
         */
       /* planetRenderer.begin(ShapeRenderer.ShapeType.Filled);
        planetRenderer.setColor(Color.RED);
        for(Circle spaceobject: meteorObjects){

            planetRenderer.circle(spaceobject.x+33, spaceobject.y+32,30.0f);

            for(Circle planetobject: planetObjects) {

                planetRenderer.circle(planetobject.x+51, planetobject.y+50,48.0f);
            }
        }

        planetRenderer.end();*/


      /* shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(spaceshipobject.x + 15, spaceshipobject.y + 7, spaceshipobject.width - 12, spaceshipobject.height - 11);
        shapeRenderer.end();
        shapeRenderer1.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer1.setColor(Color.RED);
        shapeRenderer1.rect(spaceshipobject.x + 5, spaceshipobject.y + 7, spaceshipobject.width + 5, spaceshipobject.height - 70);
        shapeRenderer1.end();*/

        lastPassTimeMeteor *=1.6;
        if (lastPassTimeMeteor > 100000000000.0) {

            lastPassTimeMeteor = 10;
            spawnSpaceMeteor();

        }

        lastPassTimePlanet *= 1.3;
        if (lastPassTimePlanet > 100000000000.0) {

            lastPassTimePlanet = 10;
            spawnSpacePlanet();
        }

        Iterator<Circle> itermeteor = meteorObjects.iterator();
        Iterator<Circle> iterplanet = planetObjects.iterator();


        // this while loop brings the space elements down the screen, remove them when they are at the bottom and check for collision
        while (itermeteor.hasNext() || iterplanet.hasNext()) {

            try {
                Circle meteorObject = itermeteor.next();
                meteorObject.y -= 220 * Gdx.graphics.getDeltaTime();
                if (meteorObject.y + 100 < 0) itermeteor.remove();

                meteorobject.set(meteorObject.x + 33, meteorObject.y + 32, 30.0f);
                shipobject.set(spaceshipobject.x + 15, spaceshipobject.y + 7, spaceshipobject.width - 12, spaceshipobject.height - 11);//----------------------------------------
                shipobjectbottom.set(spaceshipobject.x + 5, spaceshipobject.y + 7, spaceshipobject.width + 5, spaceshipobject.height - 70);
                if (Intersector.overlaps(meteorobject, shipobject) || Intersector.overlaps(meteorobject, shipobjectbottom)) {
                    crash.play();
                    collision = true;
                }
            } catch (Exception e) {

            }
            try {
                Circle planetObject = iterplanet.next();
                planetObject.y -= 200 * Gdx.graphics.getDeltaTime();
                if (planetObject.y + 100 < 0) {

                    setcount++;
                    if (setcount == 8) {
                        setcount = 1;
                    }

                    iterplanet.remove();

                    if (score.getCount() < 999) {

                        score.count();
                    }


                    scoreBeep.play();
                }

                planetobject.set(planetObject.x + 51, planetObject.y + 50, 48.0f);

                shipobject.set(spaceshipobject.x + 20, spaceshipobject.y + 7, spaceshipobject.width - 25, spaceshipobject.height - 11);
                shipobjectbottom.set(spaceshipobject.x + 5, spaceshipobject.y + 7, spaceshipobject.width + 5, spaceshipobject.height - 70);

                if (Intersector.overlaps(planetobject, shipobject) || Intersector.overlaps(planetobject, shipobjectbottom)) {
                    crash.play();// play crash sound
                    collision = true;//set true on collision
                }
            } catch (Exception e) {

            }

        }

        checkScreenBoundary();// this function checks the boundary on the screen
    }

    /**
     * @return collision
     */
    public boolean checkcollision() {
        return collision;
    }

    private void spawnSpaceMeteor() {
        Circle meteorObject1 = new Circle();
        meteorObject1.x = spaceshipobject.getX();// random x
        meteorObject1.y = 800;
        meteorObjects.add(meteorObject1);

        //lastPassTimeMeteor = TimeUtils.nanoTime();


    }


 /*   private void spawnSpaceMeteor1() {
        Circle meteorObject = new Circle();

        int[] temp = {300, 400};

      /*  if (corx1 < temp.length) {
            meteorObject.x = temp[corx1];//MathUtils.random(0, 480 - 80);// random x
            corx1++;
        } else {
            corx1 = 0;
        }

        meteorObject.y = 800; //start at the top of the screen
        meteorObjects.add(meteorObject);

        //lastPassTimeMeteor = TimeUtils.nanoTime();

        Gdx.app.log("getX",""+ spaceshipobject.getX());
        if (spaceshipobject.getX()>240 ){
            int count = 0;
           while(count<2){

               meteorObject.x = temp[count];
               meteorObject.y = 800; //start at the top of the screen
               meteorObjects.add(meteorObject);
               count++;
           }

        }else{
            if (corx1 < temp.length) {
                meteorObject.x = temp[corx1];//MathUtils.random(0, 480 - 80);// random x
                corx1++;
            } else {
                corx1 = 0;
            }

            meteorObject.y = 800; //start at the top of the screen
            meteorObjects.add(meteorObject);
        }


    }*/


    private void spawnSpacePlanet() {
        Circle planetObject = new Circle();

        planetObject.x = 480 / 2 - 100 / 2;// center
        planetObject.y = 800; //start at the top of the screen

        planetObjects.add(planetObject);

    }

    private void checkScreenBoundary() {


        if (spaceshipobject.getX() < 0) {
            spaceshipobject.setX(0);
        }
        if (spaceshipobject.getX() > 480 - 65) {
            spaceshipobject.setX(480 - 65);
        }
        if (spaceshipobject.getY() > 800 - 109) {
            spaceshipobject.setY(800 - 109);
        }
        if (spaceshipobject.getY() < 150) {
            spaceshipobject.setY(150);
        }


    }

    public Viewport resize(int width, int height) {
        camera.update();
        viewport.update(width, height);
        return viewport;

    }

    public int getCount() {
        return score.getCount();
    }


    public void dispose() {
        spaceship1.dispose();
        meteor.dispose();
        planet1.dispose();
        planet3.dispose();
        planet4.dispose();
        planet5.dispose();
        planet6.dispose();
        planet9.dispose();
        planet8.dispose();
        background.dispose();
        mainsoundtrack.dispose();

    }




    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.BACK){
            game.setScreen(new StartScreen(game, adsController, gameShareInterface));

        }

        return false;
    }
    public void hide(){


    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}