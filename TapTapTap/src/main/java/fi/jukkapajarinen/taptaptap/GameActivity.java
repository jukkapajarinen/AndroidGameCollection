package fi.jukkapajarinen.taptaptap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.InterstitialAd;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import static fi.jukkapajarinen.taptaptap.MainActivity.interstitialAd;

public class GameActivity extends AppCompatActivity {

  private InterstitialAd mInterstitialAd;
  private SharedPreferences mPrefs;
  private boolean mAdsRemoved;
  private final Random mRandom = new Random();
  private final int[] BOARD_SIZES = new int[] { 4, 8, 16, 32 };
  private final int[] SPEEDS = new int[] { 1000, 750, 500, 250 };
  private final int[] COLORS = new int[]{ 0xff1f2d3b, 0xfff5f5f5 };
  private int mBoardSize;
  private boolean mMovesEnabled;
  private boolean mTimerEnabled;
  private int mGameSpeed;
  private int mElapsedMoves;
  private TextView mElapsedMovesTextView;
  private GridLayout mGameGridContainer;
  private ArrayList<String> mDarkenedBtnTags;
  private long mLastPauseTime;
  private Chronometer mChronometer;
  private Handler mTimer2Handler;
  private boolean mPaused;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Init ad and check for sharedPreferences and adsRemoved
    mInterstitialAd = interstitialAd();
    mPrefs = getSharedPreferences(getResources().getString(R.string.shared_preferences), Context.MODE_PRIVATE);
    mAdsRemoved = mPrefs.getBoolean("prefAdsRemoved", false);

