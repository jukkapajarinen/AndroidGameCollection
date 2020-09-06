package fi.jukkapajarinen.floodit;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import static fi.jukkapajarinen.floodit.MainActivity.interstitialAd;

public class GameActivity extends AppCompatActivity {

  private InterstitialAd mInterstitialAd;
  private SharedPreferences mPrefs;
  private boolean mAdsRemoved;
  private final Random mRandom = new Random();
  private final int[] BOARD_SIZES = new int[] { 4, 8, 16, 32 };
  private final int[] COLORS = new int[]{ 0xffd9534f, 0xff5cb85c, 0xff209cee, 0xfff0ad4e, 0xffad85d2 };
  private int mBoardSizeChoice;
  private int mBoardSize;
  private int mColorsSize;
  private boolean mMovesEnabled;
  private boolean mTimerEnabled;
  private int mElapsedMoves;
  private int mAllowedMoves;
  private TextView mElapsedMovesTextView;
  private GridLayout mGameGridContainer;
  private HashMap<String, Integer> mButtonColorsMap;
  private ArrayList<String> mFilledAreaBtnsTags;
  private ArrayList<Button> mActionBtns;
  private int mCurrentActionBtnId;
  private long mLastPauseTime;
  private Chronometer mChronometer;
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
      getWindow().setStatusBarColor(0xff209cee);
    }

    // Read settings from SharedPreferences
    mBoardSizeChoice = mPrefs.getInt("prefBoardSizeChoice",1);
    mBoardSize = BOARD_SIZES[mBoardSizeChoice];
    mColorsSize = 5; // 5 possible colors atm
    mMovesEnabled = mPrefs.getBoolean("prefMovesEnabled", true);
    mTimerEnabled = mPrefs.getBoolean("prefTimerEnabled", true);
    mAllowedMoves = (int) (mBoardSize * 1.5);

    // Initialize all data structures
    mButtonColorsMap = new HashMap<>();
    mFilledAreaBtnsTags = new ArrayList<>();
    mActionBtns = new ArrayList<>();

    // Populate colorTileGrid with Buttons
    mGameGridContainer = (GridLayout) findViewById(R.id.gameGrid);
    mGameGridContainer.removeAllViews();
    for(int i = 0 ; i < mBoardSize ; i++) {
      for(int j = 0 ; j < mBoardSize ; j++) {
        String tag = i + ";" + j;
        int random = mRandom.nextInt(5);
        int color = COLORS[random];
        if(tag.equals("0;0")) mCurrentActionBtnId = random;
        mButtonColorsMap.put(tag, color);
        Button btn = new Button(this);
        btn.setTag(tag);
        btn.setBackgroundColor(color);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(i), GridLayout.spec(j));
        params.width = params.height = (getResources().getDisplayMetrics().widthPixels - Math.round(56 * MainActivity.resources().getDisplayMetrics().density)) / mBoardSize;
        btn.setLayoutParams(params);
        mGameGridContainer.addView(btn);
      }
    }

    // Read ActionBtns to memory
    for(int i = 0; i < mColorsSize ; i++) {
      Button btn = (Button) findViewById(getResources().getIdentifier("action_"+i, "id", getPackageName()));
      mActionBtns.add(btn);
    }

    // Init boardSizeText and elapsedMoves
    mElapsedMoves = 0;
    mElapsedMovesTextView = (TextView) findViewById(R.id.elapsedMovesTextView);
    mElapsedMovesTextView.setText(getResources().getString(R.string.content_moves_text) + ": " + mElapsedMoves + " / " + mAllowedMoves);
    if(!mMovesEnabled) mElapsedMovesTextView.setVisibility(View.INVISIBLE);

    mLastPauseTime = SystemClock.elapsedRealtime();
    mChronometer = (Chronometer) findViewById(R.id.elapsedTimeChronometer);
    if(!mTimerEnabled) mChronometer.setVisibility(View.INVISIBLE);
    mChronometer.setBase(mLastPauseTime);
    mChronometer.start();
    mPaused = false;

    // Add First button to filledAreaBtnsTags
    highLightActiveActionButton();
    mFilledAreaBtnsTags.add("0;0");
    updateFilledAreaButtons();

    // Set event listeners for all layout widgets
    setOnClickListeners();
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

  private void updateFilledAreaButtons() {
    ArrayList<String> tagsCopy = new ArrayList<>(mFilledAreaBtnsTags);
    for(String tag : tagsCopy) {
      int row = Integer.parseInt(tag.split(";")[0]);
      int col = Integer.parseInt(tag.split(";")[1]);

      // Check top of button
      if((row-1 >= 0) && mButtonColorsMap.get((row-1)+";"+col) == COLORS[mCurrentActionBtnId]) {
        if(!mFilledAreaBtnsTags.contains((row-1)+";"+col)) {
          mFilledAreaBtnsTags.add((row-1)+";"+col);
          updateFilledAreaButtons();
        }
      }

      // Check right of button
      if((col+1 < mBoardSize) && mButtonColorsMap.get(row+";"+(col+1)) == COLORS[mCurrentActionBtnId]) {
        if(!mFilledAreaBtnsTags.contains(row+";"+(col+1))) {
          mFilledAreaBtnsTags.add(row+";"+(col+1));
          updateFilledAreaButtons();
        }
      }

      // Check bottom of button
      if((row+1 < mBoardSize) && mButtonColorsMap.get((row+1)+";"+col) == COLORS[mCurrentActionBtnId]) {
        if(!mFilledAreaBtnsTags.contains((row+1)+";"+col)) {
          mFilledAreaBtnsTags.add((row+1)+";"+col);
          updateFilledAreaButtons();
        }
      }

      // Check left of button
      if((col-1 >= 0) && mButtonColorsMap.get(row+";"+(col-1)) == COLORS[mCurrentActionBtnId]) {
        if(!mFilledAreaBtnsTags.contains(row+";"+(col-1))) {
          mFilledAreaBtnsTags.add(row+";"+(col-1));
          updateFilledAreaButtons();
        }
      }
    }
    System.out.println("SIZE: "+mFilledAreaBtnsTags.size());
  }

  private void changeFilledAreaColors() {
    for(String tag : mFilledAreaBtnsTags) {
      Button btn = (Button) mGameGridContainer.findViewWithTag(tag);
      btn.setBackgroundColor(COLORS[mCurrentActionBtnId]);
    }
  }

  private void highLightActiveActionButton() {
    for(Button action : mActionBtns) action.setBackgroundColor(Color.TRANSPARENT);
    mActionBtns.get(mCurrentActionBtnId).setBackgroundColor(0xff1a2531);
  }

  private void setOnClickListeners() {

    // Set ActionBtns listeners
    for(final Button btn : mActionBtns) {
      btn.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
          if(mPaused) {
            Toast.makeText(GameActivity.this, GameActivity.this.getResources().getText(R.string.toast_paused), Toast.LENGTH_SHORT).show();
          } else {

            // Update mElapsedMoves and TextView
            mElapsedMoves++;
            mElapsedMovesTextView.setText(getResources().getString(R.string.content_moves_text) + ": " + mElapsedMoves + " / " + mAllowedMoves);

            // Change currentActionId and highlight the button
            String resourceIdName = getResources().getResourceName(btn.getId());
            resourceIdName = resourceIdName.substring(resourceIdName.indexOf("action_"));
            mCurrentActionBtnId = Integer.parseInt(resourceIdName.substring(7));
            highLightActiveActionButton();

            // Change colors of current Flow
            changeFilledAreaColors();
            updateFilledAreaButtons();

            // Check for winning move or if elapsedMoves is too much
            if(mFilledAreaBtnsTags.size() == mBoardSize * mBoardSize || mElapsedMoves >= mAllowedMoves) {
              // First be pessimistic about elapsedmoves >= allowedMoves
              String title = getResources().getString(R.string.dia_title_game_over_2);
              String message = getResources().getString(R.string.content_game_over_2);

              // Check if we came here because of game is completed
              if(mFilledAreaBtnsTags.size() == mBoardSize * mBoardSize) {
                title = getResources().getString(R.string.dia_title_game_over);
                message = getResources().getString(R.string.content_game_over);
              }

              // Create and Show GameOverDialog
              GameOverDialog gameOverDialog = new GameOverDialog(GameActivity.this, title, message);
              gameOverDialog.show();
              gameOverDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(0xff209cee);
              gameOverDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(0xff209cee);
              gameOverDialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(0xff209cee);
            }
          }
        }
      });
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
