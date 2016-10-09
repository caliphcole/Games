package com.dreamj.rocket.Screen;

import com.badlogic.gdx.Screen;
import com.dreamj.rocket.TRGame;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;
import com.dreamj.rocket.stage.StageOne;

/**
 * Created by Caliph Cole on 05/21/2015.
 */
public class MainGameScreen implements Screen {

    private StageOne stage;
    private TRGame game;
    private AdsController adsController;
    private GameShareInterface gameShareInterface;

    public MainGameScreen(TRGame game, AdsController adsController, GameShareInterface gameShareInterface) {
        this.game = game;
        this.stage = new StageOne(game,gameShareInterface, adsController);
        this.adsController = adsController;
        this.gameShareInterface = gameShareInterface;


    }

    @Override
    public void show() {

        adsController.hideBannerAd();

    }

    @Override
    public void render(float delta) {

        stage.draw();
        stage.act(delta);

        if (stage.getStoredCollision()) {
            game.setScreen(new EndScreen(game, stage.getScore(), adsController, gameShareInterface));
        }
        // Gdx.input.setInputProcessor(this);
        // Gdx.input.setCatchBackKey(true);
    }

    @Override
    public void resize(int width, int height) {

        stage.resize(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        dispose();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