    // Set layout and orientation
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    setContentView(R.layout.activity_game);
    setSupportActionBar((Toolbar) findViewById(R.id.appToolbar));
    getSupportActionBar().setDisplayShowTitleEnabled(false);
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    // Try to set statusBar color
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      getWindow().setStatusBarColor(0xff1f2d3b);
    }

    // Read settings from SharedPreferences
    mBoardSize = BOARD_SIZES[mPrefs.getInt("prefBoardSizeChoice",1)];
    mMovesEnabled = mPrefs.getBoolean("prefMovesEnabled", true);
    mTimerEnabled = mPrefs.getBoolean("prefTimerEnabled", true);
    mGameSpeed = SPEEDS[mPrefs.getInt("prefGameSpeedChoice", 0)];

    // Populate colorTileGrid with Buttons
    mGameGridContainer = (GridLayout) findViewById(R.id.gameGrid);
    mGameGridContainer.removeAllViews();
    for(int i = 0 ; i < mBoardSize ; i++) {
      for(int j = 0 ; j < mBoardSize ; j++) {
        String tag = i + ";" + j;
        Button btn = new Button(this);
        btn.setTag(tag);
        btn.setBackgroundColor(COLORS[1]);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
        params.width = params.height = (getResources().getDisplayMetrics().widthPixels - Math.round(56 * MainActivity.resources().getDisplayMetrics().density)) / mBoardSize;
        params.setMargins(2, 2, 2, 2);
        btn.setLayoutParams(params);
        mGameGridContainer.addView(btn);
      }
    }

    // Init boardSizeText and elapsedMoves
    mElapsedMoves = 0;
    mElapsedMovesTextView = (TextView) findViewById(R.id.elapsedMovesTextView);
    mElapsedMovesTextView.setText(getResources().getString(R.string.content_moves_text) + ": " + mElapsedMoves);
    if(!mMovesEnabled) mElapsedMovesTextView.setVisibility(View.INVISIBLE);

    mLastPauseTime = SystemClock.elapsedRealtime();
    mChronometer = (Chronometer) findViewById(R.id.elapsedTimeChronometer);
    if(!mTimerEnabled) mChronometer.setVisibility(View.INVISIBLE);
    mChronometer.setBase(mLastPauseTime);
    mChronometer.start();
    mPaused = false;

    // Create arraylist for darkened buttons
    mDarkenedBtnTags = new ArrayList<>();

    // Set timer callback
    mTimer2Handler = new Handler();
    mTimer2Handler.postDelayed(new Runnable() {
      @Override
      public void run() {
        if(!mPaused) {
          createNextDarkButton();
          mGameSpeed = mGameSpeed > 11 ? mGameSpeed - 10 : 1;
          mTimer2Handler.postDelayed(this, mGameSpeed);
        }
      }
    }, mGameSpeed);

    // Set event listeners for all layout widgets
    setOnClickListeners();
  }

  private void createNextDarkButton() {
    if(mDarkenedBtnTags.size() < 5) {
      String tag = mRandom.nextInt(mBoardSize)+";"+mRandom.nextInt(mBoardSize);
      while(mDarkenedBtnTags.contains(tag)) {
        tag = mRandom.nextInt(mBoardSize)+";"+mRandom.nextInt(mBoardSize);
      }
      mDarkenedBtnTags.add(tag);

      Button btn = (Button) mGameGridContainer.findViewWithTag(tag);
      btn.setBackgroundColor(COLORS[0]);
    } else {
      createGameOverDialog();
    }
  }

  private void createGameOverDialog() {
    mPaused = true;
    mChronometer.stop();

    String title = mElapsedMoves > 10 ? getResources().getString(R.string.dia_title_game_over) : getResources().getString(R.string.dia_title_game_over_2);
    String message = getResources().getString(R.string.content_moves_text) + ": " + mElapsedMoves;
    GameOverDialog gameOverDialog = new GameOverDialog(GameActivity.this, title, message);
    gameOverDialog.show();
    gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff1f2d3b);
    gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff1f2d3b);
    gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff1f2d3b);
  }

  @Override
  protected void onPause() {
    super.onPause();
    finish();
  }

  @Override
  public void onBackPressed() {
    super.onBackPressed();
    if(!mAdsRemoved && mInterstitialAd.isLoaded()) mInterstitialAd.show();
  }

  private void setOnClickListeners() {
    for(int i = 0 ; i < mBoardSize ; i++) {
      for (int j = 0; j < mBoardSize; j++) {
        final Button btn = (Button) mGameGridContainer.findViewWithTag(i+";"+j);
        btn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            if(mPaused) {
              Toast.makeText(GameActivity.this, GameActivity.this.getResources().getText(R.string.toast_paused), Toast.LENGTH_SHORT).show();
            } else {
              if(mDarkenedBtnTags.contains(btn.getTag().toString())) {
                btn.setBackgroundColor(COLORS[1]);
                mElapsedMoves++;
                mElapsedMovesTextView.setText(getResources().getString(R.string.content_moves_text) + ": " + mElapsedMoves);
                mDarkenedBtnTags.remove(btn.getTag().toString());
              } else {
                createGameOverDialog();
              }
            }
          }
        });
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);
    getMenuInflater().inflate(R.menu.menu_game, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        finish();
        if(!mAdsRemoved && mInterstitialAd.isLoaded()) mInterstitialAd.show();
        return true;
      case R.id.menu_item_pause:
        if(!mPaused) {
          mLastPauseTime = SystemClock.elapsedRealtime();
          mChronometer.stop();
          mPaused = true;
          Toast.makeText(this, getResources().getText(R.string.toast_paused), Toast.LENGTH_SHORT).show();
        } else {
          mChronometer.setBase(mChronometer.getBase() + SystemClock.elapsedRealtime() - mLastPauseTime);
          mChronometer.start();
          mPaused = false;
          Toast.makeText(this, getResources().getText(R.string.toast_unpaused), Toast.LENGTH_SHORT).show();
        }
        return true;
      case R.id.menu_item_help:
        if(!mPaused) {
          Toast.makeText(this, getResources().getText(R.string.toast_help), Toast.LENGTH_SHORT).show();
        }
        return true;
    }
    return false;
  }
}
