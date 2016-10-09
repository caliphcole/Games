package com.dreamj.rocket.TRHelper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by Caliph Cole on 05/21/2015.
 */
public class AssetLoader {


    public static TextureRegion logo,startbackground ,mainbackground;
    public static Texture logoTexture,startscreenimage, main;


    public static void load() {

        logoTexture = new Texture(Gdx.files.internal("data/logo.png"));
        logoTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        logo = new TextureRegion(logoTexture, 0, 0, 512, 184);

        startscreenimage = new Texture(Gdx.files.internal("startbackground.jpg"));
        startscreenimage.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        startbackground = new TextureRegion(startscreenimage,0,0,480,800);

        main = new Texture(Gdx.files.internal("mainbackground.jpg"));
        main.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        mainbackground = new TextureRegion(main,0,0,480,800);



    }

    public static void dispose() {
        // We must dispose of the texture when we are finished.
        logoTexture.dispose();
        startscreenimage.dispose();
        main.dispose();

    }
}
