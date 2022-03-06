package fi.jukkapajarinen.supersnake;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.gms.ads.InterstitialAd;

import fi.jukkapajarinen.androidgameloop.GameActivity;

import static fi.jukkapajarinen.supersnake.MainActivity.interstitialAd;

public class MyGameActivity extends GameActivity {

  private InterstitialAd mInterstitialAd;
  private SharedPreferences mPrefs;
  private boolean mAdsRemoved;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Check for sharedPreferences and adsRemoved
    mInterstitialAd = interstitialAd();
    mPrefs = getSharedPreferences(getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    MyGame myGame = new MyGame(this);
    start(myGame);
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if(!mAdsRemoved && mInterstitialAd.isLoaded()) mInterstitialAd.show();
  }
}
