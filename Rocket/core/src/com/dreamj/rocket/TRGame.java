package com.dreamj.rocket;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.dreamj.rocket.Screen.SplashScreen;
import com.dreamj.rocket.TRHelper.AssetLoader;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;

public class TRGame extends Game implements ApplicationListener {

	//variables
	private AdsController adsController;
	private GameShareInterface gameShareInterface;

	/**
	 * TRGames constructor
	 * @param adsController
	 * @param gameShareInterface
	 */
	public TRGame(AdsController adsController, GameShareInterface gameShareInterface) {

		this.adsController = adsController;//control the ads
		this.gameShareInterface = gameShareInterface;// control the share

	}
	@Override
	public void create() {

		AssetLoader.load();//load assets for the splash screen
		setScreen(new SplashScreen(this, adsController,gameShareInterface));//go to splash screen
	}

	@Override
	public void dispose() {
		super.dispose();
		AssetLoader.dispose();// dispose the assetloader after finished
	}
}
