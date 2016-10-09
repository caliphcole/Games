package com.dreamj.rocket.android;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.Pixmap;
import com.dreamj.rocket.TRGame;
import com.dreamj.rocket.admob.AdsController;
import com.dreamj.rocket.googleshare.GameShareInterface;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;

public class AndroidLauncher extends AndroidApplication implements AdsController, GameShareInterface {

	//Ads Variables
	private static final String BANNER_AD_UNIT_ID ="ca-app-pub-7406673040779336/5698605404";// "ca-app-pub-3940256099942544/6300978111"
	private final String TAPPX_KEY = "/120940746/Pub-4769-Android-7998";

	private com.google.android.gms.ads.doubleclick.PublisherInterstitialAd adBanner = null;

	AdView bannerAd;



	View gameView;// View of the Game




	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		config.useWakelock = true;

		// Create a gameView and a bannerAd AdView
		gameView = initializeForView(new TRGame(this,this), config);

		setupAds();

		// Define the layout
		RelativeLayout layout = new RelativeLayout(this);

		layout.addView(gameView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

		/*RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);*/

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_TOP);

		layout.addView(bannerAd, params);

		/*RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT);
		params1.addRule(RelativeLayout.ALIGN_PARENT_TOP);*/

		//layout.addView(adBanner, params);

		setContentView(layout);


	}

	/**
	 * Setup ads
	 */
	public void setupAds() {
		bannerAd = new AdView(this);
		bannerAd.setVisibility(View.INVISIBLE);
		bannerAd.setBackgroundColor(0xff000000); // black
		bannerAd.setAdUnitId(BANNER_AD_UNIT_ID);
		bannerAd.setAdSize(AdSize.SMART_BANNER);
		adBanner = new com.google.android.gms.ads.doubleclick.PublisherInterstitialAd(this);

		//adBanner.setVisibility(View.INVISIBLE);
		//adBanner.setBackgroundColor(0xff000000); // black
		adBanner.setAdUnitId(TAPPX_KEY);
		//adBanner.setAdSizes(AdSize.SMART_BANNER);
	}

	/*
	Two methods that handles the visibility of the ads
	 */

	@Override
	public void showBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setVisibility(View.VISIBLE);
				AdRequest.Builder builder = new AdRequest.Builder();
				AdRequest ad = builder.build();
				bannerAd.loadAd(ad);

				//adBanner.setVisibility(View.VISIBLE);


			}
		});


	}

	@Override
	public void hideBannerAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				bannerAd.setVisibility(View.INVISIBLE);
				//adBanner.setVisibility(View.INVISIBLE);
			}
		});
	}

	@Override
	public void showInInterstitialAd() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				PublisherAdRequest.Builder builder1 = new PublisherAdRequest.Builder();
				PublisherAdRequest ad1 = builder1.build();
				adBanner.loadAd(ad1);
				adBanner.show();
			}
		});
	}


			/**
			 * Method that handles the sharing of the endscreen score board
			 * @param pix
			 */
			@Override
			public void shareScore(Pixmap pix) {


				Bitmap bitmap = Bitmap.createBitmap(getWindow().getDecorView().getRootView().getWidth(), getWindow().getDecorView().getRootView().getHeight(), Bitmap.Config.ARGB_8888);
				bitmap.copyPixelsFromBuffer(pix.getPixels());
				pix.dispose();

				try {//Store the screen shot
					String path = MediaStore.Images.Media.insertImage(getContext().getContentResolver(), bitmap, "image", null);

					Intent sharing = new Intent(android.content.Intent.ACTION_SEND);
					sharing.setType("image/png");
					sharing.putExtra(android.content.Intent.EXTRA_SUBJECT, "Star Rocket");
					sharing.putExtra(android.content.Intent.EXTRA_TEXT, "You Can't Beat Me !!!!\n Download: play.google.com");
					sharing.putExtra(android.content.Intent.EXTRA_STREAM, Uri.parse(path));
					startActivity(Intent.createChooser(sharing, "Share High Score via"));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}


		}
