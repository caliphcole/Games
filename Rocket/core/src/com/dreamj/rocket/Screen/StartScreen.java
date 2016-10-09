package com.dreamj.rocket.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dreamj.rocket.TRGame;
import com.dreamj.rocket.TRHelper.AssetLoader;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;
import com.dreamj.rocket.tweenaccessor.SpriteAccessor;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by Caliph Cole on 05/21/2015.
 */
public class StartScreen implements InputProcessor, Screen {

    private static Preferences prefs;
    private static Preferences adprefs;

    private TweenManager manager;

    private SpriteBatch batcher;
    private SpriteBatch batch;
    private Sprite spritebackground;
    private Texture tap;
    private SpriteBatch spriteBatch;
    private BitmapFont bitmapFont;
    private String earthDestroyMessage = "The Earth\n\nHas Been \n\n    Destroyed!!! \n\n    You Have Escaped!!! \n\n    Save Yourself....";

    private Rectangle tapobject;

    private OrthographicCamera cam2d;
    private PerspectiveCamera cam3d;
    private Viewport viewport;
    private int  WIDTH,HEIGHT;
    private final float scrollSpeed = 8.0f; //unit per second
    private float timeformessagerunoff =  0;
    private float time = 8.0f;
    private static int count;

    private Music tradsoundtrack;//sound track

    private AdsController adsController;
    private GameShareInterface gameShareInterface;

    private TRGame game;



    public StartScreen(TRGame game,AdsController adsController, GameShareInterface gameShareInterface){
        this.game = game;
        this.adsController = adsController;
        this.gameShareInterface = gameShareInterface;
    }
    @Override
    public void show() {

        setUpadsPref();
        count = adprefs.getInteger("adscount");
        setadspref(count +1 );
        adsController.showBannerAd();//hide ads
        adsController.showInInterstitialAd();



        cam2d = new OrthographicCamera();
        cam2d.setToOrtho(false, 480, 800);

        setUpGameEnterPref();

        spriteBatch = new SpriteBatch();
        batch = new SpriteBatch();
        cam3d = new PerspectiveCamera();
        viewport = new StretchViewport(480, 800, cam3d);
        spritebackground = new Sprite(AssetLoader.startbackground);
        batcher = new SpriteBatch();
        tap = new Texture(Gdx.files.internal("taptostart.png"));
        bitmapFont = new BitmapFont();
        bitmapFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        bitmapFont.setUseIntegerPositions(false);
        bitmapFont.setColor(Color.RED);

        spritebackground.setColor(1, 1, 1, 0);

        tapobject = new Rectangle();
        tapobject.x = 480/2-101/2;
        double y = 800/6;
        int x = 163/2;
        tapobject.y = (int)y-x;


        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        //Full Screen start screen background
        spritebackground.setSize(width, height);
        spritebackground.setPosition((width / 2) - (spritebackground.getWidth() / 2), (height / 2)
                - (spritebackground.getHeight() / 2));


        setupTween();


        Gdx.input.setInputProcessor(this);//listen for input from the user

        Gdx.input.setCatchBackKey(true);

        tradsoundtrack = Gdx.audio.newMusic(Gdx.files.internal("tradsoundtrack.mp3"));
        tradsoundtrack.setLooping(true);
        tradsoundtrack.play();
    }

    private void setupTween() {
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        manager = new TweenManager();

        Tween.to(spritebackground, SpriteAccessor.ALPHA, .8f).target(1)
                .ease(TweenEquations.easeInQuad).start(manager);


    }

    @Override
    public void render(float delta) {

        manager.update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        spriteBatch.setProjectionMatrix(cam3d.combined);
        float dt = Gdx.graphics.getDeltaTime();
        cam3d.translate(0.0f, -dt * scrollSpeed, 0.0f);
        cam3d.update(false);
        batch.setProjectionMatrix(cam2d.combined);

        batcher.begin();
        spritebackground.draw(batcher);


        batcher.end();

        batch.begin();

        timeformessagerunoff += delta;
        if(timeformessagerunoff>time || prefs.getInteger("enter")==9){
            //spritetap.draw(batcher);
            batch.draw(tap,tapobject.x,tapobject.y);
            timeformessagerunoff = 9;
            prefs.putInteger("enter",(int)timeformessagerunoff);
            prefs.flush();
        }
        batch.end();

        spriteBatch.begin();
        bitmapFont.draw(spriteBatch, earthDestroyMessage, -cam3d.viewportWidth, -cam3d.viewportHeight, cam3d.viewportWidth, 1, false);
        spriteBatch.end();

    }

    @Override
    public void resize(int width, int height) {

        WIDTH = width;
        HEIGHT = height;

        //define an ortho camera 10 unit wide with height depending on aspect ratio
        float camWidth = 10.0f;
        float camHeight = camWidth * (float)HEIGHT / (float)WIDTH;
        cam2d = new OrthographicCamera();//(camWidth, camHeight);
        cam2d.setToOrtho(false, 480, 800);
        //cam2d.position.set(camWidth / 2.0f, camHeight / 2.0f, 0.0f);
        cam2d.update();

        //define the perspective camera
        Gdx.app.log("camwidth:", camWidth + " camHeight: " + camHeight);
        cam3d = new PerspectiveCamera(35.0f, camWidth* 0.0000000001f,camHeight * 0.00000000001f);

        cam3d.translate(0.0f, -10.0f, 3.0f);

        cam3d.lookAt(0.0f, 0.0f, 0.0f);
        cam3d.update(true);
        viewport.update(width, height);

    }

    @Override
    public void pause() {

        tradsoundtrack.pause();

    }

    @Override
    public void resume() {

        tradsoundtrack.play();

    }

    @Override
    public void dispose() {

        tradsoundtrack.dispose();
        batcher.dispose();
        spriteBatch.dispose();
        bitmapFont.dispose();

    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        if(timeformessagerunoff ==9) {// should check if  its the first time using the app
            game.setScreen(new MainGameScreen(game, adsController, gameShareInterface));

            return false;
        }
        return false;
    }

    private  static void setUpGameEnterPref(){

        prefs = Gdx.app.getPreferences("Enter");

        if (!prefs.contains("enter")) {
            prefs.putInteger("enter", 0);
        }

    }

    private static void setUpadsPref(){
        adprefs = Gdx.app.getPreferences("adscount");

        if(!adprefs.contains("adscount")){
            adprefs.putInteger("adscount",0);
            adprefs.flush();
        }

    }

    private static void setadspref(int c){
        adprefs.putInteger("adscount",c);
        adprefs.flush();
    }

    @Override
    public void hide() {
        dispose();
    }


    @Override
    public boolean keyDown(int keycode) {
        if(keycode == Input.Keys.BACK) {
            Gdx.app.exit();

        }
        return false;
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
