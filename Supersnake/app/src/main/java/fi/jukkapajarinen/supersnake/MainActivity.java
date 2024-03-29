package fi.jukkapajarinen.supersnake;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.util.Locale;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

  public static InterstitialAd interstitialAd() { return mInterstitialAd; }
  private static InterstitialAd mInterstitialAd;
  private final Random mRandom = new Random();
  private SharedPreferences mPrefs;
  private boolean mAdsRemoved;
  private TextView mTitle;
  private TextView mSubTitle;
  private FloatingActionButton mPlayFAB;
  private FloatingActionButton mSettingsFAB;
  private FloatingActionButton mShareFAB;
  private AdView mAdView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Initializing contextual things
    mPrefs = getSharedPreferences(getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    // Set layout and orientation
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_main);

    // Try to set statusBar color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(0xff209cee);
    }

    // Init admob advertisements
    if (!mAdsRemoved) {
      MobileAds.initialize(this, getResources().getString(R.string.admob_publisher_id));
      mInterstitialAd = new InterstitialAd(this);
      mInterstitialAd.setAdUnitId(getResources().getString(R.string.admob_interstitial_unit_id));
      mAdView = (AdView) findViewById(R.id.adView);
    }

    // Change title font typeface (has to be done programmatically)
    mTitle = (TextView) findViewById(R.id.appTitle);
    String fontName = Locale.getDefault().getLanguage().equals("zh") ? "fonts/zcoolhappy.ttf" : "fonts/troika.ttf";
    mTitle.setTypeface(Typeface.createFromAsset(getAssets(), fontName));

    // Initialize subTitle from layout
    mSubTitle = (TextView) findViewById(R.id.subTitle);

    // Initialize FABs from layout
    mPlayFAB = (FloatingActionButton) findViewById(R.id.playFAB);
    mSettingsFAB = (FloatingActionButton) findViewById(R.id.settingsFAB);
    mShareFAB = (FloatingActionButton) findViewById(R.id.shareFAB);

    // Set event listeners for layout widgets
    setOnClickListeners();
  }

  @Override
  protected void onResume() {
    super.onResume();

    // Build or refresh interstitial and banner ads
    if (!mAdsRemoved) {
      mInterstitialAd.loadAd(new AdRequest.Builder().build());
      mAdView.loadAd(new AdRequest.Builder().build());
    }
  }

  private void setOnClickListeners() {
    // subtitle hyperlink
    mSubTitle.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent authorIntent = new Intent(Intent.ACTION_VIEW);
        authorIntent.setData(Uri.parse("https://jukkapajarinen.bitbucket.io/"));
        startActivity(authorIntent);
      }
    });

    // playFAB starts the MyGameActivity
    mPlayFAB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent playIntent = new Intent(MainActivity.this, MyGameActivity.class);
        playIntent.putExtra("prefPlayerModeChoice", mPrefs.getInt("prefPlayerModeChoice", 0));
        startActivity(playIntent);
      }
    });

    // settingsFAB opens the settingsDialog
    mSettingsFAB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        SettingsDialog settingsDialog = new SettingsDialog(MainActivity.this);
        settingsDialog.show();
        settingsDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xffd9534f);
        settingsDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xffd9534f);
        settingsDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xffd9534f);
      }
    });

    // shareFAB opens shareIntent
    mShareFAB.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.app_store_link));
        startActivity(shareIntent);
      }
    });
  }
}
