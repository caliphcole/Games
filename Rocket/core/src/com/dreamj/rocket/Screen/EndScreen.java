package com.dreamj.rocket.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dreamj.rocket.TRGame;
import com.dreamj.rocket.TRHelper.AssetLoader;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;
import com.dreamj.rocket.tweenaccessor.SpriteAccessor;
import com.sun.jndi.toolkit.url.Uri;

import java.nio.ByteBuffer;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;


/**
 * Created by Caliph Cole on 05/21/2015.
 */
public class EndScreen implements InputProcessor,Screen{

    private static Preferences prefs;

    private TweenManager manager;
    private OrthographicCamera camera;
    private SpriteBatch batcher;
    private  Texture scoreboard;
    private Texture highscoreboard;
    private BitmapFont fontHighscore;
    private BitmapFont fontScore;
    private Sprite sprite;
    private TRGame game;
    private static int score;
    private Rectangle rectscore;
    private Rectangle recthighscore;
    private Music tradsoundtrack;
    private AdsController adsController;

    private TextButton googleshare;
    private Stage stage;
    private TextButton.TextButtonStyle textButtonStyle;
    private Skin skin;
    private Drawable bttn;
    private BitmapFont fontbutton;
    private GameShareInterface gameShareInterface;
    private Uri uis;
    private Image m;
    private boolean ifbuttonnotclicked = true;
    private Viewport viewport;
    private SpriteBatch  bat;
    private static boolean starhighscore =false;

    public EndScreen(TRGame game,int score, AdsController adsController,GameShareInterface gameShareInterface){

        this.game = game;
        this.score = score;
        this.adsController = adsController;
        this.gameShareInterface = gameShareInterface;
    }
    @Override
    public void show() {
        adsController.showBannerAd();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 480, 800);
        viewport = new StretchViewport(480,800,camera);

        sprite = new Sprite(AssetLoader.startbackground);
        sprite.setColor(1, 1, 1, 0);

        scoreboard = new Texture(Gdx.files.internal("scoreboard.jpg"));
        highscoreboard = new Texture(Gdx.files.internal("highscoreboard.png"));

        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float desiredWidth = width * .7f;
        float scale = desiredWidth / sprite.getWidth();

        sprite.setSize(width, height);
        sprite.setPosition((width / 2) - (sprite.getWidth() / 2), (height / 2)
                - (sprite.getHeight() / 2));
        setupTween();
        batcher = new SpriteBatch();
        bat = new SpriteBatch();


        tradsoundtrack = Gdx.audio.newMusic(Gdx.files.internal("tradsoundtrack.mp3"));
        tradsoundtrack.setLooping(true);
        tradsoundtrack.play();

        setUpPrefHighScore();
        saveHighScore(score);
        fontHighscore = new BitmapFont(Gdx.files.internal("data/highscore.fnt"),
                Gdx.files.internal("data/highscore_0.png"), false);


        fontScore = new BitmapFont(Gdx.files.internal("data/score.fnt"),
                Gdx.files.internal("data/score_0.png"), false);


        rectscore = new Rectangle();
        rectscore.setX(480 / 2 - 260 / 2);
        rectscore.setY(800 / 2 - 187 / 2);

        recthighscore = new Rectangle();
        recthighscore.setX(480/2- 325/2);
        recthighscore.setY(800/2 - 195/2);


        stage = new Stage();
        stage.setViewport(viewport);



        fontbutton = new BitmapFont();
        skin = new Skin();
        skin.add("button", new Texture(Gdx.files.internal("gsharebutton.png")));
        textButtonStyle = new TextButton.TextButtonStyle();
        bttn = skin.getDrawable("button");

        textButtonStyle.font = fontbutton;
        textButtonStyle.up = bttn;
        textButtonStyle.down = bttn;
        textButtonStyle.checked = bttn;

        googleshare = new TextButton("", textButtonStyle);
        googleshare.setX(480-125);
        googleshare.setY(45);
        stage.addActor(googleshare);





        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(stage);
        multiplexer.addProcessor(this);
        Gdx.input.setInputProcessor(multiplexer);


        googleshare.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {

                Gdx.app.log("button being presses", "");
                Pixmap pix = getScreenshot(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

                googleshare.setVisible(false);
                gameShareInterface.shareScore(pix);
                googleshare.setVisible(true);
            }
        });

}

    private void setupTween() {
        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        manager = new TweenManager();

        TweenCallback cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {

            }
        };

        Tween.to(sprite, SpriteAccessor.ALPHA, .8f).target(1)
                .ease(TweenEquations.easeInQuad).start(manager);

    }

    @Override
    public void render(float delta) {

        manager.update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        camera.update();

        bat.setProjectionMatrix(camera.combined);


        batcher.begin();
        sprite.draw(batcher);
        batcher.end();

        bat.begin();
        if(starhighscore){
            bat.draw(highscoreboard, recthighscore.x, recthighscore.y);
        }else {
            bat.draw(scoreboard, rectscore.x, rectscore.y);
        }
        fontHighscore.draw(bat, "" + getHighScore(),252 , 378);

        if(getScore()<9){
            fontScore.draw(bat, "" + getScore(), 228, 440);
        }else if(getScore()<99){
            fontScore.draw(bat, "" + getScore(), 215, 440);
        }else {
            fontScore.draw(bat, "" + getScore(), 202, 440);
        }
        bat.end();

       stage.draw();


    }

    private  static void setUpPrefHighScore(){

        prefs = Gdx.app.getPreferences("Rocket");

        if (!prefs.contains("highScore")) {
            prefs.putInteger("highScore", 0);
        }

    }

    private static void saveHighScore(int score){

        if(score> getHighScore()){
            prefs.putInteger("highScore", score);
            prefs.flush();
            starhighscore = true;
        }else{
            starhighscore = false;
        }
    }

    public static int getHighScore() {
        return prefs.getInteger("highScore");
    }
    public int getScore(){
        return score;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,height);
        camera.update();
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
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        tradsoundtrack.dispose();
        scoreboard.dispose();
        fontScore.dispose();
        fontHighscore.dispose();

    }

    @Override
    public boolean keyDown(int keycode) {

        if(keycode == Input.Keys.BACK){
            game.setScreen(new StartScreen(game,adsController,gameShareInterface));

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
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

        game.setScreen(new StartScreen(game, adsController, gameShareInterface));
        tradsoundtrack.stop();


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



    private static int counter = 1;

    private static Pixmap getScreenshot(int x, int y, int w,int h, boolean ydown){
        final Pixmap pixmap = ScreenUtils.getFrameBufferPixmap(x,y,w,h);

        if(ydown){
            ByteBuffer pixels = pixmap.getPixels();
            int numBytes = w* h* 4;
            byte[] lines = new byte[numBytes];
            int numBytesPerLine = w* 4;
            for(int i = 0; i <h;i++){
                pixels.position((h-i-1)*numBytesPerLine);
                pixels.get(lines,i*numBytesPerLine,numBytesPerLine);
            }
            pixels.clear();
            pixels.put(lines);
            pixels.rewind();
        }

        return pixmap;
    }

}
