package com.dreamj.rocket.Screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dreamj.rocket.TRGame;
import com.dreamj.rocket.TRHelper.AssetLoader;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;
import com.dreamj.rocket.tweenaccessor.SpriteAccessor;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.TweenEquations;
import aurelienribon.tweenengine.TweenManager;

/**
 * Created by Caliph Cole on 05/21/2015.
 */
public class SplashScreen implements Screen {

    private TweenManager manager;
    private SpriteBatch batcher;
    private Sprite sprite;
    private TRGame game;

    private AdsController adsController;
    private GameShareInterface gameShareInterface;


    public SplashScreen(TRGame game, AdsController adsController, GameShareInterface gameShareInterface) {

        this.game = game;
        this.adsController = adsController;
        this.gameShareInterface = gameShareInterface;
    }

    @Override
    public void show() {

        adsController.hideBannerAd();//hide ad

        sprite = new Sprite(AssetLoader.logo);//load the image form the splash screen
        sprite.setColor(1, 1, 1, 0);

        //Scale image to screen size
        float width = Gdx.graphics.getWidth();
        float height = Gdx.graphics.getHeight();
        float desiredWidth = width * .7f;
        float scale = desiredWidth / sprite.getWidth();

        sprite.setSize(sprite.getWidth() * scale, sprite.getHeight() * scale);
        sprite.setPosition((width / 2) - (sprite.getWidth() / 2), (height / 2)
                - (sprite.getHeight() / 2));
        setupTween();// fade splash screen image,listen for splash image fade

        batcher = new SpriteBatch();//instantiate the sprtiebatch
    }

    private void setupTween() {

        Tween.registerAccessor(Sprite.class, new SpriteAccessor());
        manager = new TweenManager();

        //Got to starts screen after splash screen fades
        TweenCallback cb = new TweenCallback() {
            @Override
            public void onEvent(int type, BaseTween<?> source) {
                game.setScreen(new StartScreen(game, adsController,gameShareInterface));
                //dispose();
            }
        };

        //Fade in the splash image and fade out
        Tween.to(sprite, SpriteAccessor.ALPHA, 1.3f).target(1)
                .ease(TweenEquations.easeInOutQuad).repeatYoyo(1, .4f)
                .setCallback(cb).setCallbackTriggers(TweenCallback.COMPLETE)
                .start(manager);
    }

    @Override
    public void render(float delta) {

        manager.update(delta);
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batcher.begin();
        sprite.draw(batcher);
        batcher.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {


    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        //dispose();
    }

    @Override
    public void dispose() {

        batcher.dispose();
    }
}
