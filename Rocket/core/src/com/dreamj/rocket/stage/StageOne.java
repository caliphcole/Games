package com.dreamj.rocket.stage;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.dreamj.rocket.TRGame;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;
import com.dreamj.rocket.render.Renderer;

/**
 * Created by Caliph Cole on 05/21/2015.
 */
public class StageOne extends Stage{

    private Renderer render;

    private boolean storeCollision =false;// used to check for collision


    public StageOne(TRGame game,GameShareInterface gameShareInterface, AdsController adsController){

        render = new Renderer();//instantiate render class
        render.setup(game, gameShareInterface, adsController);

    }
    @Override
    public void act() {
        super.act();
    }

    @Override
    public void draw() {
        super.draw();

        render.render();// render to screen
        storeCollision = render.checkcollision();// update storecollision
    }

    public boolean getStoredCollision(){
        return storeCollision;
    }

    public int getScore(){

        return render.getCount();
    }
    public Viewport resize(int width, int height) {
       return render.resize(width, height);
    }

    public void hide(){
        render.hide();
    }
    public void dispose(){
        render.dispose();
    }

}
